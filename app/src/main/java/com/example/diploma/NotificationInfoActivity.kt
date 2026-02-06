package com.example.diploma

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.diploma.databinding.ActivityNotificationInfoBinding
import kotlinx.coroutines.launch

class NotificationInfoActivity : BaseActivity() {

    private lateinit var binding: ActivityNotificationInfoBinding
    private lateinit var repository: NotificationRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        repository = NotificationRepository(applicationContext)

        supportActionBar?.apply {
            title = getString(R.string.notification_info_title)
            setDisplayHomeAsUpEnabled(true)
        }

        val notificationId = intent.getLongExtra(EXTRA_NOTIFICATION_ID, -1L)
        if (notificationId != -1L) {
            lifecycleScope.launch {
                repository.markAsRead(notificationId)
                val notification = repository.getById(notificationId)
                notification?.let { displayNotification(it) }
            }
        }
    }

    private fun displayNotification(notification: Notification) {
        binding.notificationTitle.text = notification.title
        binding.notificationDateTime.text = NotificationDateTime.format(notification.eventTime)
        binding.notificationText.text = if (notification.text.isNotEmpty()) {
            notification.text
        } else {
            "Нет подробного описания"
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
    }
}
