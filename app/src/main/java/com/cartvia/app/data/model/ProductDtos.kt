package com.cartvia.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    val productId: String,
    val barcode: String,
    val name: String,
    val description: String,
    val category: String,
    val price: String,
    val discountPct: String? = null,
    val expectedWeightG: String? = null,
    val weightTolerancePct: String? = null,
    val imageUrl: String? = null,
    val aisleNumber: String? = null,
    val sectionName: String? = null,
    val inStock: Boolean
)

@Serializable
data class PaginatedResponse<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val number: Int,
    val size: Int
)
