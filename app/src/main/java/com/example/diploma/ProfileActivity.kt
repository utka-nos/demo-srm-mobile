package com.example.diploma

import android.os.Bundle

class ProfileActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        
        supportActionBar?.title = getString(R.string.menu_profile)
    }
}
