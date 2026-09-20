package com.cartvia.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cartvia.app.data.model.OfferResponse
import com.cartvia.app.data.model.StoreOccupancyDto
import com.cartvia.app.data.model.StoreResponse
import com.cartvia.app.data.repository.AuthRepository
import com.cartvia.app.data.repository.ShoppingListRepository
import com.cartvia.app.data.repository.StoreRepository
import com.cartvia.app.util.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val storeRepository: StoreRepository,
    private val shoppingListRepository: ShoppingListRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _offers = MutableStateFlow<List<OfferResponse>>(emptyList())
    val offers = _offers.asStateFlow()

    private val _stores = MutableStateFlow<ApiResult<List<StoreResponse>>?>(null)
    val stores = _stores.asStateFlow()

    private val _storeRanking = MutableStateFlow<ApiResult<List<StoreOccupancyDto>>?>(null)
    val storeRanking = _storeRanking.asStateFlow()

    private val _selectedStore = MutableStateFlow<StoreResponse?>(null)
    val selectedStore = _selectedStore.asStateFlow()

    init {
        loadStores()
        loadStoreRanking()
    }

    fun loadStores() {
        viewModelScope.launch {
            _stores.value = ApiResult.Loading
            val result = storeRepository.getStores()
            _stores.value = result
            if (result is ApiResult.Success && result.data.isNotEmpty()) {
                _selectedStore.value = result.data.first()
            }
        }
    }

    fun loadStoreRanking() {
        viewModelScope.launch {
            _storeRanking.value = storeRepository.getStoreOccupancyRanking()
        }
    }

    fun selectStore(store: StoreResponse) {
        _selectedStore.value = store
    }

    fun addOfferToList(offer: OfferResponse) {
        viewModelScope.launch {
            val userId = authRepository.currentUser.firstOrNull()?.id ?: return@launch
            val listResult = shoppingListRepository.getOrCreateList(userId)
            if (listResult is ApiResult.Success) {
                shoppingListRepository.addItem(listResult.data.id, offer.productId, offer.title)
            }
        }
    }
}
