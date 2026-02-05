package com.example.diploma

import androidx.lifecycle.ViewModel
import com.example.diploma.type.AuthenticateDtoInput

class LoginViewModel(private val authManager: AuthManager) : ViewModel() {

    suspend fun login(login: String, password: String): Boolean {
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
        return if (payload?.success == true && payload.jwtToken != null) {
            authManager.saveAuthData(payload.jwtToken, login)
            true
        } else {
            false
        }
    }
}
