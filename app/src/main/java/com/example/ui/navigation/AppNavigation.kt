package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ui.AppViewModel
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.InventoryScreen
import com.example.ui.screens.SalesScreen
import com.example.ui.screens.ExpensesScreen
import com.example.ui.screens.AddProductScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object Inventory : Screen("inventory")
    object Sales : Screen("sales")
    object Expenses : Screen("expenses")
    object AddProduct : Screen("add_product")
    object Reports : Screen("reports")
    object Settings : Screen("settings")
    object Subscription : Screen("subscription")
    object BusinessDetails : Screen("business_details")
    object Team : Screen("team")
    object Billing : Screen("billing")
    object Devices : Screen("devices")
}

@Composable
fun AppNavigation(navController: NavHostController, viewModel: AppViewModel) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            com.example.ui.screens.LoginScreen(navController, viewModel)
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController, viewModel)
        }
        composable(Screen.Inventory.route) {
            InventoryScreen(navController, viewModel)
        }
        composable(Screen.Sales.route) {
            SalesScreen(navController, viewModel)
        }
        composable(Screen.Expenses.route) {
            ExpensesScreen(navController, viewModel)
        }
        composable(Screen.AddProduct.route) {
            AddProductScreen(navController, viewModel)
        }
        composable(Screen.Reports.route) {
            com.example.ui.screens.ReportsScreen(navController, viewModel)
        }
        composable(Screen.Settings.route) {
            com.example.ui.screens.SettingsScreen(navController, viewModel)
        }
        composable(Screen.Subscription.route) {
            com.example.ui.screens.SubscriptionScreen(navController)
        }
        composable(Screen.BusinessDetails.route) {
            com.example.ui.screens.BusinessDetailsScreen(navController)
        }
        composable(Screen.Team.route) {
            com.example.ui.screens.TeamScreen(navController)
        }
        composable(Screen.Billing.route) {
            com.example.ui.screens.BillingScreen(navController)
        }
        composable(Screen.Devices.route) {
            com.example.ui.screens.DevicesScreen(navController)
        }
    }
}
