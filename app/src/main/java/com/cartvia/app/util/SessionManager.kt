package com.cartvia.app.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor() {
    private val _activeSessionId = MutableStateFlow<String?>(null)
    val activeSessionId = _activeSessionId.asStateFlow()

    fun startSession(sessionId: String) {
        _activeSessionId.value = sessionId
    }

    fun endSession() {
        _activeSessionId.value = null
    }
}
