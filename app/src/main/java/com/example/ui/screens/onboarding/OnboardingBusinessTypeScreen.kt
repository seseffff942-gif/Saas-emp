package com.example.ui.screens.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ui.AppViewModel
import com.example.ui.navigation.Screen

@Composable
fun OnboardingBusinessTypeScreen(navController: NavController, viewModel: AppViewModel) {
    var selectedTypes by remember { mutableStateOf(setOf("Productos")) }

    val options = listOf(
        BusinessTypeOption("Productos", "Para negocios que venden cualquier tipo de artículo físico.", Icons.Default.Inventory2),
        BusinessTypeOption("Servicios", "Para negocios que ofrecen soluciones, atención o asesorías personalizadas.", Icons.Default.CalendarMonth),
        BusinessTypeOption("Comida preparada y bebidas", "Ideal para negocios como restaurantes, panaderías, cafés, bares, entre otros.", Icons.Default.Restaurant),
        BusinessTypeOption("Ninguno", "No se adapta ninguna de las opciones.", Icons.Default.Block)
    )

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                LinearProgressIndicator(
                    progress = { 0.4f },
                    modifier = Modifier.fillMaxWidth().height(8.dp).padding(horizontal = 16.dp),
                    color = Color(0xFF4CAF50),
                    trackColor = Color(0xFFE0E0E0),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "¿Qué vende tu negocio?",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Puedes seleccionar una o varias opciones. Adaptaremos Treinta a tu negocio.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            options.forEach { option ->
                val isSelected = selectedTypes.contains(option.title)
                BusinessTypeCard(
                    option = option,
                    isSelected = isSelected,
                    onClick = {
                        selectedTypes = if (isSelected) {
                            selectedTypes - option.title
                        } else {
                            selectedTypes + option.title
                        }
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { 
                    navController.navigate(Screen.OnboardingCategory.route)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Continuar", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

data class BusinessTypeOption(val title: String, val subtitle: String, val icon: ImageVector)

@Composable
fun BusinessTypeCard(option: BusinessTypeOption, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) Color(0xFF4CAF50) else Color(0xFFE0E0E0)),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = null,
                tint = if (isSelected) Color(0xFF4CAF50) else Color.Gray,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = option.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50)
                )
            }
        }
    }
}
