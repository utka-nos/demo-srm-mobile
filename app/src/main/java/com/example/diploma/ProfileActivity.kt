package com.example.diploma

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.example.diploma.databinding.ActivityProfileBinding
import kotlinx.coroutines.launch
import java.io.IOException

class ProfileActivity : BaseActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        supportActionBar?.title = getString(R.string.menu_profile)

        binding.retryButton.setOnClickListener {
            loadUserProfile()
        }

        loadUserProfile()
    }

    private fun loadUserProfile() {
        val userIdStr = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
            .getString("user_id", null) ?: return

        val userId = userIdStr.toLongOrNull() ?: return

        showLoading()

        lifecycleScope.launch {
            try {
                val apolloClient = ApolloInstance.getApolloClient(authManager)
                val response = apolloClient.query(GetUserQuery(userId)).execute()

                if (response.hasErrors()) {
                    showError("Ошибка сервера: ${response.errors?.first()?.message}")
                } else {
                    val user = response.data?.user
                    if (user != null) {
                        displayUser(user)
                        showContent()
                    } else {
                        showError("Пользователь не найден")
                    }
                }
            } catch (e: IOException) {
                showError("Ошибка сети. Проверьте подключение.")
            } catch (e: Exception) {
                showError("Произошла ошибка: ${e.message}")
            }
        }
    }

    private fun displayUser(user: GetUserQuery.User) {
        val fullName = "${user.personName?.lastName ?: ""} ${user.personName?.firstName ?: ""} ${user.personName?.middleName ?: ""}".trim()
        binding.profileFullName.text = if (fullName.isNotEmpty()) fullName else user.login
        binding.profilePosition.text = user.position ?: "Должность не указана"
        
        // Display roles (groups)
        val roles = user.groups?.mapNotNull { it?.title }?.joinToString(", ")
        binding.profileGroups.text = if (!roles.isNullOrEmpty()) "Роли: $roles" else "Роли не назначены"
        binding.profileGroups.visibility = if (!roles.isNullOrEmpty()) View.VISIBLE else View.GONE

        binding.profileEmail.text = user.email ?: "Не указан"
        binding.profilePhone.text = user.phone ?: "Не указан"
        binding.profileOrgTitle.text = user.organization?.title ?: "Организация не указана"
        binding.profileOrgInn.text = user.organization?.taxCode?.let { "ИНН: $it" } ?: ""
    }

    private fun showLoading() {
        binding.profileProgressBar.visibility = View.VISIBLE
        binding.profileScrollView.visibility = View.GONE
        binding.errorLayout.visibility = View.GONE
    }

    private fun showContent() {
        binding.profileProgressBar.visibility = View.GONE
        binding.profileScrollView.visibility = View.VISIBLE
        binding.errorLayout.visibility = View.GONE
    }

    private fun showError(message: String) {
        binding.profileProgressBar.visibility = View.GONE
        binding.profileScrollView.visibility = View.GONE
        binding.errorLayout.visibility = View.VISIBLE
        binding.errorTextView.text = message
    }
}
