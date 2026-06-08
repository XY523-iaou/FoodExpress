package com.foodexpress.feature.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.foodexpress.core.model.UserRole

@Composable
fun RoleSelectScreen(
    viewModel: AuthViewModel,
    onContinue: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "选择您的角色",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "您可以使用同一账号切换角色",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(48.dp))

        RoleCard(
            icon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(48.dp)) },
            title = "我是顾客",
            description = "浏览餐厅，点外卖，享受美食配送",
            isSelected = uiState.selectedRole == UserRole.CUSTOMER,
            onClick = { viewModel.selectRole(UserRole.CUSTOMER) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        RoleCard(
            icon = { Icon(Icons.Default.Store, contentDescription = null, modifier = Modifier.size(48.dp)) },
            title = "我是商家",
            description = "管理菜单，接收订单，经营餐厅",
            isSelected = uiState.selectedRole == UserRole.MERCHANT,
            onClick = { viewModel.selectRole(UserRole.MERCHANT) }
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("继 续", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun RoleCard(
    icon: @Composable () -> Unit,
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected)
            CardDefaults.outlinedCardBorder().copy(
            )
        else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
