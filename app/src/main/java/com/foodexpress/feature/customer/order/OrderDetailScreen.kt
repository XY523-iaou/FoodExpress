package com.foodexpress.feature.customer.order

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.foodexpress.core.model.OrderStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderId: String,
    onBackClick: () -> Unit,
    viewModel: OrderDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(orderId) {
        viewModel.loadOrder(orderId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("订单详情") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val order = uiState.order ?: return@Scaffold

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card {
                    Column(Modifier.padding(16.dp)) {
                        Text("订单状态", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(16.dp))
                        OrderStatusTracker(currentStatus = order.status)
                    }
                }
            }

            item {
                Card {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Store, null, Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(order.restaurantName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("订单号: ${order.id.take(8).uppercase()}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            item {
                Card {
                    Column(Modifier.padding(16.dp)) {
                        Text("菜品明细", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        order.items.forEach { cartItem ->
                            Row(
                                Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${cartItem.menuItem.name} ×${cartItem.quantity}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f))
                                Text("¥${String.format("%.2f", cartItem.subtotal)}",
                                    style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                        HorizontalDivider(Modifier.padding(vertical = 8.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("配送费", style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("¥${String.format("%.2f", order.deliveryFee)}",
                                style = MaterialTheme.typography.bodyMedium)
                        }
                        Spacer(Modifier.height(4.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("合计", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text("¥${String.format("%.2f", order.totalAmount)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            item {
                Card {
                    Column(Modifier.padding(16.dp)) {
                        Text("配送信息", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, null, Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.width(8.dp))
                            Text(order.deliveryAddress.fullAddress, style = MaterialTheme.typography.bodyMedium)
                        }
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, null, Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.width(8.dp))
                            Text(order.contactPhone, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            if (order.note.isNotBlank()) {
                item {
                    Card {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.Notes, null, Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.width(8.dp))
                            Text("备注: ${order.note}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            item {
                Text(
                    text = "下单时间: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date(order.createdAt))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun OrderStatusTracker(currentStatus: OrderStatus) {
    val steps = listOf(
        OrderStatus.PLACED to "已下单",
        OrderStatus.CONFIRMED to "已接单",
        OrderStatus.PREPARING to "制作中",
        OrderStatus.DELIVERING to "配送中",
        OrderStatus.DELIVERED to "已送达"
    )

    val currentIndex = steps.indexOfFirst { it.first == currentStatus }
    val isCancelled = currentStatus == OrderStatus.CANCELLED

    if (isCancelled) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Cancel, null, Modifier.size(24.dp), tint = MaterialTheme.colorScheme.error)
            Spacer(Modifier.width(8.dp))
            Text("订单已取消", style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.error)
        }
        return
    }

    steps.forEachIndexed { index, (_, label) ->
        val isCompleted = index <= currentIndex
        val isCurrent = index == currentIndex

        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(if (isCurrent) 28.dp else 24.dp),
                shape = MaterialTheme.shapes.extraLarge,
                color = when {
                    isCompleted -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (isCompleted) {
                        Icon(
                            Icons.Default.Check, null,
                            modifier = Modifier.size(if (isCurrent) 18.dp else 14.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            Spacer(Modifier.width(8.dp))

            Text(
                text = label,
                style = if (isCurrent) MaterialTheme.typography.labelLarge
                    else MaterialTheme.typography.labelMedium,
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                color = if (isCompleted) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (index < steps.size - 1) {
                Spacer(Modifier.width(8.dp))
                HorizontalDivider(
                    modifier = Modifier.weight(1f).padding(vertical = 12.dp),
                    thickness = 2.dp,
                    color = if (index < currentIndex) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(Modifier.width(8.dp))
            }
        }
    }
}
