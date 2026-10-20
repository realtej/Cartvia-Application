package com.cartvia.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                color = MaterialTheme.colorScheme.onBackground
            )
        },
        actions = {
            IconButton(onClick = onNotificationClick) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
fun CartviaBottomNavigationBar(
    navController: NavController,
    currentDestination: NavDestination?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 16.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Home
                val isHomeSelected = currentDestination?.hierarchy?.any { it.hasRoute(Screen.Home::class) } == true
                NavPillItem(
                    label = "Home",
                    icon = Icons.Default.Home,
                    isSelected = isHomeSelected,
                    onClick = {
                        navController.navigate(Screen.Home) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )

                // 2. List
                val isListSelected = currentDestination?.hierarchy?.any { it.hasRoute(Screen.MyList::class) || it.hasRoute(Screen.List::class) } == true
                NavPillItem(
                    label = "List",
                    icon = Icons.Default.List,
                    isSelected = isListSelected,
                    onClick = {
                        navController.navigate(Screen.MyList) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )

                // 3. Center Scan Button
                val isScanSelected = currentDestination?.hierarchy?.any { it.hasRoute(Screen.ScanAndGo::class) } == true
                if (isScanSelected) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF635BFF),
                        onClick = {
                            navController.navigate(Screen.ScanAndGo) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CropFree,
                                contentDescription = "Scan",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Scan",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF635BFF))
                            .clickable {
                                navController.navigate(Screen.ScanAndGo) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CropFree,
                            contentDescription = "Scan & Go",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // 4. Map
                val isMapSelected = currentDestination?.hierarchy?.any { it.hasRoute(Screen.NearbyStores::class) || it.hasRoute(Screen.LocateShop::class) } == true
                NavPillItem(
                    label = "Map",
                    icon = Icons.Default.Map,
                    isSelected = isMapSelected,
                    onClick = {
                        navController.navigate(Screen.NearbyStores) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )

                // 5. Profile
                val isProfileSelected = currentDestination?.hierarchy?.any { it.hasRoute(Screen.Profile::class) } == true
                NavPillItem(
                    label = "Profile",
                    icon = Icons.Default.Person,
                    isSelected = isProfileSelected,
                    onClick = {
                        navController.navigate(Screen.Profile) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun NavPillItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    if (isSelected) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
            onClick = onClick
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    } else {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

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
        dest.hasRoute(Screen.MyList::class) ||
        dest.hasRoute(Screen.ShoppingBag::class) ||
        dest.hasRoute(Screen.ScanAndGo::class) ||
        dest.hasRoute(Screen.LocateShop::class) ||
        dest.hasRoute(Screen.NearbyStores::class) ||
        dest.hasRoute(Screen.Profile::class) ||
        dest.hasRoute(Screen.Notifications::class) ||
        dest.hasRoute(Screen.SupportFaq::class) ||
        dest.hasRoute(Screen.OrderHistory::class)
    } == true

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        content(PaddingValues())

        if (showBars) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                CartviaBottomNavigationBar(
                    navController = navController,
                    currentDestination = currentDestination
                )
            }
        }
    }
}
