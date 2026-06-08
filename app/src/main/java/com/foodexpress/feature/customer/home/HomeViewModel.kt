package com.foodexpress.feature.customer.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foodexpress.core.model.Restaurant
import com.foodexpress.data.repository.RestaurantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val restaurants: List<Restaurant> = emptyList(),
    val categories: List<String> = listOf("全部", "中餐", "日料", "快餐", "东南亚", "西餐"),
    val selectedCategory: String = "全部",
    val searchQuery: String = "",
    val error: String? = null,
    val showSeedButton: Boolean = false  // Show when no restaurants exist
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadRestaurants()
    }

    fun loadRestaurants() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            restaurantRepository.getRestaurants().fold(
                onSuccess = { restaurants ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        restaurants = restaurants,
                        showSeedButton = restaurants.isEmpty()
                    )
                    applyFilters()
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.localizedMessage,
                        showSeedButton = true
                    )
                }
            )
        }
    }

    fun selectCategory(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        applyFilters()
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        if (query.length >= 2) {
            searchRestaurants(query)
        } else if (query.isEmpty()) {
            loadRestaurants()
        }
    }

    fun seedData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            restaurantRepository.seedSampleData()
            loadRestaurants()
        }
    }

    private fun searchRestaurants(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            restaurantRepository.searchRestaurants(query).fold(
                onSuccess = { results ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        restaurants = results
                    )
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            )
        }
    }

    private fun applyFilters() {
        val state = _uiState.value
        val filtered = state.restaurants.filter { restaurant ->
            (state.selectedCategory == "全部" || restaurant.category == state.selectedCategory)
        }
        _uiState.value = state.copy(restaurants = filtered)
    }
}
