package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.data.model.FinanceLog
import com.example.data.model.Product
import com.example.ui.AppViewModel
import com.example.ui.components.BottomNavBar
import com.example.ui.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesScreen(navController: NavController, viewModel: AppViewModel) {
    val allProducts by viewModel.allProducts.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    val filteredProducts by remember(allProducts, searchQuery) {
        derivedStateOf {
            if (searchQuery.isBlank()) allProducts
            else allProducts.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
    }

    // Local cart state
    val cart = remember { mutableStateMapOf<Int, Int>() } // ProductId -> Quantity
    val totalAmount = cart.entries.sumOf { entry ->
        val product = allProducts.find { it.id == entry.key }
        (product?.price ?: 0.0) * entry.value
    }
    val totalItems = cart.values.sum()

    val scope = rememberCoroutineScope()
    var isSaving by remember { mutableStateOf(false) }
    var showQuickSaleDialog by remember { mutableStateOf(false) }
    var manualAmount by remember { mutableStateOf("") }
    var manualDescription by remember { mutableStateOf("") }

    if (showQuickSaleDialog) {
        AlertDialog(
            onDismissRequest = { showQuickSaleDialog = false },
            title = { Text("Venta Rápida Libre") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Registra una venta sin afectar el inventario.", style = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(
                        value = manualAmount,
                        onValueChange = { manualAmount = it },
                        label = { Text("Monto (Q)") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = manualDescription,
                        onValueChange = { manualDescription = it },
                        label = { Text("Descripción (Opcional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = manualAmount.toDoubleOrNull() ?: 0.0
                        if (amount > 0) {
                            scope.launch {
                                isSaving = true
                                viewModel.logFinance(
                                    FinanceLog(
                                        type = "INCOME",
                                        amount = amount,
                                        title = if (manualDescription.isNotBlank()) "Venta Manual: $manualDescription" else "Venta Manual",
                                        category = "Sales"
                                    )
                                )
                                showQuickSaleDialog = false
                                manualAmount = ""
                                manualDescription = ""
                                delay(500)
                                isSaving = false
                                navController.navigate(Screen.Dashboard.route) {
                                    popUpTo(Screen.Dashboard.route) { inclusive = true }
                                }
                            }
                        }
                    },
                    enabled = !isSaving && (manualAmount.toDoubleOrNull() ?: 0.0) > 0
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Registrar")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showQuickSaleDialog = false }, enabled = !isSaving) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Nueva Venta", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Default.CalendarToday, "Calendario", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
            )
        },
        bottomBar = {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                text = if (totalItems > 0) "Resumen de Carrito ($totalItems art.)" else "Sin artículos seleccionados",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text("Total: Q ${"%.2f".format(totalAmount)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Button(
                            onClick = {
                                if (totalItems > 0) {
                                    scope.launch {
                                        isSaving = true
                                        // Log Sale
                                        viewModel.logFinance(
                                            FinanceLog(
                                                type = "INCOME",
                                                amount = totalAmount,
                                                title = "Venta ($totalItems art.)",
                                                category = "Sales"
                                            )
                                        )
                                        // Update Stock
                                        cart.forEach { (productId, qty) ->
                                            val product = allProducts.find { it.id == productId }
                                            if (product != null) {
                                                viewModel.updateProduct(product.copy(stock = product.stock - qty))
                                            }
                                        }
                                        cart.clear()
                                        delay(500)
                                        isSaving = false
                                        navController.navigate(Screen.Dashboard.route) {
                                            popUpTo(Screen.Dashboard.route) { inclusive = true }
                                        }
                                    }
                                } else {
                                    // Venta rápida manual
                                    showQuickSaleDialog = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isSaving
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                            } else {
                                Icon(if (totalItems > 0) Icons.Default.CheckCircle else Icons.Default.Add, null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (totalItems > 0) "Confirmar Venta" else "Venta Rápida Libre")
                            }
                        }
                    }
                }
                BottomNavBar(navController = navController, currentRoute = Screen.Sales.route)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                placeholder = { Text("Buscar en inventario...", style = MaterialTheme.typography.bodyMedium) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.primary) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredProducts) { product ->
                    val qty = cart[product.id] ?: 0
                    SaleItemRow(
                        product = product,
                        quantity = qty,
                        onAdd = {
                            if (qty < product.stock) cart[product.id] = qty + 1
                        },
                        onRemove = {
                            if (qty > 0) {
                                if (qty == 1) cart.remove(product.id)
                                else cart[product.id] = qty - 1
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SaleItemRow(
    product: Product,
    quantity: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Inventory2, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                        if (product.stock <= 5) {
                            Text(
                                text = "BAJO STOCK",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Black),
                                color = MaterialTheme.colorScheme.onError,
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.error, RoundedCornerShape(16.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Q ${"%.2f".format(product.price)}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.5f), RoundedCornerShape(24.dp))
                    .padding(4.dp)
            ) {
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                ) {
                    Icon(Icons.Default.Remove, null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(16.dp))
                }
                Text(
                    text = quantity.toString(),
                    modifier = Modifier.width(32.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = if (quantity > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(
                    onClick = onAdd,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (quantity < product.stock) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(Icons.Default.Add, null, tint = if (quantity < product.stock) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
