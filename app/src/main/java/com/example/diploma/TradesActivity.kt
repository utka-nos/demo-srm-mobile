package com.example.diploma

import android.os.Bundle

class TradesActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trades)
        
        supportActionBar?.title = getString(R.string.menu_trades)
    }
}
