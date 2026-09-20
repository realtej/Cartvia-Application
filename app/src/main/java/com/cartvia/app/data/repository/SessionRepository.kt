package com.cartvia.app.data.repository

import com.cartvia.app.data.model.*
import com.cartvia.app.data.remote.SessionApi
import com.cartvia.app.util.ApiResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepository @Inject constructor(
    private val sessionApi: SessionApi
) : BaseRepository() {

    suspend fun createSession(trolleyCode: String, userId: String?): ApiResult<SessionResponse> = safeApiCall {
        sessionApi.createSession(CreateSessionRequest(trolleyCode, userId))
    }

    suspend fun getSession(id: String): ApiResult<SessionResponse> = safeApiCall {
        sessionApi.getSession(id)
    }

    suspend fun endSession(id: String): ApiResult<SessionEndResponse> = safeApiCall {
        sessionApi.endSession(id)
    }

    suspend fun getHistory(page: Int, size: Int, search: String? = null): ApiResult<PaginatedResponse<SessionHistoryDto>> = safeApiCall {
        // TODO: backend endpoint pending — see api contract in comments in SessionApi
        sessionApi.getSessionHistory(page, size, search)
    }
}
