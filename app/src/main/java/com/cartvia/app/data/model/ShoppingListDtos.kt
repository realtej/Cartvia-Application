package com.cartvia.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ShoppingListResponse(
    val id: String,
    val name: String,
    val items: List<ShoppingListItemDto>
)

@Serializable
data class ShoppingListItemDto(
    val id: String,
    val productId: String?,
    val name: String,
    val aisleNumber: String? = null,
    val sectionName: String? = null,
    val purchased: Boolean,
    val purchasedAt: String?
)

@Serializable
data class AddShoppingListItemRequest(
    val productId: String? = null,
    val customName: String? = null
)

@Serializable
data class UpdateShoppingListItemRequest(
    val purchased: Boolean
)
