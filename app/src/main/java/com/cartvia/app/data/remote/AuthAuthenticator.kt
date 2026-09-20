package com.cartvia.app.data.remote

import com.cartvia.app.data.local.TokenManager
import com.cartvia.app.data.model.RefreshTokenRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider

class AuthAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val authApiProvider: Provider<AuthApi>
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = tokenManager.getRefreshTokenSync() ?: return null
        
        synchronized(this) {
            val currentToken = tokenManager.getAccessTokenSync()
            val authHeader = response.request.header("Authorization")
            
            // If token was already refreshed by another thread
            if (authHeader != null && "Bearer $currentToken" != authHeader) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            val refreshResponse = runBlocking {
                authApiProvider.get().refresh(RefreshTokenRequest(refreshToken))
            }

            if (refreshResponse.isSuccessful && refreshResponse.body()?.success == true) {
                val newAccessToken = refreshResponse.body()?.data?.accessToken ?: return null
                runBlocking { tokenManager.updateAccessToken(newAccessToken) }
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $newAccessToken")
                    .build()
            } else {
                runBlocking { tokenManager.clearAll() }
                return null
            }
        }
    }
}
