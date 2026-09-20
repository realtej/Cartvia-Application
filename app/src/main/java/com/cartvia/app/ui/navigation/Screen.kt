package com.cartvia.app.ui.navigation

import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable object Onboarding
    @Serializable object Login
    @Serializable object Register
    @Serializable object ForgotPassword
    @Serializable object Home
    @Serializable object ShoppingBag
    @Serializable object List
    @Serializable object MyList
    @Serializable object ScanAndGo
    @Serializable object LocateShop
    @Serializable object OrderHistory
    @Serializable data class PaymentSuccess(val orderId: String)
    @Serializable data class PaymentFailed(val orderId: String)
    @Serializable data class DigitalReceipt(val orderId: String)
    @Serializable data class Shop(val sessionId: String)
    @Serializable data class Checkout(val orderId: String)
    @Serializable data class Receipt(val orderId: String)
    @Serializable object Profile
    @Serializable data class StoreHeatmap(val storeId: String)
    @Serializable object NearbyStores
}
