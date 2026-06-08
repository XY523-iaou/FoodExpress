package com.foodexpress.feature.customer.restaurant

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.foodexpress.core.model.CartItem
import com.foodexpress.core.model.MenuItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailScreen(
    restaurantId: String,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit,
    viewModel: RestaurantDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(restaurantId) {
        viewModel.loadRestaurant(restaurantId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        uiState.restaurant?.name ?: "餐厅详情",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = onCartClick) {
                        BadgedBox(
                            badge = {
                                if (uiState.cartItemCount > 0) {
                                    Badge { Text("${uiState.cartItemCount}") }
                                }
                            }
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "购物车")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                return@LazyColumn
            }

            item {
                RestaurantHeader(
                    restaurant = uiState.restaurant,
                    menuItems = uiState.menuItems
                )
            }

            item {
                CategoryTabs(
                    categories = uiState.categories,
                    selectedCategoryId = uiState.selectedCategoryId,
                    onCategorySelected = viewModel::selectCategory
                )
            }

            items(uiState.filteredMenuItems) { item ->
                MenuItemCard(
                    menuItem = item,
                    cartQuantity = uiState.cartItems.find { it.menuItem.id == item.id }?.quantity ?: 0,
                    onAddToCart = { viewModel.addToCart(item) },
                    onRemoveFromCart = { viewModel.removeFromCart(item) },
                    onIncreaseQuantity = { viewModel.addToCart(item) }
                )
            }

            if (uiState.filteredMenuItems.isEmpty() && !uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "暂无菜品",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun RestaurantHeader(
    restaurant: com.foodexpress.core.model.Restaurant?,
    menuItems: List<MenuItem>
) {
    if (restaurant == null) return

    Column {
        AsyncImage(
            model = restaurant.imageUrl,
            contentDescription = restaurant.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        )

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = restaurant.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${restaurant.rating} (${restaurant.reviewCount}条评价)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.DeliveryDining,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "配送费¥${String.format("%.0f", restaurant.deliveryFee)} · ${restaurant.deliveryTime}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "起送¥${String.format("%.0f", restaurant.minOrderAmount)} · ${restaurant.category} · ${restaurant.address}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = restaurant.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        HorizontalDivider()
    }
}

@Composable
fun CategoryTabs(
    categories: List<com.foodexpress.core.model.MenuCategory>,
    selectedCategoryId: String?,
    onCategorySelected: (String?) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = categories.indexOfFirst { it.id == selectedCategoryId }.coerceAtLeast(0),
        modifier = Modifier.fillMaxWidth(),
        edgePadding = 16.dp,
        divider = {},
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Tab(
            selected = selectedCategoryId == null,
            onClick = { onCategorySelected(null) },
            text = { Text("全部") }
        )
        categories.forEach { category ->
            Tab(
                selected = selectedCategoryId == category.id,
                onClick = { onCategorySelected(category.id) },
                text = { Text(category.name) }
            )
        }
    }
    HorizontalDivider()
}

@Composable
fun MenuItemCard(
    menuItem: MenuItem,
    cartQuantity: Int,
    onAddToCart: () -> Unit,
    onRemoveFromCart: () -> Unit,
    onIncreaseQuantity: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            AsyncImage(
                model = menuItem.imageUrl,
                contentDescription = menuItem.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = menuItem.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    menuItem.tags.take(2).forEach { tag ->
                        Spacer(modifier = Modifier.width(4.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = MaterialTheme.shapes.extraSmall
                        ) {
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = menuItem.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "¥${String.format("%.2f", menuItem.price)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (cartQuantity > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            SmallFloatingActionButton(
                                onClick = onRemoveFromCart,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Icon(
                                    Icons.Default.Remove,
                                    contentDescription = "减少",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "$cartQuantity",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            SmallFloatingActionButton(
                                onClick = onIncreaseQuantity,
                                containerColor = MaterialTheme.colorScheme.primary
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "增加",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    } else {
                        IconButton(
                            onClick = onAddToCart,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.AddCircle,
                                contentDescription = "加入购物车",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                if (menuItem.salesCount > 0) {
                    Text(
                        text = "已售${menuItem.salesCount}份",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
