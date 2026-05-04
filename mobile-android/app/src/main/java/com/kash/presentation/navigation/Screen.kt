package com.kash.presentation.navigation

sealed class Screen(val route: String) {
    data object Dashboard      : Screen("dashboard")
    data object Transactions   : Screen("transactions")
    data object Spaces         : Screen("spaces")
    data object AddTransaction : Screen("add_transaction")
    data object Profile        : Screen("profile")
}
