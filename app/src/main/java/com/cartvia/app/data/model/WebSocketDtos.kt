package com.cartvia.app.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class WebSocketFrame(
    val type: WebSocketEventType,
    val data: JsonElement? = null,
    val timestamp: String
)
