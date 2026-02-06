package com.example.diploma

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotificationRepository(context: Context) {

    private val notificationDao = AppDatabase.getInstance(context).notificationDao()

    fun observeNotifications(): Flow<List<Notification>> {
        return notificationDao.observeAll().map { list ->
            list.map { entity ->
                Notification(
                    id = entity.id,
                    title = entity.title,
                    text = entity.text,
                    eventTime = entity.eventTime,
                    isRead = entity.isRead
                )
            }
        }
    }

    suspend fun syncNotifications(): SyncNotificationsResult {
        // TODO: call backend API/GraphQL endpoint for notifications and upsert into Room.
        return SyncNotificationsResult.NotImplemented
    }

    suspend fun getById(id: Long): Notification? {
        val entity = notificationDao.getById(id) ?: return null
        return Notification(
            id = entity.id,
            title = entity.title,
            text = entity.text,
            eventTime = entity.eventTime,
            isRead = entity.isRead
        )
    }

    suspend fun markAsRead(id: Long) {
        notificationDao.markAsRead(id)
    }

    sealed interface SyncNotificationsResult {
        data object Success : SyncNotificationsResult
        data object NotImplemented : SyncNotificationsResult
        data class Error(val message: String) : SyncNotificationsResult
    }
}
