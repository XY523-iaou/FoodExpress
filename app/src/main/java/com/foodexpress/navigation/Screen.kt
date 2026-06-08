package com.foodexpress.navigation

/**
 * Defines all navigation destinations in the app.
 */
sealed class Screen(val route: String) {
    // Auth
    data object Login : Screen("auth/login")
    data object Register : Screen("auth/register")
    data object RoleSelect : Screen("auth/role_select")

    // Customer
    data object Home : Screen("customer/home")
    data object RestaurantDetail : Screen("customer/restaurant/{restaurantId}") {
        fun createRoute(restaurantId: String) = "customer/restaurant/$restaurantId"
    }
    data object Cart : Screen("customer/cart")
    data object Checkout : Screen("customer/checkout")
    data object OrderTracking : Screen("customer/orders")
    data object OrderDetail : Screen("customer/order/{orderId}") {
        fun createRoute(orderId: String) = "customer/order/$orderId"
    }
    data object Profile : Screen("customer/profile")
    data object AddressManager : Screen("customer/addresses")

    // Merchant
    data object MerchantDashboard : Screen("merchant/dashboard")
    data object MerchantMenu : Screen("merchant/menu")
    data object MerchantOrders : Screen("merchant/orders")
    data object AddEditItem : Screen("merchant/menu/item/{itemId}") {
        fun createRoute(itemId: String = "new") = "merchant/menu/item/$itemId"
    }
}
