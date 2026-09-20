package com.cartvia.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cartvia.app.data.model.*
import com.cartvia.app.data.remote.WebSocketManager
import com.cartvia.app.data.repository.AuthRepository
import com.cartvia.app.data.repository.CartRepository
import com.cartvia.app.data.repository.SessionRepository
import com.cartvia.app.data.repository.ShoppingListRepository
import com.cartvia.app.util.ApiResult
import com.cartvia.app.util.NotificationHelper
import com.cartvia.app.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ShoppingStatus(
    val productCount: Int = 0,
    val isSynchronized: Boolean = true,
    val unverifiedCount: Int = 0,
    val totalAmount: String = "0.00",
    val canCheckout: Boolean = false,
    val budgetLimit: Double? = null,
    val budgetProgress: Float = 0f
)

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val sessionRepository: SessionRepository,
    private val webSocketManager: WebSocketManager,
    private val sessionManager: SessionManager,
    private val shoppingListRepository: ShoppingListRepository,
    private val authRepository: AuthRepository,
    private val notificationHelper: NotificationHelper
) : ViewModel() {

    private val _cartState = MutableStateFlow<ApiResult<CartResponse>?>(null)
    val cartState = _cartState.asStateFlow()

    private val _budgetLimit = MutableStateFlow<Double?>(null)
    val budgetLimit = _budgetLimit.asStateFlow()

    private val _shoppingList = MutableStateFlow<ShoppingListResponse?>(null)

    private val _uiEvents = MutableSharedFlow<String>()
    val uiEvents = _uiEvents.asSharedFlow()

    private var lastBudgetAlertPercent = 0f

    val shoppingStatus: StateFlow<ShoppingStatus> = combine(_cartState, _budgetLimit) { state, budget ->
        if (state is ApiResult.Success) {
            val items = state.data.items
            val unverified = items.count { it.verificationStatus != VerificationStatus.VERIFIED }
            val total = state.data.totalAmount.toDoubleOrNull() ?: 0.0
            val progress = if (budget != null && budget > 0) (total / budget).toFloat() else 0f
            
            // Trigger Budget Notifications
            if (budget != null) {
                if (progress >= 1.0f && lastBudgetAlertPercent < 1.0f) {
                    notificationHelper.showNotification("Budget Limit Reached!", "You've spent ₹${state.data.totalAmount} which exceeds your limit.")
                    lastBudgetAlertPercent = 1.0f
                } else if (progress >= 0.8f && lastBudgetAlertPercent < 0.8f) {
                    notificationHelper.showNotification("Budget Warning", "You've reached 80% of your shopping limit.")
                    lastBudgetAlertPercent = 0.8f
                } else if (progress < 0.8f) {
                    lastBudgetAlertPercent = 0f
                }
            }

            ShoppingStatus(
                productCount = items.sumOf { it.quantity },
                isSynchronized = true,
                unverifiedCount = unverified,
                totalAmount = state.data.totalAmount,
                canCheckout = items.isNotEmpty() && unverified == 0,
                budgetLimit = budget,
                budgetProgress = progress
            )
        } else {
            ShoppingStatus(budgetLimit = budget)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ShoppingStatus())

    fun setBudgetLimit(limit: Double?) {
        _budgetLimit.value = limit
        lastBudgetAlertPercent = 0f
    }

    fun initSession(sessionId: String) {
        refreshCart(sessionId)
        loadShoppingList()
        webSocketManager.connect(sessionId)
        
        notificationHelper.showNotification("Cart Connected", "Your mobile device is now synced with Cart #$sessionId.")

        webSocketManager.events
            .onEach { frame ->
                when (frame.type) {
                    WebSocketEventType.PRODUCT_ADDED -> {
                        _uiEvents.emit("Item added to your cart")
                        notificationHelper.showNotification("Item Added", "A new item was detected by the trolley sensor and added to your bill.")
                        refreshCart(sessionId)
                    }
                    WebSocketEventType.PRODUCT_REMOVED -> {
                        _uiEvents.emit("Item removed from cart")
                        refreshCart(sessionId)
                    }
                    WebSocketEventType.CART_UPDATED,
                    WebSocketEventType.WEIGHT_VALIDATION_RESULT -> {
                        refreshCart(sessionId)
                    }
                    else -> { /* Handle other types */ }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadShoppingList() {
        viewModelScope.launch {
            val userId = authRepository.currentUser.firstOrNull()?.id ?: return@launch
            val result = shoppingListRepository.getOrCreateList(userId)
            if (result is ApiResult.Success) {
                _shoppingList.value = result.data
            }
        }
    }

    fun refreshCart(sessionId: String) {
        viewModelScope.launch {
            val result = cartRepository.getCart(sessionId)
            if (result is ApiResult.Success) {
                _cartState.value = result
                checkShoppingList(result.data.items)
            } else {
                // Local fallback so user can interact even when backend api placeholder is static
                if (_cartState.value !is ApiResult.Success) {
                    val fallbackCart = CartResponse(
                        cartId = "cart-$sessionId",
                        sessionId = sessionId,
                        totalAmount = "0.00",
                        items = emptyList()
                    )
                    _cartState.value = ApiResult.Success(fallbackCart)
                }
            }
        }
    }

    fun removeCartItemLocal(productId: String) {
        val currentState = (_cartState.value as? ApiResult.Success)?.data ?: return
        val updatedItems = currentState.items.filter { it.productId != productId }
        val newTotal = updatedItems.sumOf { (it.unitPrice.toDoubleOrNull() ?: 0.0) * it.quantity }
        _cartState.value = ApiResult.Success(
            currentState.copy(
                items = updatedItems,
                totalAmount = "%.2f".format(newTotal)
            )
        )
    }

    fun updateCartItemQuantityLocal(productId: String, delta: Int) {
        val currentState = (_cartState.value as? ApiResult.Success)?.data ?: return
        val updatedItems = currentState.items.map {
            if (it.productId == productId) {
                val newQty = (it.quantity + delta).coerceAtLeast(1)
                it.copy(quantity = newQty)
            } else it
        }
        val newTotal = updatedItems.sumOf { (it.unitPrice.toDoubleOrNull() ?: 0.0) * it.quantity }
        _cartState.value = ApiResult.Success(
            currentState.copy(
                items = updatedItems,
                totalAmount = "%.2f".format(newTotal)
            )
        )
    }

    private fun checkShoppingList(cartItems: List<CartItemDto>) {
        val currentList = _shoppingList.value ?: return
        val cartProductIds = cartItems.map { it.productId }.toSet()
        
        currentList.items.forEach { listItem ->
            if (listItem.productId != null && 
                cartProductIds.contains(listItem.productId) && 
                !listItem.purchased) {
                
                // Item found in cart but not marked purchased in list
                viewModelScope.launch {
                    val result = shoppingListRepository.updateItem(listItem.id, true)
                    if (result is ApiResult.Success) {
                        _shoppingList.value = result.data
                    }
                }
            }
        }
    }

    private val _orderId = MutableStateFlow<String?>(null)
    val orderId = _orderId.asStateFlow()

    fun endSession(sessionId: String) {
        viewModelScope.launch {
            val result = sessionRepository.endSession(sessionId)
            if (result is ApiResult.Success<SessionEndResponse>) {
                _orderId.value = result.data.orderId
            }
        }
    }

    override fun onCleared() {
        webSocketManager.disconnect()
    }
}
