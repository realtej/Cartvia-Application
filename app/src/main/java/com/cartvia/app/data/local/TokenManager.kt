package com.cartvia.app.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.cartvia.app.data.model.UserSummaryDto
import com.cartvia.app.data.model.ShoppingListResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(@ApplicationContext private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true }

    private val masterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }
    
    private val sharedPrefs: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            "secure_cartvia_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    companion object {
        private const val ACCESS_TOKEN = "access_token"
        private const val REFRESH_TOKEN = "refresh_token"
        private const val USER_DATA = "user_data"
        private const val PREFERENCES_DATA = "user_preferences"
        private const val ONBOARDING_COMPLETED = "onboarding_completed"
        private const val SAVED_SHOPPING_LIST = "saved_shopping_list"
        private const val SAVED_SHOPPING_LISTS = "saved_shopping_lists"
    }

    fun isOnboardingCompletedSync(): Boolean = sharedPrefs.getBoolean(ONBOARDING_COMPLETED, false)

    suspend fun setOnboardingCompleted(completed: Boolean) = withContext(Dispatchers.IO) {
        sharedPrefs.edit().putBoolean(ONBOARDING_COMPLETED, completed).apply()
    }

    private val _preferences = MutableStateFlow(getPreferencesSync())
    val preferences: Flow<com.cartvia.app.data.model.UserPreferences> = _preferences.asStateFlow()

    private fun getPreferencesSync(): com.cartvia.app.data.model.UserPreferences {
        return sharedPrefs.getString(PREFERENCES_DATA, null)?.let {
            try {
                json.decodeFromString<com.cartvia.app.data.model.UserPreferences>(it)
            } catch (e: Exception) {
                com.cartvia.app.data.model.UserPreferences()
            }
        } ?: com.cartvia.app.data.model.UserPreferences()
    }

    suspend fun updatePreferences(prefs: com.cartvia.app.data.model.UserPreferences) = withContext(Dispatchers.IO) {
        val jsonStr = json.encodeToString(prefs)
        sharedPrefs.edit().putString(PREFERENCES_DATA, jsonStr).apply()
        _preferences.value = prefs
    }

    fun getSavedShoppingListSync(): ShoppingListResponse? {
        return sharedPrefs.getString(SAVED_SHOPPING_LIST, null)?.let {
            try {
                json.decodeFromString<ShoppingListResponse>(it)
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun saveShoppingList(list: ShoppingListResponse?) = withContext(Dispatchers.IO) {
        if (list == null) {
            sharedPrefs.edit().remove(SAVED_SHOPPING_LIST).apply()
        } else {
            val str = json.encodeToString(list)
            sharedPrefs.edit().putString(SAVED_SHOPPING_LIST, str).apply()
        }
    }

    fun getSavedShoppingListsSync(): List<ShoppingListResponse> {
        return sharedPrefs.getString(SAVED_SHOPPING_LISTS, null)?.let {
            try {
                json.decodeFromString<List<ShoppingListResponse>>(it)
            } catch (e: Exception) {
                emptyList()
            }
        } ?: emptyList()
    }

    suspend fun saveShoppingLists(lists: List<ShoppingListResponse>) = withContext(Dispatchers.IO) {
        val str = json.encodeToString(lists)
        sharedPrefs.edit().putString(SAVED_SHOPPING_LISTS, str).apply()
    }

    private val _userData = MutableStateFlow<UserSummaryDto?>(null)
    private var isUserDataLoaded = false

    val userData: Flow<UserSummaryDto?> = flow {
        val cached = synchronized(this@TokenManager) {
            if (!isUserDataLoaded) {
                _userData.value = getUserDataSync()
                isUserDataLoaded = true
            }
            _userData.value
        }
        emit(cached)
        emitAll(_userData)
    }.distinctUntilChanged().flowOn(Dispatchers.IO)

    fun getAccessTokenSync(): String? = sharedPrefs.getString(ACCESS_TOKEN, null)
    fun getRefreshTokenSync(): String? = sharedPrefs.getString(REFRESH_TOKEN, null)
    
    private fun getUserDataSync(): UserSummaryDto? {
        return sharedPrefs.getString(USER_DATA, null)?.let {
            try {
                json.decodeFromString<UserSummaryDto>(it)
            } catch (e: Exception) {
                null
            }
        }
    }

    val accessToken: Flow<String?> = flow { emit(getAccessTokenSync()) }.flowOn(Dispatchers.IO)
    val refreshToken: Flow<String?> = flow { emit(getRefreshTokenSync()) }.flowOn(Dispatchers.IO)

    suspend fun saveTokens(access: String, refresh: String) = withContext(Dispatchers.IO) {
        sharedPrefs.edit().apply {
            putString(ACCESS_TOKEN, access)
            putString(REFRESH_TOKEN, refresh)
            apply()
        }
    }

    suspend fun updateAccessToken(access: String) = withContext(Dispatchers.IO) {
        sharedPrefs.edit().putString(ACCESS_TOKEN, access).apply()
    }

    suspend fun saveUser(user: UserSummaryDto) = withContext(Dispatchers.IO) {
        val userJson = json.encodeToString(user)
        sharedPrefs.edit().putString(USER_DATA, userJson).apply()
        synchronized(this@TokenManager) {
            _userData.value = user
            isUserDataLoaded = true
        }
    }

    suspend fun clearAll() = withContext(Dispatchers.IO) {
        sharedPrefs.edit().clear().apply()
        synchronized(this@TokenManager) {
            _userData.value = null
            isUserDataLoaded = true
        }
    }
}
