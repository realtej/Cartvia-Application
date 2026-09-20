package com.cartvia.app.data.remote

import com.cartvia.app.data.model.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface SessionApi {
    @POST("/api/sessions")
    suspend fun createSession(@Body request: CreateSessionRequest): ApiEnvelope<SessionResponse>

    @GET("/api/sessions/{id}")
    suspend fun getSession(@Path("id") id: String): ApiEnvelope<SessionResponse>

    @POST("/api/sessions/{id}/end")
    suspend fun endSession(@Path("id") id: String): ApiEnvelope<SessionEndResponse>

    @GET("/api/sessions/history")
    suspend fun getSessionHistory(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("search") search: String? = null
    ): ApiEnvelope<PaginatedResponse<SessionHistoryDto>>
}
