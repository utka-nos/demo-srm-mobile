package com.example.diploma

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class PushNotificationService : FirebaseMessagingService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onMessageReceived(message: RemoteMessage) {
        val payload = extractPayload(message) ?: return

        serviceScope.launch {
            val repository = NotificationRepository(applicationContext)
            val notificationId = repository.saveFromPush(payload)
            if (notificationId == -1L) {
                return@launch
            }
            showSystemNotification(payload, notificationId)
        }
    }

    override fun onNewToken(token: String) {
        PushTokenStore(applicationContext).saveToken(token)
        serviceScope.launch {
            PushTokenRegistrar(applicationContext).registerToken(token)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    private fun extractPayload(message: RemoteMessage): PushPayload? {
        val data = message.data

        val title = data["title"] ?: message.notification?.title
        val text = data["text"] ?: data["body"] ?: message.notification?.body
        if (title.isNullOrBlank() || text.isNullOrBlank()) {
            Log.w(TAG, "Push payload does not contain title/text")
            return null
        }

        val eventTimeRaw = data["eventTime"] ?: data["event_time"]
        return PushPayload(
            externalId = data["id"] ?: data["notificationId"],
            title = title,
            text = text,
            eventTime = NotificationDateTime.parse(eventTimeRaw)
        )
    }

    private fun showSystemNotification(payload: PushPayload, localNotificationId: Long) {
        ensureChannelExists()

        val intent = Intent(this, NotificationInfoActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(NotificationInfoActivity.EXTRA_NOTIFICATION_ID, localNotificationId)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            localNotificationId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val contentText = "${payload.text}\n${NotificationDateTime.format(payload.eventTime)}"

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(payload.title)
            .setContentText(payload.text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(this)
            .notify(localNotificationId.toInt(), notification)
    }

    private fun ensureChannelExists() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.push_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = getString(R.string.push_channel_description)
        }

        manager.createNotificationChannel(channel)
    }

    companion object {
        private const val TAG = "PushNotificationService"
        private const val CHANNEL_ID = "synapp_push_channel"

        fun syncToken(context: Context) {
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener { token ->
                    PushTokenStore(context).saveToken(token)
                    CoroutineScope(Dispatchers.IO).launch {
                        PushTokenRegistrar(context).registerToken(token)
                    }
                }
                .addOnFailureListener { error ->
                    Log.w(TAG, "Cannot fetch FCM token: ${error.message}")
                }
        }
    }
}
