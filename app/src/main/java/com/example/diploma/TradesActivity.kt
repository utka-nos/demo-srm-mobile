package com.example.diploma

import android.content.Intent
import android.os.Bundle

class TradesActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Перемещаем проверку авторизации ПЕРЕД setContentView или BaseActivity.onCreate
        // Но так как BaseActivity инициализирует authManager в onCreate, 
        // мы должны вызвать super.onCreate или получить его вручную.
        // BaseActivity.onCreate вызывает super.onCreate(savedInstanceState) и setContentView(R.layout.activity_base)
        
        if (!authManager.isLoggedIn()) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_trades)
        supportActionBar?.title = getString(R.string.menu_trades)
    }
}
