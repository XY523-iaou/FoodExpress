package com.foodexpress.core.model

enum class UserRole { CUSTOMER, MERCHANT }

enum class OrderStatus {
    PLACED,
    CONFIRMED,
    PREPARING,
    DELIVERING,
    DELIVERED,
    CANCELLED
}

data class Address(
    val id: String = "",
    val label: String = "",
    val fullAddress: String = "",
    val contactPhone: String = "",
    val isDefault: Boolean = false
)

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: UserRole = UserRole.CUSTOMER,
    val avatarUrl: String? = null,
    val addresses: List<Address> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

data class Restaurant(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val category: String = "",
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val address: String = "",
    val isOpen: Boolean = true,
    val merchantId: String = "",
    val deliveryFee: Double = 0.0,
    val minOrderAmount: Double = 0.0,
    val deliveryTime: String = "30-45分钟",
    val categories: List<MenuCategory> = emptyList()
)

data class MenuCategory(
    val id: String = "",
    val name: String = "",
    val sortOrder: Int = 0
)

data class MenuItem(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val isAvailable: Boolean = true,
    val categoryId: String = "",
    val restaurantId: String = "",
    val tags: List<String> = emptyList(),
    val salesCount: Int = 0
)

data class CartItem(
    val menuItem: MenuItem,
    val quantity: Int = 1,
    val note: String = ""
) {
    val subtotal: Double get() = menuItem.price * quantity
}

data class Order(
    val id: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val restaurantId: String = "",
    val restaurantName: String = "",
    val items: List<CartItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val status: OrderStatus = OrderStatus.PLACED,
    val deliveryAddress: Address = Address(),
    val contactPhone: String = "",
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
