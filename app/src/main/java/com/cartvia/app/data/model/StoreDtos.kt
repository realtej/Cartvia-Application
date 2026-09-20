package com.cartvia.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class StoreTrafficResponse(
    val storeId: String,
    val storeName: String,
    val busyTrolleys: Int,
    val availableTrolleys: Int,
    val totalTrolleys: Int,
    val occupancyPct: Double
)

@Serializable
data class StoreResponse(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class OfferResponse(
    val id: String,
    val title: String,
    val description: String,
    val productId: String? = null,
    val imageUrl: String? = null
)
