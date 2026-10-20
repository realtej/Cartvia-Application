package com.cartvia.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cartvia.app.data.local.LocalListRepository
import com.cartvia.app.data.model.ProductResponse
import com.cartvia.app.data.model.ShoppingListItemDto
import com.cartvia.app.data.model.ShoppingListResponse
import com.cartvia.app.util.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ShoppingListViewModel @Inject constructor(
    private val localListRepository: LocalListRepository
) : ViewModel() {

    private val _listState = MutableStateFlow<ApiResult<ShoppingListResponse>?>(null)
    val listState = _listState.asStateFlow()

    private val _searchResults = MutableStateFlow<List<ProductResponse>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    var currentListId: String = ""

    fun loadList(listId: String) {
        currentListId = listId
        viewModelScope.launch {
            _listState.value = ApiResult.Loading
            val localList = localListRepository.getList(listId)
            if (localList != null) {
                _listState.value = ApiResult.Success(localList)
            } else {
                val fallback = ShoppingListResponse(
                    id = listId.ifBlank { UUID.randomUUID().toString() },
                    name = "Shopping List",
                    items = emptyList()
                )
                localListRepository.saveList(fallback)
                _listState.value = ApiResult.Success(fallback)
            }
        }
    }

    fun saveCurrentState() {
        val current = (_listState.value as? ApiResult.Success)?.data ?: return
        localListRepository.saveList(current)
    }

    fun createNewListWithName(name: String) {
        viewModelScope.launch {
            val newList = ShoppingListResponse(
                id = currentListId.ifBlank { UUID.randomUUID().toString() },
                name = name,
                items = emptyList()
            )
            localListRepository.saveList(newList)
            currentListId = newList.id
            _listState.value = ApiResult.Success(newList)
        }
    }

    fun deleteCurrentList() {
        viewModelScope.launch {
            val current = (_listState.value as? ApiResult.Success)?.data ?: return@launch
            localListRepository.deleteList(current.id)
            _listState.value = ApiResult.Success(ShoppingListResponse("", "", emptyList()))
        }
    }

    fun addItemCustom(name: String, quantity: Int, unit: String, substituteAllowed: Boolean, isUrgent: Boolean) {
        viewModelScope.launch {
            val current = (_listState.value as? ApiResult.Success)?.data ?: return@launch
            val newItem = ShoppingListItemDto(
                id = UUID.randomUUID().toString(),
                productId = null,
                name = name,
                aisleNumber = "$quantity $unit",
                sectionName = if (isUrgent) "Urgent" else "General",
                purchased = false,
                purchasedAt = null
            )
            val updated = current.copy(items = current.items + newItem)
            localListRepository.saveList(updated) // Auto-save locally
            _listState.value = ApiResult.Success(updated)
        }
    }

    fun updateItemCustom(itemId: String, name: String, quantity: Int, unit: String, substituteAllowed: Boolean, isUrgent: Boolean) {
        viewModelScope.launch {
            val current = (_listState.value as? ApiResult.Success)?.data ?: return@launch
            val updatedItems = current.items.map {
                if (it.id == itemId) {
                    it.copy(
                        name = name,
                        aisleNumber = "$quantity $unit",
                        sectionName = if (isUrgent) "Urgent" else "General"
                    )
                } else it
            }
            val updated = current.copy(items = updatedItems)
            localListRepository.saveList(updated) // Auto-save locally
            _listState.value = ApiResult.Success(updated)
        }
    }

    fun toggleItem(itemId: String, purchased: Boolean) {
        viewModelScope.launch {
            val current = (_listState.value as? ApiResult.Success)?.data ?: return@launch
            val updatedItems = current.items.map {
                if (it.id == itemId) it.copy(purchased = purchased) else it
            }
            val updated = current.copy(items = updatedItems)
            localListRepository.saveList(updated) // Auto-save locally
            _listState.value = ApiResult.Success(updated)
        }
    }

    fun removeItem(itemId: String) {
        viewModelScope.launch {
            val current = (_listState.value as? ApiResult.Success)?.data ?: return@launch
            val updatedItems = current.items.filter { it.id != itemId }
            val updated = current.copy(items = updatedItems)
            localListRepository.saveList(updated) // Auto-save locally
            _listState.value = ApiResult.Success(updated)
        }
    }
}
