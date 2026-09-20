package com.cartvia.app.data.remote

import com.cartvia.app.data.model.AddShoppingListItemRequest
import com.cartvia.app.data.model.ShoppingListResponse
import com.cartvia.app.data.model.UpdateShoppingListItemRequest
import retrofit2.http.*

interface ShoppingListApi {
    @GET("/api/shopping-list")
    suspend fun getList(@Query("userId") userId: String): ApiEnvelope<ShoppingListResponse>

    @POST("/api/shopping-list")
    suspend fun createList(@Query("userId") userId: String): ApiEnvelope<ShoppingListResponse>

    @POST("/api/shopping-list/{listId}/items")
    suspend fun addItem(
        @Path("listId") listId: String,
        @Body request: AddShoppingListItemRequest
    ): ApiEnvelope<ShoppingListResponse>

    @PATCH("/api/shopping-list/items/{itemId}")
    suspend fun updateItem(
        @Path("itemId") itemId: String,
        @Body request: UpdateShoppingListItemRequest
    ): ApiEnvelope<ShoppingListResponse>
}
