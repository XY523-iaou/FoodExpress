package com.foodexpress.feature.merchant.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MerchantOrdersScreen(
    onBackClick: () -> Unit
) {
    val orders = remember {
        listOf(
            MockOrder("ORD001", "张三", "招牌炸酱面 x2, 拍黄瓜 x1", 44.0, "已下单", "5分钟前"),
            MockOrder("ORD002", "李四", "红烧牛肉面 x1, 酸梅汤 x2", 40.0, "准备中", "15分钟前"),
            MockOrder("ORD003", "王五", "招牌炸酱面 x3", 54.0, "配送中", "30分钟前"),
            MockOrder("ORD004", "赵六", "拍黄瓜 x2, 红烧牛肉面 x1", 44.0, "已送达", "1小时前"),
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("订单管理") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(orders) { order ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("订单 #${order.id}", style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold)
                            Text(order.time, style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("顾客: ${order.customerName}", style = MaterialTheme.typography.bodyMedium)
                        Text(order.items, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = when (order.status) {
                                    "已下单" -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                                    "准备中" -> MaterialTheme.colorScheme.primaryContainer
                                    "配送中" -> MaterialTheme.colorScheme.secondaryContainer
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                },
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    order.status,
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Text("¥${String.format("%.2f", order.amount)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary)
                        }

                        if (order.status == "已下单") {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                    )
                                ) { Text("拒单") }
                                Button(
                                    onClick = { },
                                    modifier = Modifier.weight(1f)
                                ) { Text("接单") }
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class MockOrder(
    val id: String,
    val customerName: String,
    val items: String,
    val amount: Double,
    val status: String,
    val time: String
)
