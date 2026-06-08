package com.foodexpress.feature.customer.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foodexpress.core.model.Order
import com.foodexpress.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrderDetailUiState(
    val isLoading: Boolean = false,
    val order: Order? = null,
    val error: String? = null
)

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderDetailUiState())
    val uiState = _uiState.asStateFlow()

    fun loadOrder(orderId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            orderRepository.observeOrder(orderId).collect { order ->
                _uiState.value = OrderDetailUiState(order = order)
            }
        }
    }
}
