package com.example.data

object MockData {
    // Cumilla District Upazilas / Areas
    val CUMILLA_AREAS = listOf(
        "Cumilla Sadar (কুমিল্লা সদর)",
        "Cumilla Sadar South (কুমিল্লা সদর দক্ষিণ)",
        "Laksam (লাকসাম)",
        "Chauddagram (চৌদ্দগ্রাম)",
        "Barura (বরুড়া)",
        "Debidwar (দেবিদ্বার)",
        "Daudkandi (দাউদকান্দি)",
        "Muradnagar (মুরাদনগর)",
        "Burichang (বুড়িচং)",
        "Brahmanpara (ব্রাহ্মণপাড়া)",
        "Chandina (চান্দিনা)",
        "Langalkot (লাঙ্গলকোট)",
        "Monohorgonj (মনোহরগঞ্জ)",
        "Homna (হোমনা)",
        "Meghna (মেঘনা)",
        "Titas (তিতাস)"
    )

    // Hospitals Directory in Cumilla
    val CUMILLA_HOSPITALS = listOf(
        Hospital("Cumilla Medical College Hospital", "কুমিল্লা মেডিকেল কলেজ হাসপাতাল", "Cumilla Sadar", "01711234567"),
        Hospital("Cumilla General Hospital (Sadar Hospital)", "কুমিল্লা জেনারেল হাসপাতাল", "Sadar South", "01711345678"),
        Hospital("Mainamati Medical College Hospital", "ময়নামতি মেডিকেল কলেজ হাসপাতাল", "Burichang", "01711456789"),
        Hospital("Eastern Medical College Hospital", "ইস্টার্ন মেডিকেল কলেজ হাসপাতাল", "Cumilla Sadar", "01711567890"),
        Hospital("CD Path Hospital Cumilla", "সিডি প্যাথ হাসপাতাল কুমিল্লা", "Cumilla Sadar", "01711678901"),
        Hospital("Moon Hospital Cumilla", "মুন হাসপাতাল কুমিল্লা", "Cumilla Sadar", "01711789012"),
        Hospital("Tower Hospital Cumilla", "টাওয়ার হাসপাতাল কুমিল্লা", "Cumilla Sadar", "01711890123"),
        Hospital("Mukti Hospital", "মুক্তি হাসপাতাল", "Laksam", "01711901234")
    )

    data class Hospital(
        val nameEn: String,
        val nameBn: String,
        val area: String,
        val contact: String
    )

    // Prepopulated Users (Admin, Volunteers, Donors)
    val DEFAULT_USERS = listOf(
        // Super Admin (Founder)
        UserEntity(
            id = "01711111111",
            name = "سهراب নাগ (সৌরভ নাগ)",
            age = 29,
            bloodGroup = "O+",
            phone = "01711111111",
            area = "Cumilla Sadar",
            upazila = "Cumilla Sadar (কুমিল্লা সদর)",
            role = "Super Admin",
            verificationStatus = "Verified",
            photoId = 1,
            totalDonations = 12,
            lastDonationDate = "2026-04-10",
            livesSaved = 12,
            badge = "Hero",
            isAvailable = true,
            permissions = "All"
        ),
        // Volunteer 1
        UserEntity(
            id = "01722222222",
            name = "আরিফ রহমান",
            age = 24,
            bloodGroup = "A+",
            phone = "01722222222",
            area = "Laksam",
            upazila = "Laksam (লাকসাম)",
            role = "Volunteer",
            verificationStatus = "Verified",
            photoId = 2,
            totalDonations = 6,
            lastDonationDate = "2026-05-15",
            livesSaved = 6,
            badge = "Volunteer",
            isAvailable = true,
            permissions = "Approve donor,Verify phone,Approve request,Call donor,Update request status"
        ),
        // Volunteer 2
        UserEntity(
            id = "01733333333",
            name = "তাসনিম সুলতানা",
            age = 22,
            bloodGroup = "B+",
            phone = "01733333333",
            area = "Debidwar",
            upazila = "Debidwar (দেবিদ্বার)",
            role = "Volunteer",
            verificationStatus = "Verified",
            photoId = 3,
            totalDonations = 4,
            lastDonationDate = "2026-03-20",
            livesSaved = 4,
            badge = "Volunteer",
            isAvailable = false,
            permissions = "Approve donor,Verify phone,Approve request,Call donor"
        ),
        // Donor 1
        UserEntity(
            id = "01744444444",
            name = "মেহেদী হাসান",
            age = 27,
            bloodGroup = "AB+",
            phone = "01744444444",
            area = "Cumilla Sadar",
            upazila = "Cumilla Sadar (কুমিল্লা সদর)",
            role = "Donor",
            verificationStatus = "Verified",
            photoId = 4,
            totalDonations = 9,
            lastDonationDate = "2026-01-10",
            livesSaved = 9,
            badge = "Top Donor",
            isAvailable = true
        ),
        // Donor 2
        UserEntity(
            id = "01755555555",
            name = "ফারজানা আক্তার",
            age = 23,
            bloodGroup = "O-",
            phone = "01755555555",
            area = "Chauddagram",
            upazila = "Chauddagram (চৌদ্দগ্রাম)",
            role = "Donor",
            verificationStatus = "Verified",
            photoId = 5,
            totalDonations = 3,
            lastDonationDate = "2026-06-01",
            livesSaved = 3,
            badge = "None",
            isAvailable = false // Unavailable because donated recently (eligibility buffer)
        ),
        // Donor 3 (Pending verification)
        UserEntity(
            id = "01766666666",
            name = "কামরুল ইসলাম",
            age = 31,
            bloodGroup = "A-",
            phone = "01766666666",
            area = "Barura",
            upazila = "Barura (বরুড়া)",
            role = "Donor",
            verificationStatus = "Pending Verification",
            photoId = 6,
            totalDonations = 0,
            lastDonationDate = null,
            livesSaved = 0,
            badge = "None",
            isAvailable = true
        ),
        // Donor 4
        UserEntity(
            id = "01777777777",
            name = "তানভীর আহমেদ",
            age = 28,
            bloodGroup = "B-",
            phone = "01777777777",
            area = "Cumilla Sadar South",
            upazila = "Cumilla Sadar South (কুমিল্লা সদর দক্ষিণ)",
            role = "Donor",
            verificationStatus = "Verified",
            photoId = 7,
            totalDonations = 15,
            lastDonationDate = "2025-12-15",
            livesSaved = 15,
            badge = "Hero",
            isAvailable = true
        )
    )

    // Prepopulated Requests
    val DEFAULT_REQUESTS = listOf(
        BloodRequestEntity(
            id = 1,
            patientIssue = "Thalassemia Patient Blood Exchange (থ্যালাসেমিয়া রোগীর রক্ত বদল)",
            bloodGroup = "A+",
            quantity = "2 Bags",
            hemoglobin = "8.2",
            donationDate = "2026-07-20",
            donationTime = "11:00 AM",
            hospitalName = "Cumilla Medical College Hospital",
            area = "Cumilla Sadar (কুমিল্লা সদর)",
            contactPhone = "01812345678",
            reference = "Dr. Aminul Islam",
            isEmergency = true,
            status = "Approved",
            priorityScore = 95.5
        ),
        BloodRequestEntity(
            id = 2,
            patientIssue = "Emergency Caesarean Section (জরুরি সিজারিয়ান অপারেশন)",
            bloodGroup = "O-",
            quantity = "1 Bag",
            hemoglobin = "9.0",
            donationDate = "2026-07-18",
            donationTime = "02:30 PM",
            hospitalName = "Moon Hospital Cumilla",
            area = "Cumilla Sadar (কুমিল্লা সদর)",
            contactPhone = "01887654321",
            reference = "Patient's Husband",
            isEmergency = true,
            status = "Approved",
            priorityScore = 98.0
        ),
        BloodRequestEntity(
            id = 3,
            patientIssue = "Accident Trauma Surgery (দুর্ঘটনাজনিত ট্রমা সার্জারি)",
            bloodGroup = "O+",
            quantity = "3 Bags",
            hemoglobin = "7.5",
            donationDate = "2026-07-19",
            donationTime = "09:00 AM",
            hospitalName = "Cumilla General Hospital (Sadar Hospital)",
            area = "Cumilla Sadar (কুমিল্লা সদর)",
            contactPhone = "01911223344",
            reference = "Brother",
            isEmergency = true,
            status = "Pending",
            priorityScore = 92.0
        ),
        BloodRequestEntity(
            id = 4,
            patientIssue = "Chemotherapy Support (কেমোথেরাপি সাপোর্ট)",
            bloodGroup = "AB+",
            quantity = "1 Bag",
            hemoglobin = "10.1",
            donationDate = "2026-07-25",
            donationTime = "10:00 AM",
            hospitalName = "Eastern Medical College Hospital",
            area = "Cumilla Sadar (কুমিল্লা সদর)",
            contactPhone = "01511556677",
            reference = "Self",
            isEmergency = false,
            status = "Approved",
            priorityScore = 45.0
        )
    )

    // FAQs
    val FAQS = listOf(
        Faq(
            questionEn = "Who is eligible to donate blood?",
            answerEn = "Any healthy individual between 18 and 60 years old, weighing at least 45 kg, with a hemoglobin level above 12.5g/dL, and who hasn't donated blood in the last 4 months (120 days) can donate blood.",
            questionBn = "কারা রক্ত দিতে পারবেন?",
            answerBn = "১৮ থেকে ৬০ বছর বয়সী যেকোনো সুস্থ ব্যক্তি, যার ওজন কমপক্ষে ৪৫ কেজি, হিমোগ্লোবিনের মাত্রা ১২.৫ বা তার বেশি এবং বিগত ৪ মাসে (১২০ দিন) রক্ত দেননি, তিনি রক্ত দিতে পারবেন।"
        ),
        Faq(
            questionEn = "How long does the blood donation process take?",
            answerEn = "The actual drawing of blood takes only 8-10 minutes. However, the registration, brief medical check, and resting afterward takes about 30 to 45 minutes in total.",
            questionBn = "রক্তদানে কতক্ষণ সময় লাগে?",
            answerBn = "রক্ত ব্যাগ পূর্ণ হতে মাত্র ৮ থেকে ১০ মিনিট সময় লাগে। তবে নিবন্ধন, সংক্ষিপ্ত শারীরিক পরীক্ষা এবং রক্তদানের পর কিছু সময় বিশ্রামের জন্য সব মিলিয়ে ৩০ থেকে ৪৫ মিনিট সময় প্রয়োজন হয়।"
        ),
        Faq(
            questionEn = "Are there any side effects of donating blood?",
            answerEn = "No, there are no long-term side effects. Some people may feel slight dizziness immediately after donation, which goes away after a brief rest and fluid intake. Your body replenishes blood fluids within 24-48 hours.",
            questionBn = "রক্ত দিলে কি কোনো ক্ষতি বা পার্শ্বপ্রতিক্রিয়া হয়?",
            answerBn = "না, কোনো দীর্ঘমেয়াদী পার্শ্বপ্রতিক্রিয়া হয় না। রক্তদানের পর সামান্য মাথা ঘোরার অনুভূতি হতে পারে, যা কিছু তরল খাবার ও বিশ্রামে ঠিক হয়ে যায়। মানবদেহ ২৪-৪৮ ঘণ্টার মধ্যে রক্তের তরল অংশ পূরণ করে ফেলে।"
        ),
        Faq(
            questionEn = "Is my contact number safe on the platform?",
            answerEn = "Absolutely. We keep your exact location hidden, showing only your general area. Your phone number is only visible to verified volunteers and patients with approved emergency requests to prevent spam.",
            questionBn = "আমার যোগাযোগ নম্বর কি এই প্ল্যাটফর্মে নিরাপদ?",
            answerBn = "হ্যাঁ, সম্পূর্ণ নিরাপদ। আমরা আপনার নিখুঁত অবস্থান গোপন রাখি, কেবল আপনার এলাকা দেখাই। স্প্যাম বা হয়রানি রোধ করতে আপনার ফোন নম্বরটি কেবল ভেরিফাইড ভলান্টিয়ার এবং অ্যাপ্রুভড জরুরি রিকোয়েস্টকারীদের দেখানো হয়।"
        )
    )

    data class Faq(
        val questionEn: String,
        val answerEn: String,
        val questionBn: String,
        val answerBn: String
    )

    // Success Stories
    val SUCCESS_STORIES = listOf(
        SuccessStory(
            titleEn = "Thalassemia Child Saved in Monohorgonj",
            titleBn = "মনোহরগঞ্জে থ্যালাসেমিয়া আক্রান্ত শিশুর প্রাণ রক্ষা",
            storyEn = "A 6-year-old Thalassemia patient needed rare O-negative blood in Monohorgonj. Within 12 minutes of creating an emergency request on 'রক্ত দিতে প্রস্তুত', O- donor Farzana was notified, traveled immediately, and completed the life-saving donation.",
            storyBn = "মনোহরগঞ্জে থ্যালাসেমিয়ায় আক্রান্ত ৬ বছরের এক শিশুর জরুরি ও-নেগেটিভ রক্তের প্রয়োজন ছিল। 'রক্ত দিতে প্রস্তুত' প্ল্যাটফর্মে রিকোয়েস্ট করার ১২ মিনিটের মধ্যে ভেরিফাইড ডোনার ফারজানার কাছে নোটিফিকেশন যায়। তিনি তাৎক্ষণিকভাবে গিয়ে রক্তদান সম্পন্ন করেন।"
        ),
        SuccessStory(
            titleEn = "Midnight Accident Response at Cumilla CMCH",
            titleBn = "কুমিল্লা মেডিকেল কলেজে মধ্যরাতে সড়ক দুর্ঘটনার তাৎক্ষণিক রেসপন্স",
            storyEn = "Following a highway accident trauma on Dhaka-Chattogram highway, 3 bags of A-positive blood were requested at 2 AM. Volunteer Arif coordinated directly with nearby registered active donors, and all bags were managed within 40 minutes.",
            storyBn = "ঢাকা-চট্টগ্রাম মহাসড়কে রাতের দুর্ঘটনায় কুমিল্লা মেডিকেল কলেজে রাত ২টায় ৩ ব্যাগ এ-পজিটিভ রক্তের প্রয়োজন হয়। ভলান্টিয়ার আরিফ সরাসরি প্ল্যাটফর্মের মাধ্যমে পার্শ্ববর্তী ডোনারদের সাথে যোগাযোগ করে মাত্র ৪০ মিনিটের মধ্যে রক্তের ব্যবস্থা করেন।"
        )
    )

    data class SuccessStory(
        val titleEn: String,
        val titleBn: String,
        val storyEn: String,
        val storyBn: String
    )
}
