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

    suspend fun saveFromPush(payload: PushPayload): Long {
        val entity = NotificationEntity(
            externalId = payload.externalId,
            title = payload.title,
            text = payload.text,
            eventTime = payload.eventTime,
            receivedAt = System.currentTimeMillis(),
            isRead = false
        )

        val insertedId = notificationDao.insert(entity)
        if (insertedId != -1L) {
            return insertedId
        }

        val externalId = payload.externalId
        if (externalId != null) {
            return notificationDao.findByExternalId(externalId)?.id ?: -1L
        }

        return -1L
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
}
