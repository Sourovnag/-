package com.example.ui

object Translations {
    private val dict = mapOf(
        "app_title" to mapOf("BN" to "রক্ত দিতে প্রস্তুত", "EN" to "Ready to Donate"),
        "slogan" to mapOf("BN" to "রক্ত দিতে প্রস্তুত আমরা", "EN" to "We are ready to donate blood"),
        "home" to mapOf("BN" to "হোম", "EN" to "Home"),
        "find_blood" to mapOf("BN" to "রক্ত খুঁজুন", "EN" to "Find Blood"),
        "become_donor" to mapOf("BN" to "রক্তদাতা হোন", "EN" to "Become Donor"),
        "emergency_btn" to mapOf("BN" to "🚨 জরুরি রক্ত প্রয়োজন", "EN" to "🚨 Need Blood Now"),
        "blood_requests" to mapOf("BN" to "রক্তের রিকোয়েস্ট", "EN" to "Blood Requests"),
        "dashboard" to mapOf("BN" to "ড্যাশবোর্ড", "EN" to "Dashboard"),
        "about" to mapOf("BN" to "আমাদের সম্পর্কে", "EN" to "About Us"),
        "contact" to mapOf("BN" to "যোগাযোগ", "EN" to "Contact"),
        "settings" to mapOf("BN" to "সেটিংস", "EN" to "Settings"),
        "notifications" to mapOf("BN" to "নোটিফিকেশন", "EN" to "Notifications"),
        
        // Form & Details Labels
        "full_name" to mapOf("BN" to "পূর্ণ নাম", "EN" to "Full Name"),
        "age" to mapOf("BN" to "বয়স", "EN" to "Age"),
        "blood_group" to mapOf("BN" to "রক্তের গ্রুপ", "EN" to "Blood Group"),
        "phone" to mapOf("BN" to "ফোন নম্বর", "EN" to "Phone Number"),
        "area" to mapOf("BN" to "এলাকা/উপজেলা", "EN" to "Area/Upazila"),
        "district" to mapOf("BN" to "জেলা", "EN" to "District"),
        "status" to mapOf("BN" to "স্ট্যাটাস", "EN" to "Status"),
        "last_donation" to mapOf("BN" to "সর্বশেষ রক্তদান", "EN" to "Last Donation"),
        "total_donations" to mapOf("BN" to "মোট রক্তদান", "EN" to "Total Donations"),
        "lives_saved" to mapOf("BN" to "বাঁচানো জীবন", "EN" to "Lives Saved"),
        "is_available" to mapOf("BN" to "রক্তদানে প্রস্তুত?", "EN" to "Available to Donate?"),
        
        // Recipient request details
        "patient_issue" to mapOf("BN" to "💁 রোগীর সমস্যা", "EN" to "💁 Patient Issue"),
        "blood_qty" to mapOf("BN" to "💉 রক্তের পরিমাণ", "EN" to "💉 Blood Quantity"),
        "hemoglobin" to mapOf("BN" to "👉 হিমোগ্লোবিন (ঐচ্ছিক)", "EN" to "👉 Hemoglobin (Optional)"),
        "donation_date" to mapOf("BN" to "📆 রক্তদানের তারিখ", "EN" to "📆 Donation Date"),
        "donation_time" to mapOf("BN" to "⌚ রক্তদানের সময়", "EN" to "⌚ Donation Time"),
        "hospital_name" to mapOf("BN" to "🏥 হাসপাতালের নাম", "EN" to "🏥 Hospital Name"),
        "reference" to mapOf("BN" to "👨‍👦 রেফারেন্স (ঐচ্ছিক)", "EN" to "👨‍👦 Reference (Optional)"),
        "contact_phone" to mapOf("BN" to "☎ যোগাযোগ নম্বর", "EN" to "☎ Contact Number"),
        
        // Status Terms
        "status_pending" to mapOf("BN" to "পেন্ডিং ভেরিফিকেশন", "EN" to "Pending Verification"),
        "status_verified" to mapOf("BN" to "ভেরিফাইড ডোনার", "EN" to "Verified Donor"),
        "status_rejected" to mapOf("BN" to "প্রত্যাখ্যাত", "EN" to "Rejected"),
        "yes" to mapOf("BN" to "হ্যাঁ", "EN" to "Yes"),
        "no" to mapOf("BN" to "না", "EN" to "No"),
        "approved" to mapOf("BN" to "অনুমোদিত", "EN" to "Approved"),
        "fulfilled" to mapOf("BN" to "রক্তদান সম্পন্ন", "EN" to "Fulfilled"),
        
        // Buttons / Messages
        "submit" to mapOf("BN" to "সাবমিট করুন", "EN" to "Submit"),
        "search" to mapOf("BN" to "অনুসন্ধান করুন", "EN" to "Search"),
        "call" to mapOf("BN" to "কল করুন", "EN" to "Call Now"),
        "whatsapp" to mapOf("BN" to "হোয়াটসঅ্যাপ", "EN" to "WhatsApp"),
        "chat" to mapOf("BN" to "মেসেজ করুন", "EN" to "Send Message"),
        "login" to mapOf("BN" to "লগইন করুন", "EN" to "Login"),
        "logout" to mapOf("BN" to "লগআউট", "EN" to "Logout"),
        "cancel" to mapOf("BN" to "বাতিল করুন", "EN" to "Cancel"),
        "save" to mapOf("BN" to "সংরক্ষণ করুন", "EN" to "Save"),
        
        // Dashboard
        "user_dashboard" to mapOf("BN" to "আমার ড্যাশবোর্ড", "EN" to "My Dashboard"),
        "admin_dashboard" to mapOf("BN" to "এডমিন প্যানেল", "EN" to "Admin Control Panel"),
        "volunteer_queue" to mapOf("BN" to "ভলান্টিয়ার কাজের তালিকা", "EN" to "Volunteer Queue"),
        "registered_donors" to mapOf("BN" to "নিবন্ধিত রক্তদাতা", "EN" to "Registered Donors"),
        "verified_donors" to mapOf("BN" to "ভেরিফাইড রক্তদাতা", "EN" to "Verified Donors"),
        "active_emergencies" to mapOf("BN" to "সক্রিয় জরুরি রক্তের চাহিদা", "EN" to "Active Emergencies"),
        "success_transfusions" to mapOf("BN" to "সাফল্যের সাথে রক্তদান", "EN" to "Successful Transfusions")
    )

    fun get(key: String, lang: String): String {
        return dict[key]?.get(lang) ?: key
    }
}
