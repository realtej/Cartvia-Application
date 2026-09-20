package com.cartvia.app.data.repository

import com.cartvia.app.data.model.*
import com.cartvia.app.data.remote.CartApi
import com.cartvia.app.util.ApiResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor(
    private val cartApi: CartApi
) : BaseRepository() {

    suspend fun getCart(sessionId: String): ApiResult<CartResponse> = safeApiCall {
        cartApi.getCart(sessionId)
    }
}
