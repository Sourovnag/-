package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String, // typically phone or email
    val name: String,
    val age: Int,
    val bloodGroup: String,
    val phone: String,
    val area: String,
    val district: String = "Cumilla",
    val upazila: String = "Cumilla Sadar",
    val role: String = "Donor", // "Super Admin", "Admin", "Volunteer", "Donor", "Recipient"
    val verificationStatus: String = "Pending Verification", // "Pending Verification", "Verified", "Rejected"
    val photoId: Int = 1, // index for avatar icon
    val totalDonations: Int = 0,
    val lastDonationDate: String? = null,
    val livesSaved: Int = 0,
    val badge: String = "None", // "None", "Top Donor", "Volunteer", "Hero"
    val isAvailable: Boolean = true,
    val isEmergencyEnabled: Boolean = false,
    val approvalComments: String? = null,
    val permissions: String = "Approve donor,Verify phone,Approve request,Call donor,Update request status" // Comma-separated allowed actions for volunteers
)

@Entity(tableName = "blood_requests")
data class BloodRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientIssue: String,
    val bloodGroup: String,
    val quantity: String,
    val hemoglobin: String? = null,
    val donationDate: String,
    val donationTime: String,
    val hospitalName: String,
    val area: String,
    val contactPhone: String,
    val reference: String? = null,
    val isEmergency: Boolean = false,
    val status: String = "Pending", // "Pending", "Approved", "Rejected", "Fulfilled"
    val timestamp: Long = System.currentTimeMillis(),
    val priorityScore: Double = 0.0,
    val spamScore: Double = 0.0,
    val isSpam: Boolean = false,
    val areaProximityIndex: Int = 0 // simulated distance tier
)

@Entity(tableName = "donation_history")
data class DonationHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val donorId: String,
    val date: String,
    val hospitalName: String,
    val patientName: String? = null,
    val certificateId: String? = null,
    val notes: String? = null
)

@Entity(tableName = "chat_messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val senderName: String,
    val senderRole: String,
    val receiverName: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val type: String = "INFO" // "EMERGENCY", "MATCH", "VERIFICATION", "INFO"
)
