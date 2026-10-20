package com.cartvia.app.data.local

import android.content.Context
import com.cartvia.app.data.model.ShoppingListResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalListStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }
    private val file = File(context.filesDir, "shopping_lists.json")

    fun loadLists(): List<ShoppingListResponse> {
        return if (file.exists()) {
            try {
                val content = file.readText()
                json.decodeFromString<List<ShoppingListResponse>>(content)
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    fun saveLists(lists: List<ShoppingListResponse>) {
        try {
            val content = json.encodeToString(lists)
            file.writeText(content)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveSingleList(updatedList: ShoppingListResponse) {
        val currentLists = loadLists().toMutableList()
        val index = currentLists.indexOfFirst { it.id == updatedList.id }
        if (index != -1) {
            currentLists[index] = updatedList
        } else {
            currentLists.add(updatedList)
        }
        saveLists(currentLists)
    }

    fun getList(listId: String): ShoppingListResponse? {
        return loadLists().find { it.id == listId }
    }
}
