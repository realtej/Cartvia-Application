package com.cartvia.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cartvia.app.data.model.StoreOccupancyDto
import com.cartvia.app.data.repository.StoreRepository
import com.cartvia.app.util.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NearbyStoresViewModel @Inject constructor(
    private val storeRepository: StoreRepository
) : ViewModel() {

    private val _rankingState = MutableStateFlow<ApiResult<List<StoreOccupancyDto>>?>(null)
    val rankingState = _rankingState.asStateFlow()

    init {
        loadStoreOccupancyRanking()
    }

    fun loadStoreOccupancyRanking() {
        viewModelScope.launch {
            _rankingState.value = ApiResult.Loading
            _rankingState.value = storeRepository.getStoreOccupancyRanking()
        }
    }
}
