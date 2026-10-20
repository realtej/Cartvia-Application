package com.cartvia.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.cartvia.app.data.local.TokenManager
import com.cartvia.app.ui.components.MainScaffold
import com.cartvia.app.ui.navigation.Screen
import com.cartvia.app.ui.screens.*
import com.cartvia.app.ui.theme.CartviaTheme
import com.cartvia.app.util.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        val isOnboardingCompleted = tokenManager.isOnboardingCompletedSync()
        val isLoggedIn = tokenManager.getAccessTokenSync() != null

        val startDestination: Any = if (!isOnboardingCompleted) {
            Screen.Onboarding
        } else if (isLoggedIn) {
            Screen.Home
        } else {
            Screen.Login
        }

        setContent {
            CartviaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    MainScaffold(navController = navController, sessionManager = sessionManager) { innerPadding ->
                        CartviaNavHost(
                            navController = navController, 
                            startDestination = startDestination,
                            sessionManager = sessionManager,
                            tokenManager = tokenManager,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CartviaNavHost(
    navController: NavHostController, 
    startDestination: Any,
    sessionManager: SessionManager,
    tokenManager: TokenManager,
    modifier: Modifier = Modifier
) {
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()

    NavHost(
        navController = navController, 
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable<Screen.Onboarding> {
            OnboardingScreen(
                onFinishOnboarding = {
                    coroutineScope.launch {
                        tokenManager.setOnboardingCompleted(true)
                        val nextDestination = if (tokenManager.getAccessTokenSync() != null) Screen.Home else Screen.Login
                        navController.navigate(nextDestination) {
                            popUpTo(Screen.Onboarding) { inclusive = true }
                        }
                    }
                }
            )
        }
        composable<Screen.Login> {
            LoginScreen(
                viewModel = hiltViewModel(),
                onLoginSuccess = { 
                    navController.navigate(Screen.Home) {
                        popUpTo(Screen.Login) { inclusive = true }
                    }
                },
                onRegisterClick = { navController.navigate(Screen.Register) },
                onForgotPasswordClick = { navController.navigate(Screen.ForgotPassword) }
            )
        }
        composable<Screen.Register> {
            RegisterScreen(
                viewModel = hiltViewModel(),
                onRegisterSuccess = { navController.navigate(Screen.Login) },
                onLoginClick = { navController.popBackStack() }
            )
        }
        composable<Screen.ForgotPassword> {
            ForgotPasswordScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable<Screen.Home> {
            HomeScreen(viewModel = hiltViewModel())
        }
        composable<Screen.List> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.List>()
            ShoppingListScreen(
                viewModel = hiltViewModel(),
                listId = route.listId,
                onBack = { navController.popBackStack() }
            )
        }
        composable<Screen.MyList> {
            MyListScreen(
                onSelectList = { listId ->
                    navController.navigate(Screen.List(listId))
                }
            )
        }
        composable<Screen.ShoppingBag> {
            ShoppingBagScreen(
                sessionManager = sessionManager,
                onCheckoutOrderId = { orderId ->
                    navController.navigate(Screen.Checkout(orderId))
                },
                onCheckout = { }
            )
        }
        composable<Screen.ScanAndGo> {
            ScanAndGoScreen(
                viewModel = hiltViewModel(),
                onSessionCreated = { sessionId ->
                    navController.navigate(Screen.ShoppingBag) {
                        popUpTo(Screen.ScanAndGo) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable<Screen.Checkout> { backStackEntry ->
            val route: Screen.Checkout = backStackEntry.toRoute()
            CheckoutScreen(
                orderId = route.orderId,
                viewModel = hiltViewModel(),
                onPaymentSuccess = { orderId ->
                    navController.navigate(Screen.PaymentSuccess(orderId)) {
                        popUpTo(Screen.Checkout) { inclusive = true }
                    }
                }
            )
        }
        composable<Screen.LocateShop> {
            NearbyStoresScreen(
                viewModel = hiltViewModel(),
                onStoreClick = { id -> navController.navigate(Screen.StoreHeatmap(id)) }
            )
        }
        composable<Screen.NearbyStores> {
            NearbyStoresScreen(
                viewModel = hiltViewModel(),
                onStoreClick = { id -> navController.navigate(Screen.StoreHeatmap(id)) }
            )
        }
        composable<Screen.StoreHeatmap> { backStackEntry ->
            val route: Screen.StoreHeatmap = backStackEntry.toRoute()
            StoreHeatmapScreen(
                storeId = route.storeId,
                viewModel = hiltViewModel(),
                onBack = { navController.popBackStack() }
            )
        }
        composable<Screen.OrderHistory> {
            OrderHistoryScreen(
                onNavigateToReceipt = { id -> navController.navigate(Screen.DigitalReceipt(id)) },
                onBack = { navController.popBackStack() }
            )
        }
        composable<Screen.PaymentSuccess> { backStackEntry ->
            val route: Screen.PaymentSuccess = backStackEntry.toRoute()
            PaymentSuccessScreen(
                orderId = route.orderId,
                viewModel = hiltViewModel(),
                onContinue = { navController.navigate(Screen.Home) },
                onViewReceipt = { 
                    navController.navigate(Screen.DigitalReceipt(route.orderId)) 
                }
            )
        }
        composable<Screen.PaymentFailed> { backStackEntry ->
            val route: Screen.PaymentFailed = backStackEntry.toRoute()
            PaymentFailedScreen(
                orderId = route.orderId,
                onRetry = { navController.popBackStack() },
                onCancel = { navController.navigate(Screen.Home) }
            )
        }
        composable<Screen.DigitalReceipt> { backStackEntry ->
            val route: Screen.DigitalReceipt = backStackEntry.toRoute()
            DigitalReceiptScreen(
                orderId = route.orderId,
                viewModel = hiltViewModel(),
                onBack = { navController.popBackStack() }
            )
        }
        composable<Screen.Profile> {
            ProfileScreen(
                viewModel = hiltViewModel(),
                onLogout = { 
                    navController.navigate(Screen.Login) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToHistory = { navController.navigate(Screen.OrderHistory) }
            )
        }
        composable<Screen.Notifications> {
            NotificationsScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable<Screen.SupportFaq> {
            SupportFaqScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable<Screen.SessionEnded> { backStackEntry ->
            val route: Screen.SessionEnded = backStackEntry.toRoute()
            SessionEndedScreen(
                cartId = route.cartId,
                finalBill = route.finalBill,
                onDone = { navController.navigate(Screen.Home) }
            )
        }
    }
}
