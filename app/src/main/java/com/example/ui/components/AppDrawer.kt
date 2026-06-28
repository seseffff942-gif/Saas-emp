package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ui.navigation.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AppDrawer(
    navController: NavController,
    currentRoute: String?,
    drawerState: DrawerState,
    coroutineScope: CoroutineScope
) {
    ModalDrawerSheet(
        drawerContainerColor = Color(0xFF0F172A),
        drawerContentColor = Color.White,
        modifier = Modifier.width(300.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 24.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF10B981)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Inventory2, contentDescription = "Logo", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text("Pro SAAS", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            // Sync Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF10B981).copy(alpha = 0.2f))
                    .border(1.dp, Color(0xFF10B981).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFF10B981)))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SINCRONIZADO (TIEMPO REAL)", color = Color(0xFF34D399), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            val items = listOf(
                Triple(Screen.Dashboard.route, "Panel de Control", Icons.Default.Dashboard),
                Triple(Screen.Inventory.route, "Bodega", Icons.Default.Inventory2),
                Triple(Screen.Sales.route, "Ventas", Icons.Default.ShoppingCart),
                Triple(Screen.Expenses.route, "Finanzas y Cuentas", Icons.Default.AccountBalanceWallet),
                Triple(Screen.Reports.route, "Proveedores", Icons.Default.LocalShipping), // Mapping to Reports for now
                Triple(Screen.Team.route, "Directorio Premium", Icons.Default.BusinessCenter), // Mapping to Team
                Triple(Screen.Settings.route, "Equipo", Icons.Default.Group)
            )

            items.forEach { (route, label, icon) ->
                DrawerItem(
                    icon = icon,
                    label = label,
                    isSelected = currentRoute == route,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        navController.navigate(route) {
                            popUpTo(Screen.Dashboard.route)
                            launchSingleTop = true
                        }
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Spacer(modifier = Modifier.weight(1f))
            
            DrawerItem(
                icon = Icons.Default.ExitToApp,
                label = "Cerrar Sesión",
                isSelected = false,
                textColor = Color(0xFFFB7185),
                onClick = {
                    coroutineScope.launch { drawerState.close() }
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun DrawerItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    textColor: Color = if (isSelected) Color(0xFF34D399) else Color(0xFF94A3B8),
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) Color(0xFF10B981).copy(alpha = 0.1f) else Color.Transparent
    val borderColor = if (isSelected) Color(0xFF10B981).copy(alpha = 0.2f) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = label, tint = textColor, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}
