package com.foodexpress.feature.customer.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foodexpress.core.model.CartItem
import com.foodexpress.core.model.MenuItem
import com.foodexpress.core.model.Restaurant
import com.foodexpress.data.repository.CartManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class CartUiState(
    val cartItems: List<CartItem> = emptyList(),
    val currentRestaurant: Restaurant? = null
) {
    val itemCount: Int get() = cartItems.sumOf { it.quantity }
    val totalAmount: Double get() = cartItems.sumOf { it.subtotal }
}

@HiltViewModel
class CartViewModel @Inject constructor(
    cartManager: CartManager
) : ViewModel() {

    val uiState: StateFlow<CartUiState> = combine(
        cartManager.cartItems,
        cartManager.currentRestaurant
    ) { items, restaurant ->
        CartUiState(cartItems = items, currentRestaurant = restaurant)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CartUiState())

    fun increaseQuantity(menuItem: MenuItem) {
        cartManager.addItem(menuItem)
    }

    fun decreaseQuantity(menuItem: MenuItem) {
        cartManager.removeItem(menuItem)
    }

    fun removeItem(menuItem: MenuItem) {
        cartManager.removeItemCompletely(menuItem)
    }

    fun clearCart() {
        cartManager.clearCart()
    }
}
