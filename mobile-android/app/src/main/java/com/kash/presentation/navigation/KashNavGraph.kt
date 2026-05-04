package com.kash.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kash.domain.model.AuthState
import com.kash.presentation.AppViewModel
import com.kash.presentation.auth.LoginScreen
import com.kash.presentation.auth.SignupScreen
import com.kash.presentation.dashboard.DashboardScreen
import com.kash.presentation.profile.ProfileScreen
import com.kash.presentation.spaces.SpacesScreen
import com.kash.presentation.transaction.AddTransactionScreen
import com.kash.presentation.transaction.TransactionsScreen
import com.kash.presentation.theme.KashColors

private data class NavItem(val screen: Screen, val label: String, val icon: ImageVector)

private val navItems = listOf(
    NavItem(Screen.Dashboard,      "Home",        Icons.Outlined.Home),
    NavItem(Screen.Transactions,   "Histórico",   Icons.Outlined.AccountBalanceWallet),
    NavItem(Screen.AddTransaction, "Adicionar",   Icons.Outlined.AddCircleOutline),
    NavItem(Screen.Spaces,         "Espaços",     Icons.Outlined.Layers),
    NavItem(Screen.Profile,        "Perfil",      Icons.Outlined.Person),
)

@Composable
fun KashNavGraph(appViewModel: AppViewModel = hiltViewModel()) {
    val authState by appViewModel.authState.collectAsStateWithLifecycle()

    when (authState) {
        AuthState.Loading          -> SplashScreen()
        AuthState.Unauthenticated  -> AuthNavHost()
        is AuthState.Authenticated -> MainApp(onLogout = { appViewModel.logout() })
    }
}

@Composable
private fun AuthNavHost() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "login") {
        composable("login") {
            LoginScreen(onNavigateToSignup = { navController.navigate("signup") })
        }
        composable("signup") {
            SignupScreen(
                onSignupSuccess   = { navController.popBackStack() },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun SplashScreen() {
    Box(
        modifier          = Modifier.fillMaxSize().background(KashColors.Background),
        contentAlignment  = Alignment.Center
    ) {
        Text("Kash", style = MaterialTheme.typography.displayLarge, color = KashColors.Accent)
    }
}

@Composable
private fun MainApp(onLogout: () -> Unit) {
    val navController = rememberNavController()
    val navBackStack  by navController.currentBackStackEntryAsState()
    val currentDest   = navBackStack?.destination

    Scaffold(
        containerColor = KashColors.Background,
        bottomBar = {
            NavigationBar(containerColor = KashColors.Surface, tonalElevation = 0.dp) {
                navItems.forEach { item ->
                    val isAdd    = item.screen == Screen.AddTransaction
                    val selected = currentDest?.hierarchy?.any { it.route == item.screen.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick  = {
                            navController.navigate(item.screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState    = !isAdd
                            }
                        },
                        modifier = Modifier.weight(1f).heightIn(min = 90.dp).widthIn(min = 85.dp),
                        icon = {
                            if (isAdd) {
                                Icon(
                                    item.icon,
                                    contentDescription = item.label,
                                    modifier = Modifier.size(28.dp),
                                    tint = KashColors.Accent
                                )
                            } else {
                                Icon(item.icon, contentDescription = item.label)
                            }
                        },
                        label  = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor   = KashColors.Accent,
                            selectedTextColor   = KashColors.Accent,
                            unselectedIconColor = KashColors.OnSurfaceFaint,
                            unselectedTextColor = KashColors.OnSurfaceFaint,
                            indicatorColor      = KashColors.SurfaceVariant
                        )
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController    = navController,
            startDestination = Screen.Dashboard.route,
            modifier         = Modifier.padding(padding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(onLogout = onLogout)
            }
            composable(Screen.Transactions.route) {
                TransactionsScreen()
            }
            composable(Screen.AddTransaction.route) {
                AddTransactionScreen(onDone = { navController.popBackStack() })
            }
            composable(Screen.Spaces.route) {
                SpacesScreen()
            }
            composable(Screen.Profile.route) {
                ProfileScreen(onLogout = onLogout)
            }
        }
    }
}
