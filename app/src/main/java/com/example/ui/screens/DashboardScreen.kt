package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.example.ui.AppViewModel
import com.example.ui.components.BottomNavBar
import com.example.ui.navigation.Screen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    navController: NavController, 
    viewModel: AppViewModel,
    drawerState: DrawerState,
    coroutineScope: kotlinx.coroutines.CoroutineScope
) {
    val allProducts by viewModel.allProducts.collectAsStateWithLifecycle()
    val lowStock by viewModel.lowStockProducts.collectAsStateWithLifecycle()
    val allLogs by viewModel.allFinanceLogs.collectAsStateWithLifecycle()
    
    val totalSalesToday by androidx.compose.runtime.remember(allLogs) {
        androidx.compose.runtime.derivedStateOf { 
            allLogs.filter { it.type == "INCOME" }.sumOf { it.amount } 
        }
    }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Panel de Control") },
                navigationIcon = {
                    IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.Sales.route) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(Icons.Default.AddShoppingCart, "Nueva Venta")
            }
        },
        // Removed contentWindowInsets to ensure default safe insets are respected
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding())
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 24.dp)
                    .padding(bottom = 120.dp), // Extra padding for the floating nav bar
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SAAS MICRO",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 2.sp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Dashboard",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .border(2.dp, MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = "Perfil", tint = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }

                // Welcome Section
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Buenos días 👋",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.SemiBold
                    )
                    val dateFormat = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())
                    Text(
                        text = dateFormat.format(Date()).uppercase(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }

                // KPI Cards
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    KpiCard(
                        title = "VENTAS DE HOY",
                        value = "Q ${"%.2f".format(totalSalesToday)}",
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        iconTint = MaterialTheme.colorScheme.onPrimary,
                        isPrimary = true
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        KpiCard(
                            title = "ARTÍCULOS",
                            value = "${allProducts.size}",
                            icon = Icons.Default.Inventory2,
                            modifier = Modifier.weight(1f)
                        )
                        KpiCard(
                            title = "BAJO STOCK",
                            value = "${lowStock.size}",
                            isAlert = lowStock.isNotEmpty(),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Quick Actions
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "Acciones Rápidas",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        QuickActionButton(
                            title = "Nueva Venta",
                            icon = Icons.Default.AddShoppingCart,
                            isPrimary = true,
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(Screen.Sales.route) }
                        )
                        QuickActionButton(
                            title = "Inventario",
                            icon = Icons.Default.Inventory2,
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(Screen.Inventory.route) }
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        QuickActionButton(
                            title = "Reg. Gasto",
                            icon = Icons.Default.Payments,
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(Screen.Expenses.route) }
                        )
                        QuickActionButton(
                            title = "Reportes",
                            icon = Icons.Default.BarChart,
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(Screen.Reports.route) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun KpiCard(
    title: String,
    value: String,
    icon: ImageVector? = null,
    iconTint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
    isAlert: Boolean = false,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false
) {
    val bgColor = if (isAlert) MaterialTheme.colorScheme.errorContainer else if (isPrimary) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
    val contentColor = if (isAlert) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurface
    val labelColor = if (isAlert) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
    val borderColor = if (isAlert) MaterialTheme.colorScheme.errorContainer.copy(alpha=0.5f) else MaterialTheme.colorScheme.outlineVariant
    val cornerRadius = 24.dp

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp),
        shape = RoundedCornerShape(cornerRadius),
        color = bgColor,
        shadowElevation = if (isPrimary) 8.dp else 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = title, style = MaterialTheme.typography.labelMedium, color = labelColor, fontWeight = FontWeight.Bold)
                if (icon != null) {
                    Icon(icon, contentDescription = null, tint = if (isAlert) MaterialTheme.colorScheme.error else iconTint, modifier = Modifier.size(24.dp))
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineLarge,
                    color = contentColor,
                    fontWeight = FontWeight.Black
                )
                if (isAlert) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.error)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("REVISAR", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onError, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionButton(
    title: String,
    icon: ImageVector,
    isPrimary: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isPrimary) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val contentColor = if (isPrimary) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    val iconTint = if (isPrimary) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary

    Surface(
        onClick = onClick,
        modifier = modifier.height(130.dp),
        shape = RoundedCornerShape(24.dp),
        color = bgColor,
        shadowElevation = 4.dp,
        border = if (isPrimary) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (isPrimary) MaterialTheme.colorScheme.surface.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = iconTint, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = title, style = MaterialTheme.typography.labelMedium, color = contentColor, fontWeight = FontWeight.SemiBold)
        }
    }
}
