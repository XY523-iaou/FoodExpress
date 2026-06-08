package com.foodexpress.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.foodexpress.feature.auth.AuthViewModel
import com.foodexpress.feature.auth.LoginScreen
import com.foodexpress.feature.auth.RegisterScreen
import com.foodexpress.feature.auth.RoleSelectScreen
import com.foodexpress.feature.customer.cart.CartScreen
import com.foodexpress.feature.customer.home.HomeScreen
import com.foodexpress.feature.customer.order.CheckoutScreen
import com.foodexpress.feature.customer.order.OrderDetailScreen
import com.foodexpress.feature.customer.order.OrderListScreen
import com.foodexpress.feature.customer.profile.ProfileScreen
import com.foodexpress.feature.customer.restaurant.RestaurantDetailScreen
import com.foodexpress.feature.merchant.dashboard.MerchantDashboardScreen
import com.foodexpress.feature.merchant.menu.MerchantMenuScreen
import com.foodexpress.feature.merchant.orders.MerchantOrdersScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    val authViewModel: AuthViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        // Auth
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToRoleSelect = { navController.navigate(Screen.RoleSelect.route) },
                onLoginSuccess = { isCustomer ->
                    val dest = if (isCustomer) Screen.Home.route else Screen.MerchantDashboard.route
                    navController.navigate(dest) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = { navController.popBackStack() },
                onNavigateToRoleSelect = { navController.navigate(Screen.RoleSelect.route) },
                onRegisterSuccess = { isCustomer ->
                    val dest = if (isCustomer) Screen.Home.route else Screen.MerchantDashboard.route
                    navController.navigate(dest) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.RoleSelect.route) {
            RoleSelectScreen(
                viewModel = authViewModel,
                onContinue = { navController.popBackStack() }
            )
        }

        // Customer - Home
        composable(Screen.Home.route) {
            HomeScreen(
                onRestaurantClick = { id -> navController.navigate(Screen.RestaurantDetail.createRoute(id)) },
                onCartClick = { navController.navigate(Screen.Cart.route) },
                onOrdersClick = { navController.navigate(Screen.OrderTracking.route) },
                onProfileClick = { navController.navigate(Screen.Profile.route) }
            )
        }

        // Customer - Restaurant detail
        composable(
            route = Screen.RestaurantDetail.route,
            arguments = listOf(navArgument("restaurantId") { type = NavType.StringType })
        ) { entry ->
            val restaurantId = entry.arguments?.getString("restaurantId") ?: ""
            RestaurantDetailScreen(
                restaurantId = restaurantId,
                onBackClick = { navController.popBackStack() },
                onCartClick = { navController.navigate(Screen.Cart.route) }
            )
        }

        // Customer - Cart
        composable(Screen.Cart.route) {
            CartScreen(
                onBackClick = { navController.popBackStack() },
                onCheckoutClick = { navController.navigate(Screen.Checkout.route) }
            )
        }

        // Customer - Checkout
        composable(Screen.Checkout.route) {
            CheckoutScreen(
                onBackClick = { navController.popBackStack() },
                onOrderSuccess = { orderId ->
                    navController.navigate(Screen.OrderDetail.createRoute(orderId)) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        // Customer - Orders
        composable(Screen.OrderTracking.route) {
            OrderListScreen(
                onBackClick = { navController.popBackStack() },
                onOrderClick = { orderId -> navController.navigate(Screen.OrderDetail.createRoute(orderId)) }
            )
        }

        composable(
            route = Screen.OrderDetail.route,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { entry ->
            val orderId = entry.arguments?.getString("orderId") ?: ""
            OrderDetailScreen(
                orderId = orderId,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Customer - Profile
        composable(Screen.Profile.route) {
            ProfileScreen(onBackClick = { navController.popBackStack() })
        }

        // Merchant
        composable(Screen.MerchantDashboard.route) {
            MerchantDashboardScreen(
                onMenuClick = { navController.navigate(Screen.MerchantMenu.route) },
                onOrdersClick = { navController.navigate(Screen.MerchantOrders.route) },
                onLogoutClick = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.MerchantMenu.route) {
            com.foodexpress.feature.merchant.menu.MerchantMenuScreen(
                onBackClick = { navController.popBackStack() },
                onAddItemClick = { navController.navigate(Screen.AddEditItem.createRoute()) }
            )
        }

        composable(Screen.MerchantOrders.route) {
            MerchantOrdersScreen(onBackClick = { navController.popBackStack() })
        }
    }
}
