package com.cartvia.app.data.repository

import com.cartvia.app.data.model.AddShoppingListItemRequest
import com.cartvia.app.data.model.ShoppingListItemDto
import com.cartvia.app.data.model.ShoppingListResponse
import com.cartvia.app.data.model.UpdateShoppingListItemRequest
import com.cartvia.app.data.remote.ShoppingListApi
import com.cartvia.app.util.ApiResult
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShoppingListRepository @Inject constructor(
    private val shoppingListApi: ShoppingListApi
) : BaseRepository() {

    private var localCachedList: ShoppingListResponse? = null

    private fun getOrCreateLocalFallback(userId: String): ShoppingListResponse {
        val cached = localCachedList
        if (cached != null) return cached
        
        val newFallback = ShoppingListResponse(
            id = "offline-list-id",
            name = "Offline List",
            items = mutableListOf()
        )
        localCachedList = newFallback
        return newFallback
    }

    suspend fun getOrCreateList(userId: String): ApiResult<ShoppingListResponse> {
        val result = safeApiCall { shoppingListApi.getList(userId) }
        return if (result is ApiResult.Success) {
            localCachedList = result.data
            result
        } else {
            ApiResult.Success(getOrCreateLocalFallback(userId))
        }
    }

    suspend fun addItem(listId: String, productId: String?, customName: String?): ApiResult<ShoppingListResponse> {
        val result = safeApiCall { shoppingListApi.addItem(listId, AddShoppingListItemRequest(productId, customName)) }
        return if (result is ApiResult.Success) {
            localCachedList = result.data
            result
        } else {
            val current = localCachedList ?: ShoppingListResponse("offline-list-id", "Offline List", emptyList())
            val newItem = ShoppingListItemDto(
                id = UUID.randomUUID().toString(),
                productId = productId,
                name = customName ?: "Unknown Item",
                aisleNumber = "Aisle 1",
                sectionName = "General",
                purchased = false,
                purchasedAt = null
            )
            val updatedList = current.copy(items = current.items + newItem)
            localCachedList = updatedList
            ApiResult.Success(updatedList)
        }
    }

    suspend fun updateItem(itemId: String, purchased: Boolean): ApiResult<ShoppingListResponse> {
        val result = safeApiCall { shoppingListApi.updateItem(itemId, UpdateShoppingListItemRequest(purchased)) }
        return if (result is ApiResult.Success) {
            localCachedList = result.data
            result
        } else {
            val current = localCachedList ?: ShoppingListResponse("offline-list-id", "Offline List", emptyList())
            val updatedItems = current.items.map {
                if (it.id == itemId) it.copy(purchased = purchased) else it
            }
            val updatedList = current.copy(items = updatedItems)
            localCachedList = updatedList
            ApiResult.Success(updatedList)
        }
    }

    suspend fun deleteItemLocal(itemId: String): ApiResult<ShoppingListResponse> {
        val current = localCachedList ?: ShoppingListResponse("offline-list-id", "Offline List", emptyList())
        val updatedItems = current.items.filter { it.id != itemId }
        val updatedList = current.copy(items = updatedItems)
        localCachedList = updatedList
        return ApiResult.Success(updatedList)
    }

    suspend fun addBatchItemsLocal(itemsToInsert: List<String>): ApiResult<ShoppingListResponse> {
        val current = localCachedList ?: ShoppingListResponse("offline-list-id", "Offline List", emptyList())
        val newItems = itemsToInsert.mapIndexed { idx, name ->
            ShoppingListItemDto(
                id = "ocr-${UUID.randomUUID()}-$idx",
                productId = null,
                name = name,
                aisleNumber = "Aisle ${idx + 2}",
                sectionName = "Scanned",
                purchased = false,
                purchasedAt = null
            )
        }
        val updatedList = current.copy(items = current.items + newItems)
        localCachedList = updatedList
        return ApiResult.Success(updatedList)
    }

    suspend fun createNewList(userId: String): ApiResult<ShoppingListResponse> {
        val result = safeApiCall { shoppingListApi.createList(userId) }
        return if (result is ApiResult.Success) {
            localCachedList = result.data
            result
        } else {
            val newFallback = ShoppingListResponse("offline-list-id", "Offline List", emptyList())
            localCachedList = newFallback
            ApiResult.Success(newFallback)
        }
    }
}
