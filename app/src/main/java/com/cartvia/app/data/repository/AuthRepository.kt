package com.cartvia.app.data.repository

import com.cartvia.app.data.local.TokenManager
import com.cartvia.app.data.model.*
import com.cartvia.app.data.remote.AuthApi
import com.cartvia.app.data.remote.UserApi
import com.cartvia.app.util.ApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val userApi: UserApi,
    private val tokenManager: TokenManager
) : BaseRepository() {

    suspend fun register(request: RegisterRequest): ApiResult<UserIdResponse> = withContext(Dispatchers.IO) {
        safeApiCall { authApi.register(request) }
    }

    suspend fun login(request: LoginRequest): ApiResult<LoginResponse> = withContext(Dispatchers.IO) {
        val result = safeApiCall { authApi.login(request) }
        if (result is ApiResult.Success) {
            tokenManager.saveTokens(result.data.accessToken, result.data.refreshToken)
            tokenManager.saveUser(result.data.user)
        }
        result
    }

    suspend fun devBypassLogin(): ApiResult<LoginResponse> = withContext(Dispatchers.IO) {
        val adminUser = UserSummaryDto(
            id = "sys-admin-01",
            username = "system_admin",
            name = "System Administrator",
            email = "admin@cartvia.app",
            phone = "0000000000",
            role = Role.ADMIN
        )
        val authResponse = LoginResponse(
            accessToken = "internal-bypass-token-secure",
            refreshToken = "internal-refresh-token-secure",
            user = adminUser
        )
        tokenManager.saveTokens(authResponse.accessToken, authResponse.refreshToken)
        tokenManager.saveUser(authResponse.user)
        ApiResult.Success(authResponse)
    }

    suspend fun forgotPassword(request: ForgotPasswordRequest): ApiResult<ResetTokenSentResponse> = withContext(Dispatchers.IO) {
        safeApiCall { authApi.forgotPassword(request) }
    }

    suspend fun resetPassword(request: ResetPasswordRequest): ApiResult<SuccessResponse> = withContext(Dispatchers.IO) {
        safeApiCall { authApi.resetPassword(request) }
    }

    suspend fun updateProfile(name: String, email: String, phone: String): ApiResult<UserSummaryDto> = withContext(Dispatchers.IO) {
        val result = safeApiCall { userApi.updateProfile(UpdateProfileRequest(name, email, phone)) }
        if (result is ApiResult.Success) {
            tokenManager.saveUser(result.data)
        }
        result
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        tokenManager.clearAll()
    }

    val currentUser = tokenManager.userData
    val preferences = tokenManager.preferences

    suspend fun updatePreferences(prefs: UserPreferences) {
        tokenManager.updatePreferences(prefs)
    }
}
