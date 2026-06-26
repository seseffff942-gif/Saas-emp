package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ui.navigation.Screen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.ui.draw.shadow

@Composable
fun BottomNavBar(navController: NavController, currentRoute: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .windowInsetsPadding(WindowInsets.navigationBars),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(32.dp), spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                .clip(RoundedCornerShape(32.dp))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                icon = Icons.Default.Dashboard,
                label = "Inicio",
                isSelected = currentRoute == Screen.Dashboard.route,
                onClick = { navController.navigate(Screen.Dashboard.route) { popUpTo(Screen.Dashboard.route) { inclusive = true } } }
            )
            NavItem(
                icon = Icons.Default.Inventory2,
                label = "Inventario",
                isSelected = currentRoute == Screen.Inventory.route,
                onClick = { navController.navigate(Screen.Inventory.route) { popUpTo(Screen.Dashboard.route) } }
            )
            NavItem(
                icon = Icons.Default.PointOfSale,
                label = "Ventas",
                isSelected = currentRoute == Screen.Sales.route,
                onClick = { navController.navigate(Screen.Sales.route) { popUpTo(Screen.Dashboard.route) } }
            )
            NavItem(
                icon = Icons.Default.Payments,
                label = "Gastos",
                isSelected = currentRoute == Screen.Expenses.route,
                onClick = { navController.navigate(Screen.Expenses.route) { popUpTo(Screen.Dashboard.route) } }
            )
        }
    }
}

@Composable
fun NavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val iconBgColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else androidx.compose.ui.graphics.Color.Transparent
    val iconColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    val textColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(32.dp)
                .clip(CircleShape)
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}
