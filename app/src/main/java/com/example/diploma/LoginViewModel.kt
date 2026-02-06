package com.example.diploma

import androidx.lifecycle.ViewModel
import com.example.diploma.type.AuthenticateDtoInput
import com.apollographql.apollo3.exception.ApolloException

class LoginViewModel(private val authManager: AuthManager) : ViewModel() {

    sealed class LoginResult {
        object Success : LoginResult()
        data class Error(val message: String) : LoginResult()
    }

    suspend fun login(login: String, password: String): LoginResult {
        return try {
            val apolloClient = ApolloInstance.getApolloClient(authManager)
            val response = apolloClient.mutation(
                AuthenticateMutation(
                    AuthenticateDtoInput(
                        login = com.apollographql.apollo3.api.Optional.present(login),
                        password = com.apollographql.apollo3.api.Optional.present(password)
                    )
                )
            ).execute()

            val payload = response.data?.authenticate
            if (payload?.success == true && payload.jwtToken != null) {
                authManager.saveAuthData(payload.jwtToken, login, payload.currentUserId.toString())
                LoginResult.Success
            } else {
                LoginResult.Error("Неверный логин или пароль")
            }
        } catch (e: ApolloException) {
            LoginResult.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            LoginResult.Error("Произошла ошибка: ${e.message}")
        }
    }
}
