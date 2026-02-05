package com.example.diploma

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

class AuthorizationInterceptor(private val authManager: AuthManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
        authManager.getToken()?.let {
            request.addHeader("Authorization", "Bearer $it")
        }
        return chain.proceed(request.build())
    }
}

object ApolloInstance {
    // 10.0.2.2 используется для доступа к localhost хост-машины из эмулятора Android
    private const val BASE_URL = "http://10.0.2.2:8080/graphql"
    private var client: ApolloClient? = null

    fun getApolloClient(authManager: AuthManager): ApolloClient {
        if (client == null) {
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(AuthorizationInterceptor(authManager))
                .build()

            client = ApolloClient.Builder()
                .serverUrl(BASE_URL)
                .okHttpClient(okHttpClient)
                .build()
        }
        return client!!
    }
}
