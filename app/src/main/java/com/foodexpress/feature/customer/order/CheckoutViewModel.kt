package com.foodexpress.feature.customer.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foodexpress.core.model.*
import com.foodexpress.data.repository.CartManager
import com.foodexpress.data.repository.OrderRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CheckoutUiState(
    val isLoading: Boolean = false,
    val cartItems: List<CartItem> = emptyList(),
    val restaurantName: String = "",
    val restaurantId: String = "",
    val totalAmount: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val error: String? = null,
    val orderId: String? = null
)

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartManager: CartManager,
    private val orderRepository: OrderRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val restaurant = cartManager.currentRestaurant.value
        val items = cartManager.cartItems.value
        _uiState.value = CheckoutUiState(
            cartItems = items,
            restaurantName = restaurant?.name ?: "",
            restaurantId = restaurant?.id ?: "",
            totalAmount = items.sumOf { it.subtotal } + (restaurant?.deliveryFee ?: 0.0),
            deliveryFee = restaurant?.deliveryFee ?: 0.0
        )
    }

    fun placeOrder(address: String, phone: String, note: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val state = _uiState.value

            val order = Order(
                customerId = auth.currentUser?.uid ?: "",
                customerName = auth.currentUser?.displayName ?: "用户",
                restaurantId = state.restaurantId,
                restaurantName = state.restaurantName,
                items = state.cartItems,
                totalAmount = state.totalAmount,
                deliveryFee = state.deliveryFee,
                deliveryAddress = Address(fullAddress = address, contactPhone = phone),
                contactPhone = phone,
                note = note
            )

            orderRepository.placeOrder(order).fold(
                onSuccess = { orderId ->
                    cartManager.clearCart()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        orderId = orderId
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.localizedMessage ?: "下单失败，请重试"
                    )
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
