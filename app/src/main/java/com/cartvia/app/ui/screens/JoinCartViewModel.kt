package com.cartvia.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cartvia.app.data.model.SessionResponse
import com.cartvia.app.data.model.StoreTrafficResponse
import com.cartvia.app.data.repository.AuthRepository
import com.cartvia.app.data.repository.SessionRepository
import com.cartvia.app.data.repository.StoreRepository
import com.cartvia.app.util.ApiResult
import com.cartvia.app.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JoinCartViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val authRepository: AuthRepository,
    private val storeRepository: StoreRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _sessionState = MutableStateFlow<ApiResult<SessionResponse>?>(null)
    val sessionState = _sessionState.asStateFlow()

    private val _storeTraffic = MutableStateFlow<StoreTrafficResponse?>(null)
    val storeTraffic = _storeTraffic.asStateFlow()

    fun joinCart(trolleyCode: String) {
        viewModelScope.launch {
            _sessionState.value = ApiResult.Loading
            val userId = authRepository.currentUser.firstOrNull()?.id
            val result = sessionRepository.createSession(trolleyCode, userId)
            _sessionState.value = result
            
            if (result is ApiResult.Success) {
                sessionManager.startSession(result.data.sessionId)
                fetchStoreTraffic(result.data.storeId)
            }
        }
    }

    private fun fetchStoreTraffic(storeId: String) {
        viewModelScope.launch {
            val result = storeRepository.getTraffic(storeId)
            if (result is ApiResult.Success) {
                _storeTraffic.value = result.data
            }
        }
    }
}
