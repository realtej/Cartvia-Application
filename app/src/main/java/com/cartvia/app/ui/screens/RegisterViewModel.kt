package com.cartvia.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cartvia.app.data.model.RegisterRequest
import com.cartvia.app.data.repository.AuthRepository
import com.cartvia.app.util.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _registerState = MutableStateFlow<ApiResult<Any>?>(null)
    val registerState = _registerState.asStateFlow()

    fun register(request: RegisterRequest) {
        viewModelScope.launch {
            _registerState.value = ApiResult.Loading
            _registerState.value = authRepository.register(request)
        }
    }

    fun resetRegisterState() {
        _registerState.value = null
    }
}
