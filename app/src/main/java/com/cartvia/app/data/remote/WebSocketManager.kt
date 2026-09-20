package com.cartvia.app.data.remote

import com.cartvia.app.BuildConfig
import com.cartvia.app.data.model.WebSocketFrame
import com.cartvia.app.di.AuthenticatedClient
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.json.Json
import okhttp3.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketManager @Inject constructor(
    @AuthenticatedClient private val okHttpClient: OkHttpClient,
    private val json: Json
) {
    private var webSocket: WebSocket? = null
    private var reconnectJob: Job? = null
    private var currentSessionId: String? = null
    private val socketScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var retryDelayMs = 2000L
    
    private val _events = MutableSharedFlow<WebSocketFrame>(
        extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events = _events.asSharedFlow()

    fun connect(sessionId: String) {
        currentSessionId = sessionId

        reconnectJob?.cancel()
        val request = Request.Builder()
            .url("${BuildConfig.BASE_URL.replace("http", "ws")}/ws")
            .build()
            
        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                retryDelayMs = 2000L // Reset delay on success
                // Subscribe to topic
                val subscribeFrame = "SUBSCRIBE\nid:sub-0\ndestination:/topic/sessions/$sessionId\n\n\u0000"
                webSocket.send(subscribeFrame)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                // Basic STOMP parser for MESSAGE frames
                if (text.startsWith("MESSAGE")) {
                    val body = text.substringAfter("\n\n").substringBefore("\u0000")
                    try {
                        val frame = json.decodeFromString<WebSocketFrame>(body)
                        _events.tryEmit(frame)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                t.printStackTrace()
                scheduleReconnect()
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                // If closed due to network or unexpected closure, try reconnecting unless user explicit disconnected
                if (code != 1000) {
                    scheduleReconnect()
                }
            }
        })
    }

    private fun scheduleReconnect() {
        val sessionId = currentSessionId ?: return
        reconnectJob?.cancel()
        reconnectJob = socketScope.launch {
            delay(retryDelayMs)
            // Exponential backoff logic with 30s ceiling
            retryDelayMs = (retryDelayMs * 2).coerceAtMost(30000L)
            connect(sessionId)
        }
    }

    fun disconnect() {
        currentSessionId = null
        reconnectJob?.cancel()
        reconnectJob = null
        webSocket?.close(1000, "User disconnected")
        webSocket = null
    }
}
