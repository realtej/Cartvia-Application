package com.cartvia.app.data.remote

import com.cartvia.app.data.model.*
import retrofit2.http.*

interface CartApi {
    @GET("/api/carts/{sessionId}")
    suspend fun getCart(@Path("sessionId") sessionId: String): ApiEnvelope<CartResponse>
}
