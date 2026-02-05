package com.example.diploma

import android.os.Bundle

class NotificationsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)
        supportActionBar?.title = getString(R.string.menu_notifications)
    }
}
