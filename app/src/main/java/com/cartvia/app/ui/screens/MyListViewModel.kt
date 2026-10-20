package com.cartvia.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cartvia.app.data.local.LocalListRepository
import com.cartvia.app.data.model.ShoppingListResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MyListViewModel @Inject constructor(
    private val localListRepository: LocalListRepository
) : ViewModel() {

    private val _lists = MutableStateFlow<List<ShoppingListResponse>>(emptyList())
    val lists = _lists.asStateFlow()

    init {
        loadLists()
    }

    fun loadLists() {
        viewModelScope.launch {
            val saved = localListRepository.getAllLists()
            _lists.value = saved // Starts completely clean with emptyList() if no local list files exist
        }
    }

    fun createListAndGetId(name: String): String {
        val newListId = UUID.randomUUID().toString()
        val newList = ShoppingListResponse(
            id = newListId,
            name = name,
            items = emptyList()
        )
        localListRepository.saveList(newList)
        _lists.value = localListRepository.getAllLists()
        return newListId
    }

    fun deleteList(listId: String) {
        viewModelScope.launch {
            localListRepository.deleteList(listId)
            _lists.value = localListRepository.getAllLists()
        }
    }
}
