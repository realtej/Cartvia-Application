package com.cartvia.app.data.remote

import com.cartvia.app.data.model.UpdateProfileRequest
import com.cartvia.app.data.model.UserSummaryDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface UserApi {
    @GET("/api/users/me")
    suspend fun getMe(): ApiEnvelope<UserSummaryDto>

    @PUT("/api/users/me")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): ApiEnvelope<UserSummaryDto>
}
