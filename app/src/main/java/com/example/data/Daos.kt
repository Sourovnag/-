package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY name ASC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE role = 'Donor' OR role = 'Volunteer' ORDER BY name ASC")
    fun getAllDonors(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE role = 'Volunteer' ORDER BY name ASC")
    fun getAllVolunteers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUserById(id: String)
}

@Dao
interface BloodRequestDao {
    @Query("SELECT * FROM blood_requests ORDER BY timestamp DESC")
    fun getAllRequests(): Flow<List<BloodRequestEntity>>

    @Query("SELECT * FROM blood_requests WHERE isEmergency = 1 AND status != 'Fulfilled' ORDER BY timestamp DESC")
    fun getEmergencyRequests(): Flow<List<BloodRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: BloodRequestEntity)

    @Update
    suspend fun updateRequest(request: BloodRequestEntity)

    @Query("UPDATE blood_requests SET status = :status WHERE id = :id")
    suspend fun updateRequestStatus(id: Int, status: String)

    @Query("DELETE FROM blood_requests WHERE id = :id")
    suspend fun deleteRequestById(id: Int)
}

@Dao
interface DonationHistoryDao {
    @Query("SELECT * FROM donation_history WHERE donorId = :donorId ORDER BY date DESC")
    fun getHistoryForDonor(donorId: String): Flow<List<DonationHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: DonationHistoryEntity)
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM chat_messages WHERE (senderName = :userA AND receiverName = :userB) OR (senderName = :userB AND receiverName = :userA) ORDER BY timestamp ASC")
    fun getChatHistory(userA: String, userB: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Int)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()

    @Query("DELETE FROM notifications")
    suspend fun clearAllNotifications()
}
