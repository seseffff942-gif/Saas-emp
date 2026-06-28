package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.example.data.model.Product
import com.example.ui.AppViewModel
import com.example.ui.components.BottomNavBar
import com.example.ui.navigation.Screen

import androidx.compose.material.icons.filled.Menu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    navController: NavController, 
    viewModel: AppViewModel,
    drawerState: DrawerState,
    coroutineScope: kotlinx.coroutines.CoroutineScope
) {
    val allProducts by viewModel.allProducts.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }

    val filteredProducts by remember(allProducts, searchQuery) {
        derivedStateOf {
            if (searchQuery.isBlank()) allProducts
            else allProducts.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Bodega") },
                navigationIcon = {
                    IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddProduct.route) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(Icons.Default.Add, "Agregar Producto")
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "CATÁLOGO",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 2.sp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Inventario",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Calendario", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                }

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    placeholder = { Text("Buscar productos...", style = MaterialTheme.typography.bodyMedium) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar", tint = MaterialTheme.colorScheme.primary) },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                // Grid
                LazyVerticalGrid(
                    modifier = Modifier.weight(1f),
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredProducts) { product ->
                        ProductCard(product = product)
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(product: Product) {
    val isLowStock = product.stock <= 5
    Surface(
        onClick = { /* Interactivo */ },
        modifier = Modifier,
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(if (isLowStock) 2.dp else 1.dp, if (isLowStock) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.3f)),
            contentAlignment = Alignment.Center
        ) {
            // Placeholder for image icon
            Icon(Icons.Default.Inventory2, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.outline.copy(alpha=0.5f))

            val badgeColor = if (isLowStock) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary
            val badgeTextColor = if (isLowStock) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onTertiary
            val badgeText = if (isLowStock) "BAJO STOCK" else "EN STOCK"

            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopEnd)
                    .clip(RoundedCornerShape(8.dp))
                    .background(badgeColor)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(badgeText, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Black), color = badgeTextColor)
            }
        }
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Q ${"%.2f".format(product.price)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(
                    if (isLowStock) Icons.Default.Warning else Icons.Default.Inventory2,
                    contentDescription = null,
                    tint = if (isLowStock) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "${product.stock} unidades",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isLowStock) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
    }
}
