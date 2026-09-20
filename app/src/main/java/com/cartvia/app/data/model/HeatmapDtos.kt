package com.cartvia.app.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class HeatmapLevel {
    LOW, MEDIUM, HIGH
}

@Serializable
data class ZoneHeatmapDto(
    val zoneId: String,
    val zoneName: String,
    val activeCarts: Int,
    val capacityThreshold: Int,
    val level: HeatmapLevel
)

@Serializable
data class StoreOccupancyDto(
    val storeId: String,
    val storeName: String,
    val activeCarts: Int
)
