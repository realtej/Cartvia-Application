package com.cartvia.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserPreferences(
    val dietaryTags: List<String> = emptyList(),
    val defaultBudget: Double? = null,
    val savedPaymentMethodId: String? = null
)
