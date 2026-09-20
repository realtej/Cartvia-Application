package com.cartvia.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.cartvia.app.ui.navigation.Screen
import com.cartvia.app.util.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartviaTopAppBar(
    title: String,
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        actions = {
            IconButton(onClick = onNotificationClick) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(onClick = onProfileClick) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile / Settings",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun CartviaDivider() {
    HorizontalDivider(
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    )
}

@Composable
fun CartviaBottomNavigationBar(
    navController: NavController,
    currentDestination: NavDestination?
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        val items = listOf(
            NavigationItem(
                label = "Home",
                icon = Icons.Default.Home,
                route = Screen.Home
            ),
            NavigationItem(
                label = "Catalog",
                icon = Icons.Default.List,
                route = Screen.List
            ),
            NavigationItem(
                label = "Current Cart",
                icon = Icons.Default.ShoppingCart,
                route = Screen.ShoppingBag
            ),
            NavigationItem(
                label = "Session Scanner",
                icon = Icons.Default.QrCode,
                route = Screen.ScanAndGo
            ),
            NavigationItem(
                label = "Store Map",
                icon = Icons.Default.Map,
                route = Screen.LocateShop
            )
        )

        items.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                },
                selected = isSelected,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                ),
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

private data class NavigationItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: Any
)

@Composable
fun MainScaffold(
    navController: NavController,
    sessionManager: SessionManager,
    content: @Composable (PaddingValues) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBars = currentDestination?.hierarchy?.any { dest ->
        dest.hasRoute(Screen.Home::class) ||
        dest.hasRoute(Screen.List::class) ||
        dest.hasRoute(Screen.ShoppingBag::class) ||
        dest.hasRoute(Screen.ScanAndGo::class) ||
        dest.hasRoute(Screen.LocateShop::class)
    } == true

    val screenTitle = when {
        currentDestination?.hierarchy?.any { it.hasRoute(Screen.Home::class) } == true -> "Home"
        currentDestination?.hierarchy?.any { it.hasRoute(Screen.List::class) } == true -> "Catalog"
        currentDestination?.hierarchy?.any { it.hasRoute(Screen.ShoppingBag::class) } == true -> "Current Cart"
        currentDestination?.hierarchy?.any { it.hasRoute(Screen.ScanAndGo::class) } == true -> "Session Scanner"
        currentDestination?.hierarchy?.any { it.hasRoute(Screen.LocateShop::class) } == true -> "Store Map"
        else -> "Cartvia"
    }

    Scaffold(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        topBar = {
            if (showBars) {
                CartviaTopAppBar(
                    title = screenTitle,
                    onNotificationClick = { /* Handle notification click */ },
                    onProfileClick = {
                        navController.navigate(Screen.Profile) {
                            launchSingleTop = true
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (showBars) {
                Column {
                    CartviaDivider()
                    CartviaBottomNavigationBar(
                        navController = navController,
                        currentDestination = currentDestination
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            content(innerPadding)
        }
    }
}
