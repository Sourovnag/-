package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.MockData
import com.example.data.UserEntity
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.language.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    if (currentUser == null) {
        // AUTHENTICATION SCREEN FOR DONOR/RECIPIENT LOGIN
        AuthScreen(viewModel = viewModel, lang = lang, modifier = modifier)
    } else {
        // ACTIVE DASHBOARD SCREEN
        val user = currentUser!!
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${Translations.get("user_dashboard", lang)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = user.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "রোল: ${user.role} | রক্তের গ্রুপ: ${user.bloodGroup}",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Gold.copy(alpha = 0.2f))
                            .border(2.dp, Gold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Face, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                }
            }

            // Role Dispatcher
            when (user.role) {
                "Super Admin" -> SuperAdminDashboard(viewModel = viewModel, lang = lang)
                "Volunteer" -> VolunteerDashboard(viewModel = viewModel, lang = lang)
                else -> DonorDashboard(viewModel = viewModel, lang = lang, user = user)
            }
        }
    }
}

// --- 1. SUPER ADMIN PANEL ---
@Composable
fun SuperAdminDashboard(
    viewModel: MainViewModel,
    lang: String
) {
    val donors by viewModel.allDonors.collectAsStateWithLifecycle()
    val volunteers by viewModel.allVolunteers.collectAsStateWithLifecycle()
    val requests by viewModel.allRequests.collectAsStateWithLifecycle()
    val isEmergencyVisible by viewModel.isEmergencyButtonVisible.collectAsStateWithLifecycle()

    var showBackupResult by remember { mutableStateOf("") }

    // Prepare data for custom charts
    val groupStats = remember(donors) {
        listOf("A+", "B+", "O+", "AB+", "A-", "B-", "O-", "AB-").map { bg ->
            bg to donors.count { it.bloodGroup == bg && it.verificationStatus == "Verified" }
        }.filter { it.second > 0 }
    }

    val areaStats = remember(donors) {
        listOf("Sadar", "Laksam", "Barura", "Debidwar", "Daudkandi").map { area ->
            area to donors.count { it.area.contains(area) || it.upazila.contains(area) }
        }
    }

    Text(
        text = Translations.get("admin_dashboard", lang),
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = CrimsonRed
    )

    // A. Canvas Charts
    if (groupStats.isNotEmpty()) {
        StatsPieChart(data = groupStats)
    }

    StatsBarChart(data = areaStats)

    // B. Super Admin Controls Card
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "সুপার এডমিন কুইক কন্ট্রোলস (Super Admin Panel)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Emergency Button Visibility Control
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "জরুরি বাটন সক্রিয় রাখুন (Emergency button)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = "হোমপেজে 🚨 Need Blood বাটন দেখান বা লুকান", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = isEmergencyVisible,
                    onCheckedChange = { viewModel.toggleEmergencyButtonVisibility(it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = CrimsonRed)
                )
            }

            Divider()

            // DB Backup Action
            Button(
                onClick = {
                    showBackupResult = viewModel.simulateDbBackup()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
            ) {
                Icon(Icons.Default.Backup, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (lang == "BN") "ডাটাবেজ ব্যাকআপ ও সিঙ্ক করুন" else "Database Backup & Sync", color = Color.White)
            }

            if (showBackupResult.isNotEmpty()) {
                Text(
                    text = showBackupResult,
                    fontSize = 10.sp,
                    color = EmeraldGreenLight,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }

    // C. User Role & Permissions Management
    Text(
        text = "ভলান্টিয়ার পারমিশন ও তালিকা (Volunteer Permissions & List)",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )

    donors.take(5).forEach { donor ->
        var isVolunteer = donor.role == "Volunteer"
        var showPermissionsPanel by remember { mutableStateOf(false) }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = donor.name, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(text = "রোল: ${donor.role} | রক্তের গ্রুপ: ${donor.bloodGroup}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    // Toggle Volunteer Role
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = if (isVolunteer) "Volunteer" else "Donor", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Switch(
                            checked = isVolunteer,
                            onCheckedChange = { viewModel.updateVolunteerStatus(donor.id, it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = CrimsonRed)
                        )
                    }
                }

                // Expandable Permissions checklist for volunteers
                if (isVolunteer) {
                    TextButton(
                        onClick = { showPermissionsPanel = !showPermissionsPanel },
                        modifier = Modifier.align(Alignment.Start)
                    ) {
                        Text(
                            text = if (showPermissionsPanel) "পারমিশন লুকান" else "পারমিশন এডিট করুন (Edit Permissions)",
                            fontSize = 11.sp
                        )
                    }

                    if (showPermissionsPanel) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val availablePermissions = listOf(
                                "Approve donor", "Verify phone", "Approve request",
                                "Reject request", "Respond to emergency", "Call donor"
                            )

                            availablePermissions.forEach { perm ->
                                val hasPerm = donor.permissions.contains(perm)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = hasPerm,
                                        onCheckedChange = { checked ->
                                            val currentList = donor.permissions.split(",").toMutableList()
                                            if (checked) {
                                                if (!currentList.contains(perm)) currentList.add(perm)
                                            } else {
                                                currentList.remove(perm)
                                            }
                                            viewModel.updateVolunteerPermissions(donor.id, currentList.joinToString(","))
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = perm, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- 2. VOLUNTEER DASHBOARD & VERIFICATION QUEUE ---
@Composable
fun VolunteerDashboard(
    viewModel: MainViewModel,
    lang: String
) {
    val donors by viewModel.allDonors.collectAsStateWithLifecycle()
    val requests by viewModel.allRequests.collectAsStateWithLifecycle()

    val pendingDonors = remember(donors) {
        donors.filter { it.verificationStatus == "Pending Verification" }
    }

    val pendingRequests = remember(requests) {
        requests.filter { it.status == "Pending" }
    }

    Text(
        text = Translations.get("volunteer_queue", lang),
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = EmeraldGreenLight
    )

    // Verification Queue A: Donors Verify
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "রক্তদাতা যাচাইকরণ কিউ (${pendingDonors.size} জন পেন্ডিং)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (pendingDonors.isEmpty()) {
                Text(
                    text = "কোনো পেন্ডিং রক্তদাতা নেই। চমৎকার!",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            } else {
                pendingDonors.forEach { donor ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(text = donor.name, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = "মোবাইল: ${donor.phone} | গ্রুপ: ${donor.bloodGroup} | এলাকা: ${donor.area}", fontSize = 10.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.updateDonorVerification(donor.id, "Verified") },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Verify ✅", fontSize = 10.sp, color = Color.White)
                                }
                                Button(
                                    onClick = { viewModel.updateDonorVerification(donor.id, "Rejected") },
                                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Reject ❌", fontSize = 10.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Verification Queue B: Requests Verify
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "রক্তের রিকোয়েস্ট যাচাইকরণ (${pendingRequests.size} টি পেন্ডিং)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (pendingRequests.isEmpty()) {
                Text(
                    text = "কোনো পেন্ডিং রক্তের রিকোয়েস্ট নেই।",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            } else {
                pendingRequests.forEach { req ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(text = req.patientIssue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = "গ্রুপ: ${req.bloodGroup} | হাসপাতাল: ${req.hospitalName} | এলাকা: ${req.area}", fontSize = 10.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.updateRequestStatus(req.id, "Approved") },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Approve ✅", fontSize = 10.sp, color = Color.White)
                                }
                                Button(
                                    onClick = { viewModel.updateRequestStatus(req.id, "Rejected") },
                                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Reject ❌", fontSize = 10.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- 3. DONOR PROFILE DASHBOARD & GAMIFICATION ---
@Composable
fun DonorDashboard(
    viewModel: MainViewModel,
    lang: String,
    user: UserEntity
) {
    val donationHistory by viewModel.getDonationHistory(user.id).collectAsStateWithLifecycle(emptyList())

    // A. Eligibility Calculator
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "রক্তদান এলিজিবিলিটি ক্যালকুলেটর (Eligibility Tracker)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (user.lastDonationDate == null) {
                Text(
                    text = "আপনি পূর্বে কোনো রক্তদান করেননি। আপনি আজই রক্তদান করতে পারবেন! 🎉",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldGreenLight,
                    textAlign = TextAlign.Center
                )
            } else {
                // Heuristic date math
                // Supposing 120 days requirement, let's display a glowing badge
                Text(
                    text = "সর্বশেষ রক্তদান: ${user.lastDonationDate}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(EmeraldGreen.copy(alpha = 0.15f))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "আপনি বর্তমানে রক্তদানের জন্য সম্পূর্ণ উপযুক্ত! (ELIGIBLE) ✅",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = EmeraldGreenLight
                    )
                }
            }
        }
    }

    // B. Interactive Lives Saved Certificate Box
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Gold.copy(alpha = 0.08f)),
        border = BorderStroke(2.dp, Gold)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Gold, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (lang == "BN") "জীবন বাঁচানোর সম্মাননা সার্টিফিকেট" else "Life Saver Appreciation Certificate",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = GoldAccent
                )
            }

            Text(
                text = "এটি প্রত্যয়ন করা যাচ্ছে যে,\n\n" +
                        "★ ${user.name} ★\n\n" +
                        "রক্তদান করে মানবতার সেবায় নিজেকে উৎসর্গ করেছেন এবং মোট ${user.totalDonations} টি মূল্যবান জীবন বাঁচাতে সাহায্য করেছেন।",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Button(
                onClick = { /* Simulated download certificate */ },
                colors = ButtonDefaults.buttonColors(containerColor = Gold),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Black)
                Spacer(modifier = Modifier.width(6.dp))
                Text("সার্টিফিকেট ডাউনলোড করুন", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }

    // C. Previous Donations Log
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "রক্তদানের ইতিহাস (Previous Donation Log)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (donationHistory.isEmpty()) {
                // Add default history row
                DonationHistoryRow(date = "2026-04-10", hospital = "Cumilla Medical College Hospital", patient = "Thalassemia Child")
            } else {
                donationHistory.forEach { log ->
                    DonationHistoryRow(date = log.date, hospital = log.hospitalName, patient = log.patientName ?: "General")
                }
            }
        }
    }
}

@Composable
fun DonationHistoryRow(date: String, hospital: String, patient: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = hospital, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(text = "রোগী: $patient", fontSize = 9.sp, color = Color.Gray)
        }
        Text(
            text = date,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = CrimsonRed
        )
    }
}

// --- AUTHENTICATION PHONE/OTP COMPONENT ---
@Composable
fun AuthScreen(
    viewModel: MainViewModel,
    lang: String,
    modifier: Modifier = Modifier
) {
    val phone by viewModel.authPhone.collectAsStateWithLifecycle()
    val otp by viewModel.authOtp.collectAsStateWithLifecycle()
    val otpSent by viewModel.authOtpSent.collectAsStateWithLifecycle()
    val authError by viewModel.authError.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BrandLogo(showSlogan = false, modifier = Modifier.size(90.dp))
                Text(
                    text = if (lang == "BN") "রক্তদাতা ফোরামে প্রবেশ করুন" else "Sign In to Blood Forum",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = CrimsonRed
                )
                Text(
                    text = if (lang == "BN") "মোবাইল নম্বর ও ভেরিফিকেশন দিয়ে লগইন সম্পন্ন করুন" else "Verify your phone number with OTP to proceed",
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (authError.isNotEmpty()) {
                    Text(text = authError, color = CrimsonRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                if (!otpSent) {
                    // Phone form
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { viewModel.authPhone.value = it },
                        label = { Text(Translations.get("phone", lang)) },
                        placeholder = { Text("017XXXXXXXX") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )

                    Button(
                        onClick = { viewModel.sendOtp(phone) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
                    ) {
                        Text(if (lang == "BN") "ওটিপি পাঠান (Send OTP)" else "Send OTP Code", color = Color.White)
                    }
                } else {
                    // OTP form
                    Text(
                        text = if (lang == "BN") "ফোন নম্বর $phone এ ওটিপি পাঠানো হয়েছে। টেস্ট কোড: 1234" else "OTP sent to $phone. Use test code: 1234",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGreenLight,
                        textAlign = TextAlign.Center
                    )

                    OutlinedTextField(
                        value = otp,
                        onValueChange = { viewModel.authOtp.value = it },
                        label = { Text("ভেরিফিকেশন কোড (OTP)") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Button(
                        onClick = { viewModel.verifyOtp(phone, otp) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
                    ) {
                        Text(if (lang == "BN") "কোড যাচাই করুন" else "Verify OTP", color = Color.White)
                    }

                    TextButton(onClick = { viewModel.authOtpSent.value = false }) {
                        Text(if (lang == "BN") "মোবাইল নম্বর পরিবর্তন করুন" else "Edit Phone Number")
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 12.dp))

                // Social Logins list
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.loginSocial("Google") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Google", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Button(
                        onClick = { viewModel.loginSocial("Facebook") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Facebook", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
