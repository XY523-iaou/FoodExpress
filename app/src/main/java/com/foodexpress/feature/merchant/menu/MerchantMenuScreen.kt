package com.foodexpress.feature.merchant.menu

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
fun MerchantMenuScreen(
    onBackClick: () -> Unit,
    onAddItemClick: () -> Unit
) {
    // Mock menu items for now
    val menuItems = remember {
        listOf(
            MenuItemData("1", "招牌炸酱面", "面食", 18.0, true),
            MenuItemData("2", "红烧牛肉面", "面食", 28.0, true),
            MenuItemData("3", "拍黄瓜", "小菜", 8.0, true),
            MenuItemData("4", "酸梅汤", "饮品", 6.0, false),
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("菜单管理") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddItemClick) {
                Icon(Icons.Default.Add, contentDescription = "添加菜品")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(menuItems) { item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.name, style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${item.category} · ¥${String.format("%.2f", item.price)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Surface(
                            color = if (item.isAvailable)
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = if (item.isAvailable) "在售" else "已下架",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = { /* edit */ }) {
                            Icon(Icons.Default.Edit, contentDescription = "编辑", modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}

private data class MenuItemData(
    val id: String,
    val name: String,
    val category: String,
    val price: Double,
    val isAvailable: Boolean
)
