package com.kash.presentation.navigation

sealed class Screen(val route: String) {
    data object Dashboard   : Screen("dashboard")
    data object Insights    : Screen("insights")
    data object Products    : Screen("products")
    data object Transactions: Screen("transactions")
    data object Loss        : Screen("loss")
    data object History     : Screen("history")
}
