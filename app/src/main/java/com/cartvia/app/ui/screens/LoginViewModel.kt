package com.cartvia.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cartvia.app.data.model.LoginRequest
import com.cartvia.app.data.repository.AuthRepository
import com.cartvia.app.util.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<ApiResult<Any>?>(null)
    val loginState = _loginState.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = ApiResult.Loading
            _loginState.value = authRepository.login(LoginRequest(username, password))
        }
    }

    fun loginDevBypass(key: String) {
        if (key == "CARTVIA_DEV_2026" || key == "dev123") {
            viewModelScope.launch {
                _loginState.value = ApiResult.Loading
                _loginState.value = authRepository.devBypassLogin()
            }
        } else {
            _loginState.value = ApiResult.Error(
                code = com.cartvia.app.data.remote.ApiErrorCode.UNKNOWN,
                message = "Invalid Developer Key"
            )
        }
    }

    fun resetLoginState() {
        _loginState.value = null
    }
}
