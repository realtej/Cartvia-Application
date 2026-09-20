package com.cartvia.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cartvia.app.data.model.ZoneHeatmapDto
import com.cartvia.app.data.repository.StoreRepository
import com.cartvia.app.util.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreHeatmapViewModel @Inject constructor(
    private val storeRepository: StoreRepository
) : ViewModel() {

    private val _heatmapState = MutableStateFlow<ApiResult<List<ZoneHeatmapDto>>?>(null)
    val heatmapState = _heatmapState.asStateFlow()

    private var pollingJob: Job? = null

    fun startPollingHeatmap(storeId: String) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (true) {
                _heatmapState.value = if (_heatmapState.value is ApiResult.Success) _heatmapState.value else ApiResult.Loading
                _heatmapState.value = storeRepository.getStoreHeatmapZones(storeId)
                delay(12000) // Poll every 12 seconds
            }
        }
    }

    fun stopPollingHeatmap() {
        pollingJob?.cancel()
        pollingJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopPollingHeatmap()
    }
}
