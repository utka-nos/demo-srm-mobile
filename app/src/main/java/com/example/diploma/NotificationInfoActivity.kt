package com.example.diploma

import android.os.Bundle
import com.example.diploma.databinding.ActivityNotificationInfoBinding

class NotificationInfoActivity : BaseActivity() {

    private lateinit var binding: ActivityNotificationInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = getString(R.string.notification_info_title)
            setDisplayHomeAsUpEnabled(true)
        }

        val notification = intent.getSerializableExtra(EXTRA_NOTIFICATION) as? Notification
        notification?.let {
            displayNotification(it)
        }
    }

    private fun displayNotification(notification: Notification) {
        binding.notificationTitle.text = notification.title
        binding.notificationDateTime.text = notification.dateTime
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
        const val EXTRA_NOTIFICATION = "extra_notification"
    }
}
