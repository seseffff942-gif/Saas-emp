package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.ui.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(navController: NavController, viewModel: AppViewModel) {
    val allLogs by viewModel.allFinanceLogs.collectAsStateWithLifecycle()
    val allProducts by viewModel.allProducts.collectAsStateWithLifecycle()

    val totalIncome by remember(allLogs) {
        derivedStateOf { allLogs.filter { it.type == "INCOME" }.sumOf { it.amount } }
    }
    val totalExpenses by remember(allLogs) {
        derivedStateOf { allLogs.filter { it.type == "EXPENSE" }.sumOf { it.amount } }
    }
    val netProfit by remember(totalIncome, totalExpenses) {
        derivedStateOf { totalIncome - totalExpenses }
    }
    val inventoryValue by remember(allProducts) {
        derivedStateOf { allProducts.sumOf { it.price * it.stock } }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Reportes Financieros", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás", tint = MaterialTheme.colorScheme.primary)
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Profit Summary
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(24.dp)
            ) {
                Column {
                    Text("BENEFICIO NETO", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primaryContainer)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Q ${"%.2f".format(netProfit)}", style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("INGRESOS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primaryContainer)
                            Text("Q ${"%.2f".format(totalIncome)}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("GASTOS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primaryContainer)
                            Text("Q ${"%.2f".format(totalExpenses)}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Inventory Summary
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Resumen de Inventario", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatCard(
                        title = "VALOR ESTIMADO",
                        value = "Q ${"%.2f".format(inventoryValue)}",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "TOTAL ITEMS",
                        value = "${allProducts.sumOf { it.stock }}",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // simulated charts or more details can go here
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Gráficas detalladas disponibles en la versión Premium.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(20.dp)
    ) {
        Column {
            Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
