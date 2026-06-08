package com.foodexpress.data.repository

import com.foodexpress.core.model.CartItem
import com.foodexpress.core.model.MenuItem
import com.foodexpress.core.model.Restaurant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartManager @Inject constructor() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _currentRestaurant = MutableStateFlow<Restaurant?>(null)
    val currentRestaurant: StateFlow<Restaurant?> = _currentRestaurant.asStateFlow()

    val itemCount: Int
        get() = _cartItems.value.sumOf { it.quantity }

    val totalAmount: Double
        get() = _cartItems.value.sumOf { it.subtotal }

    fun setRestaurant(restaurant: Restaurant?) {
        if (_currentRestaurant.value?.id != restaurant?.id) {
            // Different restaurant - clear cart
            _cartItems.value = emptyList()
        }
        _currentRestaurant.value = restaurant
    }

    fun addItem(menuItem: MenuItem) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.menuItem.id == menuItem.id }
        if (index >= 0) {
            current[index] = current[index].copy(quantity = current[index].quantity + 1)
        } else {
            current.add(CartItem(menuItem = menuItem, quantity = 1))
        }
        _cartItems.value = current
    }

    fun removeItem(menuItem: MenuItem) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.menuItem.id == menuItem.id }
        if (index >= 0) {
            if (current[index].quantity > 1) {
                current[index] = current[index].copy(quantity = current[index].quantity - 1)
            } else {
                current.removeAt(index)
            }
        }
        _cartItems.value = current
    }

    fun removeItemCompletely(menuItem: MenuItem) {
        _cartItems.value = _cartItems.value.filter { it.menuItem.id != menuItem.id }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _currentRestaurant.value = null
    }
}
