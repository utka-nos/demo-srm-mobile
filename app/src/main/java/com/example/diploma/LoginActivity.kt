package com.example.diploma

import android.os.Bundle
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
import com.example.diploma.type.AuthenticateDtoInput
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

        loginButton.setOnClickListener {
            val login = loginEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (login.isNotBlank() && password.isNotBlank()) {
                progressBar.visibility = View.VISIBLE
                lifecycleScope.launch {
                    val result = viewModel.login(login, password)
                    progressBar.visibility = View.GONE
                    if (result) {
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Ошибка аутентификации", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Введите логин и пароль", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
