package com.example.diploma

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val authManager = AuthManager(applicationContext)
                return LoginViewModel(authManager) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginEditText: EditText = findViewById(R.id.loginEditText)
        val passwordEditText: EditText = findViewById(R.id.passwordEditText)
        val loginButton: Button = findViewById(R.id.loginButton)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val login = loginEditText.text.toString().trim()
                val password = passwordEditText.text.toString().trim()
                
                val isEnabled = login.isNotEmpty() && password.isNotEmpty()
                loginButton.isEnabled = isEnabled
                
                // Принудительно обновляем цвет текста, если стандартный стиль не справляется
                if (isEnabled) {
                    loginButton.setTextColor(android.graphics.Color.WHITE)
                    loginButton.backgroundTintList = android.content.res.ColorStateList.valueOf(
                        androidx.core.content.ContextCompat.getColor(this@LoginActivity, R.color.purple_500)
                    )
                } else {
                    loginButton.setTextColor(androidx.core.content.ContextCompat.getColor(this@LoginActivity, R.color.grey_75))
                    loginButton.backgroundTintList = android.content.res.ColorStateList.valueOf(
                        androidx.core.content.ContextCompat.getColor(this@LoginActivity, R.color.grey_224)
                    )
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        loginEditText.addTextChangedListener(textWatcher)
        passwordEditText.addTextChangedListener(textWatcher)

        loginButton.setOnClickListener {
            val login = loginEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            progressBar.visibility = View.VISIBLE
            loginButton.isEnabled = false
            
            lifecycleScope.launch {
                val result = viewModel.login(login, password)
                progressBar.visibility = View.GONE
                
                when (result) {
                    is LoginViewModel.LoginResult.Success -> {
                        val intent = Intent(this@LoginActivity, TradesActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    is LoginViewModel.LoginResult.Error -> {
                        loginButton.isEnabled = true
                        Toast.makeText(this@LoginActivity, result.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
