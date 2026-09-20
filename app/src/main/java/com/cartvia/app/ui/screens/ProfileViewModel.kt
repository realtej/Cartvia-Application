package com.cartvia.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cartvia.app.data.model.UserPreferences
import com.cartvia.app.data.repository.AuthRepository
import com.cartvia.app.util.ApiResult
import com.cartvia.app.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    val user = authRepository.currentUser
    val preferences = authRepository.preferences

    private val _updateState = MutableStateFlow<ApiResult<Any>?>(null)
    val updateState = _updateState.asStateFlow()

    fun updateProfile(name: String, email: String, phone: String) {
        viewModelScope.launch {
            _updateState.value = ApiResult.Loading
            _updateState.value = authRepository.updateProfile(name, email, phone)
        }
    }

    fun updatePreferences(prefs: UserPreferences) {
        viewModelScope.launch {
            authRepository.updatePreferences(prefs)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            sessionManager.endSession()
        }
    }
}
