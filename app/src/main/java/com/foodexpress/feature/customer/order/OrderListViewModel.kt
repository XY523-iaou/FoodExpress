package com.foodexpress.feature.customer.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foodexpress.core.model.Order
import com.foodexpress.data.repository.OrderRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrderListUiState(
    val isLoading: Boolean = false,
    val orders: List<Order> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class OrderListViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadOrders()
        observeOrders()
    }

    private fun loadOrders() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val userId = auth.currentUser?.uid ?: return@launch
            orderRepository.getOrders(userId).fold(
                onSuccess = { orders ->
                    _uiState.value = OrderListUiState(orders = orders)
                },
                onFailure = { e ->
                    _uiState.value = OrderListUiState(error = e.localizedMessage)
                }
            )
        }
    }

    private fun observeOrders() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            orderRepository.observeOrders(userId).collect { orders ->
                _uiState.value = OrderListUiState(orders = orders)
            }
        }
    }
}
