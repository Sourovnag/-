package com.example.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.network.GeminiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val userDao = db.userDao()
    private val requestDao = db.bloodRequestDao()
    private val historyDao = db.donationHistoryDao()
    private val messageDao = db.messageDao()
    private val notificationDao = db.notificationDao()

    // --- State Managers ---
    val currentScreen = MutableStateFlow("home") // "home", "find_blood", "become_donor", "emergency", "requests", "dashboard", "about", "contact", "profile", "settings", "notifications", "chat"
    val language = MutableStateFlow("BN") // "BN" or "EN"
    val isDarkMode = MutableStateFlow(false)

    // Current Authenticated User (initially null or Super Admin for easy testing)
    val currentUser = MutableStateFlow<UserEntity?>(null)

    // Data Flows
    val allDonors = userDao.getAllDonors().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val allVolunteers = userDao.getAllVolunteers().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val allRequests = requestDao.getAllRequests().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val allNotifications = notificationDao.getAllNotifications().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Emergency button controls (Admin configurable)
    val isEmergencyButtonVisible = MutableStateFlow(true)

    // Search and Filtering Parameters
    val searchBloodGroup = MutableStateFlow("All")
    val searchArea = MutableStateFlow("All")
    val searchOnlyVerified = MutableStateFlow(false)
    val searchOnlyAvailable = MutableStateFlow(false)

    // Chat State
    val activeChatRecipient = MutableStateFlow<UserEntity?>(null)
    val chatMessages = activeChatRecipient.flatMapLatest { recipient ->
        if (recipient == null) {
            flowOf(emptyList())
        } else {
            val userSelf = currentUser.value?.name ?: "Me"
            messageDao.getChatHistory(userSelf, recipient.name)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Temp Auth Fields (for UI binding)
    val authPhone = MutableStateFlow("")
    val authOtp = MutableStateFlow("")
    val authOtpSent = MutableStateFlow(false)
    val authError = MutableStateFlow("")

    init {
        // Populate DB with rich default mock data if empty
        viewModelScope.launch(Dispatchers.IO) {
            checkAndPrepopulateDatabase()
        }
    }

    private suspend fun checkAndPrepopulateDatabase() {
        val count = userDao.getAllUsers().first().size
        if (count == 0) {
            Log.d("MainViewModel", "Database is empty. Prepopulating default mock values...")
            
            // Insert mock users
            MockData.DEFAULT_USERS.forEach { user ->
                userDao.insertUser(user)
            }

            // Insert mock blood requests
            MockData.DEFAULT_REQUESTS.forEach { req ->
                requestDao.insertRequest(req)
            }

            // Insert initial notifications
            notificationDao.insertNotification(
                NotificationEntity(
                    title = "স্বাগতম প্ল্যাটফর্মে!",
                    message = "রক্ত দিতে প্রস্তুত আমরা প্ল্যাটফর্মে আপনাকে স্বাগতম। কুমিল্লা জেলার সকল রক্তদাতাদের সাথে দ্রুত যোগাযোগে এটি সাহায্য করবে।",
                    type = "INFO"
                )
            )
            notificationDao.insertNotification(
                NotificationEntity(
                    title = "জরুরি রক্তের রিকোয়েস্ট!",
                    message = "কুমিল্লা মেডিকেল কলেজ হাসপাতালে থ্যালাসেমিয়া রোগীর জন্য জরুরি A+ রক্ত প্রয়োজন।",
                    type = "EMERGENCY"
                )
            )

            // Auto log in Super Admin by default for testing richness immediately
            currentUser.value = MockData.DEFAULT_USERS.firstOrNull()
        } else {
            // Auto-login the Super Admin even if database is already populated
            val admins = MockData.DEFAULT_USERS.filter { it.role == "Super Admin" }
            if (admins.isNotEmpty()) {
                val adminFromDb = userDao.getUserById(admins.first().id)
                if (adminFromDb != null) {
                    currentUser.value = adminFromDb
                }
            }
        }
    }

    // --- Actions ---

    fun setScreen(screen: String) {
        currentScreen.value = screen
    }

    fun toggleLanguage() {
        language.value = if (language.value == "BN") "EN" else "BN"
    }

    fun toggleTheme() {
        isDarkMode.value = !isDarkMode.value
    }

    fun logout() {
        currentUser.value = null
        setScreen("home")
    }

    // Role-switching helper (for debug role evaluation)
    fun switchRole(role: String) {
        val user = currentUser.value
        if (user != null) {
            val updated = user.copy(role = role)
            viewModelScope.launch(Dispatchers.IO) {
                userDao.insertUser(updated)
                currentUser.value = updated
            }
        } else {
            // Create a temporary user with that role
            val tempUser = UserEntity(
                id = "01700000000",
                name = "পরীক্ষামূলক ব্যবহারকারী",
                age = 25,
                bloodGroup = "B+",
                phone = "01700000000",
                area = "Cumilla Sadar",
                role = role,
                verificationStatus = if (role == "Donor") "Pending Verification" else "Verified"
            )
            viewModelScope.launch(Dispatchers.IO) {
                userDao.insertUser(tempUser)
                currentUser.value = tempUser
            }
        }
    }

    // Simulated Phone OTP Authentication
    fun sendOtp(phone: String) {
        if (phone.length < 11) {
            authError.value = if (language.value == "BN") "সঠিক ১১ ডিজিটের ফোন নম্বর দিন" else "Enter a valid 11-digit phone number"
            return
        }
        authOtpSent.value = true
        authError.value = ""
    }

    fun verifyOtp(phone: String, otp: String) {
        if (otp != "1234" && otp.isNotEmpty()) {
            authError.value = if (language.value == "BN") "ভুল ওটিপি (টেস্ট কোড: 1234)" else "Invalid OTP (Use test code: 1234)"
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val existing = userDao.getUserById(phone)
            if (existing != null) {
                currentUser.value = existing
            } else {
                // Register a new Donor
                val newUser = UserEntity(
                    id = phone,
                    name = "রক্তদাতা-" + phone.takeLast(4),
                    age = 25,
                    bloodGroup = "O+",
                    phone = phone,
                    area = "Cumilla Sadar",
                    upazila = "Cumilla Sadar (কুমিল্লা সদর)",
                    role = "Donor",
                    verificationStatus = "Pending Verification",
                    photoId = (1..8).random()
                )
                userDao.insertUser(newUser)
                currentUser.value = newUser
            }
            authOtpSent.value = false
            authOtp.value = ""
            authPhone.value = ""
            authError.value = ""
            
            // Navigate to Dashboard/Home
            withContext(Dispatchers.Main) {
                setScreen("dashboard")
            }
        }
    }

    // Google/Facebook Login simulation
    fun loginSocial(provider: String) {
        val randomPhone = "01" + (10000000..99999999).random().toString()
        viewModelScope.launch(Dispatchers.IO) {
            val newUser = UserEntity(
                id = randomPhone,
                name = "সামাজিক ব্যবহারকারী (" + provider + ")",
                age = 24,
                bloodGroup = "A+",
                phone = randomPhone,
                area = "Cumilla Sadar",
                upazila = "Cumilla Sadar (কুমিল্লা সদর)",
                role = "Donor",
                verificationStatus = "Verified",
                photoId = (1..8).random()
            )
            userDao.insertUser(newUser)
            currentUser.value = newUser
            withContext(Dispatchers.Main) {
                setScreen("dashboard")
            }
        }
    }

    // Submit New Donor Registration
    fun registerDonor(name: String, age: Int, bloodGroup: String, area: String, phone: String, photoId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val newUser = UserEntity(
                id = phone,
                name = name,
                age = age,
                bloodGroup = bloodGroup,
                phone = phone,
                area = area.substringBefore(" ("),
                upazila = area,
                role = "Donor",
                verificationStatus = "Pending Verification",
                photoId = photoId
            )
            userDao.insertUser(newUser)
            currentUser.value = newUser
            
            // Create a notification
            notificationDao.insertNotification(
                NotificationEntity(
                    title = "রক্তদাতা নিবন্ধন সফল!",
                    message = "$name এর রক্তদাতা নিবন্ধন পেন্ডিং অবস্থায় রয়েছে। শীঘ্রই ভলান্টিয়ার দ্বারা যাচাই করা হবে।",
                    type = "VERIFICATION"
                )
            )

            withContext(Dispatchers.Main) {
                setScreen("dashboard")
            }
        }
    }

    // Submit Recipient Request Form with AI Integration
    fun submitBloodRequest(
        patientIssue: String,
        bloodGroup: String,
        quantity: String,
        hemoglobin: String,
        donationDate: String,
        donationTime: String,
        hospitalName: String,
        area: String,
        contactPhone: String,
        reference: String,
        isEmergency: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val newRequest = BloodRequestEntity(
                patientIssue = patientIssue,
                bloodGroup = bloodGroup,
                quantity = quantity,
                hemoglobin = hemoglobin.ifEmpty { null },
                donationDate = donationDate,
                donationTime = donationTime,
                hospitalName = hospitalName,
                area = area,
                contactPhone = contactPhone,
                reference = reference.ifEmpty { null },
                isEmergency = isEmergency,
                status = "Pending"
            )

            // Analyze request using Gemini API (or local heuristics fallback)
            val existing = allRequests.value
            val donors = allDonors.value
            val aiResult = GeminiService.analyzeBloodRequest(newRequest, existing, donors)

            val evaluatedRequest = newRequest.copy(
                priorityScore = aiResult.priorityScore,
                isSpam = aiResult.isSpam,
                status = if (aiResult.isSpam) "Rejected" else "Pending"
            )

            // Save Request to DB
            requestDao.insertRequest(evaluatedRequest)

            // Create appropriate system notifications
            if (aiResult.isSpam) {
                notificationDao.insertNotification(
                    NotificationEntity(
                        title = "স্প্যাম রিকোয়েস্ট সতর্কবার্তা!",
                        message = "একটি রক্ত রিকোয়েস্ট স্প্যাম হিসেবে চিহ্নিত হওয়ায় ব্লক করা হয়েছে: $patientIssue",
                        type = "INFO"
                    )
                )
            } else {
                // Send notifications to matches
                val title = if (isEmergency) "🚨 জরুরি রক্ত প্রয়োজন ($bloodGroup)" else "রক্তের সন্ধান ($bloodGroup)"
                val message = "$hospitalName এ রোগীর জন্য $quantity $bloodGroup রক্ত প্রয়োজন। এলাকা: $area"
                
                notificationDao.insertNotification(
                    NotificationEntity(
                        title = title,
                        message = message,
                        type = if (isEmergency) "EMERGENCY" else "MATCH"
                    )
                )

                // Match and recommend nearby matching donors instantly
                if (aiResult.recommendedDonorIds.isNotEmpty()) {
                    notificationDao.insertNotification(
                        NotificationEntity(
                            title = "স্মার্ট ম্যাচিং: $bloodGroup ডোনার পাওয়া গেছে!",
                            message = "${aiResult.recommendedDonorIds.size} জন ভেরিফাইড ডোনারের প্রোফাইল ম্যাচ করেছে। AI স্কোর: ${String.format("%.1f", aiResult.priorityScore)}",
                            type = "MATCH"
                        )
                    )
                }
            }

            withContext(Dispatchers.Main) {
                setScreen("requests")
            }
        }
    }

    // Super Admin: Manage Admin & Volunteers
    fun updateVolunteerStatus(userId: String, isVolunteer: Boolean, permissions: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            val user = userDao.getUserById(userId) ?: return@launch
            val updated = user.copy(
                role = if (isVolunteer) "Volunteer" else "Donor",
                badge = if (isVolunteer) "Volunteer" else "None",
                permissions = permissions.ifEmpty { "Approve donor,Verify phone,Approve request,Call donor" }
            )
            userDao.insertUser(updated)
            notificationDao.insertNotification(
                NotificationEntity(
                    title = "পদবী আপডেট করা হয়েছে",
                    message = "${user.name} এর পদবী আপডেট করে ${if (isVolunteer) "ভলান্টিয়ার" else "রক্তদাতা"} করা হয়েছে।",
                    type = "INFO"
                )
            )
        }
    }

    fun updateVolunteerPermissions(userId: String, permissions: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = userDao.getUserById(userId) ?: return@launch
            val updated = user.copy(permissions = permissions)
            userDao.insertUser(updated)
        }
    }

    // Volunteer & Admin Action: Verify/Reject Donor
    fun updateDonorVerification(userId: String, status: String, comment: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = userDao.getUserById(userId) ?: return@launch
            val updated = user.copy(
                verificationStatus = status,
                approvalComments = comment,
                badge = if (status == "Verified" && user.badge == "None") "Top Donor" else user.badge
            )
            userDao.insertUser(updated)

            val bnStatus = when(status) {
                "Verified" -> "ভেরিফাইড"
                "Rejected" -> "বাতিল"
                else -> "পেন্ডিং"
            }
            notificationDao.insertNotification(
                NotificationEntity(
                    title = "রক্তদাতার প্রোফাইল $bnStatus",
                    message = "${user.name} এর রক্তদাতার প্রোফাইলটি সফলভাবে $bnStatus করা হয়েছে।",
                    type = "VERIFICATION"
                )
            )
        }
    }

    // Volunteer & Admin Action: Approve/Reject Blood Request
    fun updateRequestStatus(requestId: Int, status: String) {
        viewModelScope.launch(Dispatchers.IO) {
            requestDao.updateRequestStatus(requestId, status)
            notificationDao.insertNotification(
                NotificationEntity(
                    title = "রক্তের রিকোয়েস্ট আপডেট",
                    message = "রিকোয়েস্ট আইডি #$requestId এর স্ট্যাটাস পরিবর্তন করে '$status' করা হয়েছে।",
                    type = "INFO"
                )
            )
        }
    }

    // Admin: Show/Hide Emergency button on homepage
    fun toggleEmergencyButtonVisibility(visible: Boolean) {
        isEmergencyButtonVisible.value = visible
    }

    // Donor Donation Timeline Management
    fun addDonationHistory(donorId: String, date: String, hospital: String, recipientName: String, notes: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val history = DonationHistoryEntity(
                donorId = donorId,
                date = date,
                hospitalName = hospital,
                patientName = recipientName,
                notes = notes,
                certificateId = "CERT-" + (100000..999999).random().toString()
            )
            historyDao.insertHistory(history)

            // Update user donor stats
            val user = userDao.getUserById(donorId)
            if (user != null) {
                val updated = user.copy(
                    totalDonations = user.totalDonations + 1,
                    lastDonationDate = date,
                    livesSaved = user.livesSaved + 1,
                    isAvailable = false, // unavailable for next 120 days
                    badge = if (user.totalDonations + 1 >= 10) "Hero" else if (user.totalDonations + 1 >= 5) "Top Donor" else user.badge
                )
                userDao.insertUser(updated)
                
                // If it's currently logged-in user, refresh their session
                if (currentUser.value?.id == donorId) {
                    currentUser.value = updated
                }
            }

            notificationDao.insertNotification(
                NotificationEntity(
                    title = "রক্তদান সম্পন্ন হয়েছে 🎉",
                    message = "ধন্যবাদ! আপনার রক্তদান রেকর্ড করা হয়েছে। জীবন বাঁচানোর সার্টিফিকেট জেনারেট করা হয়েছে।",
                    type = "INFO"
                )
            )
        }
    }

    fun getDonationHistory(donorId: String): Flow<List<DonationHistoryEntity>> {
        return historyDao.getHistoryForDonor(donorId)
    }

    // Chat functionality
    fun startChatWith(recipient: UserEntity) {
        activeChatRecipient.value = recipient
        setScreen("chat")
    }

    fun sendChatMessage(text: String) {
        val recipient = activeChatRecipient.value ?: return
        val sender = currentUser.value?.name ?: "রক্তদাতা"
        val senderRole = currentUser.value?.role ?: "Donor"
        if (text.trim().isEmpty()) return

        viewModelScope.launch(Dispatchers.IO) {
            val message = MessageEntity(
                senderName = sender,
                senderRole = senderRole,
                receiverName = recipient.name,
                text = text
            )
            messageDao.insertMessage(message)

            // Simulated Auto-Reply from the other user for rich interactivity
            kotlinx.coroutines.delay(1200)

            val replyTexts = listOf(
                "আসসালামু আলাইকুম, আমি রিকোয়েস্টটি দেখেছি। আমার রক্তের গ্রুপ মিলেছে। আমি রক্ত দিতে প্রস্তুত আছি।",
                "আমি বর্তমানে কুমিল্লা শহরেই আছি। রক্তদানের সময় এবং স্থানটি আমাকে একটু পরিষ্কার করে দিন।",
                "আপনার জরুরি রিকোয়েস্টের জন্য আমি ভলান্টিয়ারের সাথে যোগাযোগ করছি। ইনশাআল্লাহ দ্রুত ব্যবস্থা হবে।",
                "ধন্যবাদ যোগাযোগ করার জন্য। আমি বিগত ৩ মাসে কোনো রক্ত দিইনি, তাই আমি এলিজিবল আছি।"
            )
            val randomReply = replyTexts.random()

            val reply = MessageEntity(
                senderName = recipient.name,
                senderRole = recipient.role,
                receiverName = sender,
                text = randomReply
            )
            messageDao.insertMessage(reply)

            notificationDao.insertNotification(
                NotificationEntity(
                    title = "নতুন মেসেজ!",
                    message = "${recipient.name} থেকে নতুন মেসেজ এসেছে: '$randomReply'",
                    type = "INFO"
                )
            )
        }
    }

    // Notifications clear/read helpers
    fun markNotificationAsRead(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            notificationDao.markAsRead(id)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch(Dispatchers.IO) {
            notificationDao.markAllAsRead()
        }
    }

    fun clearNotifications() {
        viewModelScope.launch(Dispatchers.IO) {
            notificationDao.clearAllNotifications()
        }
    }

    // Database simulation export/backup (Admin feature)
    fun simulateDbBackup(): String {
        return "SQLite Database backup compiled successfully: roktodite_backup_${System.currentTimeMillis()}.sql (1.2 MB). Cloud Synchronization verified."
    }
}
