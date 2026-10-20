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
class LocalListRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    private fun getListFile(listId: String): File {
        return File(context.filesDir, "list_$listId.json")
    }

    fun getAllLists(): List<ShoppingListResponse> {
        val dir = context.filesDir
        val files = dir.listFiles { _, name -> name.startsWith("list_") && name.endsWith(".json") } ?: return emptyList()
        return files.mapNotNull { file ->
            try {
                val content = file.readText()
                json.decodeFromString<ShoppingListResponse>(content)
            } catch (e: Exception) {
                null
            }
        }
    }

    fun getList(listId: String): ShoppingListResponse? {
        val file = getListFile(listId)
        if (!file.exists()) return null
        return try {
            val content = file.readText()
            json.decodeFromString<ShoppingListResponse>(content)
        } catch (e: Exception) {
            null
        }
    }

    fun saveList(list: ShoppingListResponse) {
        val file = getListFile(list.id)
        try {
            val content = json.encodeToString(list)
            file.writeText(content)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun deleteList(listId: String) {
        val file = getListFile(listId)
        if (file.exists()) {
            file.delete()
        }
    }
}
