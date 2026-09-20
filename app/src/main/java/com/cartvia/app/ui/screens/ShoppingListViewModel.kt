package com.cartvia.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cartvia.app.data.model.ProductResponse
import com.cartvia.app.data.model.ShoppingListResponse
import com.cartvia.app.data.repository.AuthRepository
import com.cartvia.app.data.repository.ProductRepository
import com.cartvia.app.data.repository.ShoppingListRepository
import com.cartvia.app.util.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingListViewModel @Inject constructor(
    private val shoppingListRepository: ShoppingListRepository,
    private val productRepository: ProductRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _listState = MutableStateFlow<ApiResult<ShoppingListResponse>?>(null)
    val listState = _listState.asStateFlow()

    private val _searchResults = MutableStateFlow<List<ProductResponse>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _templates = MutableStateFlow<List<String>>(emptyList())
    val templates = _templates.asStateFlow()

    private val _shareCode = MutableStateFlow<String?>(null)
    val shareCode = _shareCode.asStateFlow()

    fun loadList() {
        viewModelScope.launch {
            val userId = authRepository.currentUser.firstOrNull()?.id ?: "guest-user"
            _listState.value = ApiResult.Loading
            _listState.value = shoppingListRepository.getOrCreateList(userId)
        }
    }

    fun saveAsTemplate(name: String) {
        if (name.isNotBlank() && !_templates.value.contains(name)) {
            _templates.value = _templates.value + name
        }
    }

    fun loadTemplate(name: String) {
        viewModelScope.launch {
            _listState.value = ApiResult.Loading
            // In a real app, this would fetch template items from a DB or API
            delay(500) 
            _listState.value = shoppingListRepository.addBatchItemsLocal(emptyList())
        }
    }

    fun shareList() {
        _shareCode.value = "CART-${(1000..9999).random()}"
    }

    fun clearShareCode() {
        _shareCode.value = null
    }

    fun searchProducts(query: String) {
        if (query.length < 2) {
            _searchResults.value = emptyList()
            return
        }
        viewModelScope.launch {
            val result = productRepository.getProducts(null, query, null, 0, 10)
            if (result is ApiResult.Success) {
                _searchResults.value = result.data.content
            }
        }
    }

    fun addItem(productId: String? = null, customName: String? = null) {
        val currentList = (_listState.value as? ApiResult.Success)?.data ?: ShoppingListResponse("offline-list-id", "Offline List", emptyList())
        viewModelScope.launch {
            _listState.value = shoppingListRepository.addItem(currentList.id, productId, customName)
            _searchResults.value = emptyList()
        }
    }

    fun toggleItem(itemId: String, purchased: Boolean) {
        viewModelScope.launch {
            _listState.value = shoppingListRepository.updateItem(itemId, purchased)
        }
    }

    fun removeItem(itemId: String) {
        viewModelScope.launch {
            _listState.value = shoppingListRepository.deleteItemLocal(itemId)
        }
    }

    fun generateListFromImageMock() {
        viewModelScope.launch {
            _listState.value = ApiResult.Loading
            kotlinx.coroutines.delay(1000)
            // Simulation: Results will be empty until real ML Kit integration
            _listState.value = shoppingListRepository.addBatchItemsLocal(emptyList())
        }
    }
    
    fun createNewList() {
        viewModelScope.launch {
            val userId = authRepository.currentUser.firstOrNull()?.id ?: "guest-user"
            _listState.value = ApiResult.Loading
            _listState.value = shoppingListRepository.createNewList(userId)
        }
    }
}
