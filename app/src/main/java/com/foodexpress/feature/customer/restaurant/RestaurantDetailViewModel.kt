package com.foodexpress.feature.customer.restaurant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foodexpress.core.model.*
import com.foodexpress.data.repository.RestaurantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RestaurantDetailUiState(
    val isLoading: Boolean = false,
    val restaurant: Restaurant? = null,
    val menuItems: List<MenuItem> = emptyList(),
    val categories: List<MenuCategory> = emptyList(),
    val selectedCategoryId: String? = null,
    val cartItems: List<CartItem> = emptyList(),
    val error: String? = null
) {
    val filteredMenuItems: List<MenuItem>
        get() = if (selectedCategoryId == null) menuItems
        else menuItems.filter { it.categoryId == selectedCategoryId }

    val cartItemCount: Int
        get() = cartItems.sumOf { it.quantity }

    val cartTotal: Double
        get() = cartItems.sumOf { it.subtotal }
}

@HiltViewModel
class RestaurantDetailViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RestaurantDetailUiState())
    val uiState = _uiState.asStateFlow()

    fun loadRestaurant(restaurantId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Load restaurant info
            restaurantRepository.getRestaurantById(restaurantId).fold(
                onSuccess = { restaurant ->
                    _uiState.value = _uiState.value.copy(
                        restaurant = restaurant,
                        categories = restaurant.categories
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(error = e.localizedMessage)
                }
            )

            // Load menu items
            restaurantRepository.getMenuItems(restaurantId).fold(
                onSuccess = { items ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        menuItems = items
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.localizedMessage
                    )
                }
            )
        }
    }

    fun selectCategory(categoryId: String?) {
        _uiState.value = _uiState.value.copy(selectedCategoryId = categoryId)
    }

    fun addToCart(menuItem: MenuItem) {
        val currentCart = _uiState.value.cartItems.toMutableList()
        val existingIndex = currentCart.indexOfFirst { it.menuItem.id == menuItem.id }

        if (existingIndex >= 0) {
            val existing = currentCart[existingIndex]
            currentCart[existingIndex] = existing.copy(quantity = existing.quantity + 1)
        } else {
            currentCart.add(CartItem(menuItem = menuItem, quantity = 1))
        }

        _uiState.value = _uiState.value.copy(cartItems = currentCart)
    }

    fun removeFromCart(menuItem: MenuItem) {
        val currentCart = _uiState.value.cartItems.toMutableList()
        val existingIndex = currentCart.indexOfFirst { it.menuItem.id == menuItem.id }

        if (existingIndex >= 0) {
            val existing = currentCart[existingIndex]
            if (existing.quantity > 1) {
                currentCart[existingIndex] = existing.copy(quantity = existing.quantity - 1)
            } else {
                currentCart.removeAt(existingIndex)
            }
        }

        _uiState.value = _uiState.value.copy(cartItems = currentCart)
    }
}
