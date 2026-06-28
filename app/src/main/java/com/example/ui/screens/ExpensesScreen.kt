package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.data.model.FinanceLog
import com.example.ui.AppViewModel
import com.example.ui.components.BottomNavBar
import com.example.ui.navigation.Screen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.material.icons.filled.Menu
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(
    navController: NavController, 
    viewModel: AppViewModel,
    drawerState: DrawerState,
    coroutineScope: kotlinx.coroutines.CoroutineScope
) {
    val allLogs by viewModel.allFinanceLogs.collectAsStateWithLifecycle()
    val expenses by remember(allLogs) { derivedStateOf { allLogs.filter { it.type == "EXPENSE" } } }
    val monthTotal by remember(expenses) { derivedStateOf { expenses.sumOf { it.amount } } }

    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddExpenseDialog(
            onDismiss = { showAddDialog = false },
            onSave = { amount, title, category ->
                viewModel.logFinance(
                    FinanceLog(
                        type = "EXPENSE",
                        amount = amount,
                        title = title,
                        category = category
                    )
                )
                showAddDialog = false
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Finanzas y Cuentas", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                navigationIcon = {
                    IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.Menu, "Menu", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Default.CalendarToday, "Calendario", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            // Summary Stats
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(24.dp))
                        .border(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text("TOTAL DEL MES", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Q ${"%.2f".format(monthTotal)}", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.surfaceContainerLowest, RoundedCornerShape(24.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text("FACTURAS PEND.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("0", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }

            // Header Action
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Historial", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(24.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Registrar Gasto", style = MaterialTheme.typography.labelSmall)
                }
            }

            // Timeline
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(expenses) { expense ->
                    ExpenseTimelineItem(expense = expense)
                }
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .border(2.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp)), // Dashed normally
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Fin de registros del mes", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseTimelineItem(expense: FinanceLog) {
    val icon: ImageVector
    val iconBgColor: Color
    val iconTintColor: Color
    val categoryColor: Color

    when (expense.category) {
        "Proveedor" -> {
            icon = Icons.Default.LocalShipping
            iconBgColor = MaterialTheme.colorScheme.primaryContainer
            iconTintColor = MaterialTheme.colorScheme.onPrimaryContainer
            categoryColor = MaterialTheme.colorScheme.primary
        }
        "Servicios" -> {
            icon = Icons.Default.Bolt
            iconBgColor = MaterialTheme.colorScheme.secondaryContainer
            iconTintColor = MaterialTheme.colorScheme.onSecondaryContainer
            categoryColor = MaterialTheme.colorScheme.secondary
        }
        else -> {
            icon = Icons.Default.Inventory2
            iconBgColor = MaterialTheme.colorScheme.surfaceContainerHigh
            iconTintColor = MaterialTheme.colorScheme.onSurfaceVariant
            categoryColor = MaterialTheme.colorScheme.onSurfaceVariant
        }
    }

    val dateFormat = SimpleDateFormat("MMMM d", Locale.getDefault())

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(iconBgColor, CircleShape)
                .border(4.dp, MaterialTheme.colorScheme.background, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = iconTintColor, modifier = Modifier.size(20.dp))
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .background(MaterialTheme.colorScheme.surfaceContainerLowest, RoundedCornerShape(24.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                .clickable { /*TODO*/ }
                .padding(16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(expense.category.uppercase(), style = MaterialTheme.typography.labelSmall, color = categoryColor)
                Text(dateFormat.format(Date(expense.timestamp)), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(expense.title, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Q ${"%.2f".format(expense.amount)}", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.onSurface)
                Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun AddExpenseDialog(
    onDismiss: () -> Unit,
    onSave: (Double, String, String) -> Unit
) {
    var amountStr by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Suministros") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar Gasto") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Monto (Q)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
                // Using simple textfield for category to save time instead of complex dropdown
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Categoría (Proveedor, Servicios, Suministros)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amount = amountStr.toDoubleOrNull() ?: 0.0
                    if (amount > 0 && title.isNotBlank()) {
                        onSave(amount, title, category)
                    }
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
