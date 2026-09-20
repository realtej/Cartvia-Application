package com.cartvia.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cartvia.app.data.model.PaginatedResponse
import com.cartvia.app.data.model.SessionHistoryDto
import com.cartvia.app.data.repository.SessionRepository
import com.cartvia.app.util.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderHistoryViewModel @Inject constructor(
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _historyState = MutableStateFlow<ApiResult<PaginatedResponse<SessionHistoryDto>>?>(null)
    val historyState = _historyState.asStateFlow()

    private var currentPage = 0
    private val pageSize = 20
    private var currentSearch: String? = null

    fun loadHistory(refresh: Boolean = false, search: String? = null) {
        if (refresh || search != currentSearch) {
            currentPage = 0
            currentSearch = search
            _historyState.value = ApiResult.Loading
        }
        
        viewModelScope.launch {
            val result = sessionRepository.getHistory(currentPage, pageSize, search)
            _historyState.value = result
            if (result is ApiResult.Success) {
                currentPage++
            }
        }
    }
}
