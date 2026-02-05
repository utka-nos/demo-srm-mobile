package com.example.diploma

import android.os.Bundle

class OrganizationActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organization)
        
        supportActionBar?.title = getString(R.string.menu_organization)
    }
}
