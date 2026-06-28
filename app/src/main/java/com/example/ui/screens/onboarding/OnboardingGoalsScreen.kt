package com.example.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ui.AppViewModel
import com.example.ui.navigation.Screen

@Composable
fun OnboardingGoalsScreen(navController: NavController, viewModel: AppViewModel) {
    var selectedGoals by remember { 
        mutableStateOf(setOf(
            "Organizar mis ventas y gastos", 
            "Controlar lo que me deben", 
            "Gestionar mejor mi inventario", 
            "Llevar un registro de clientes", 
            "Delegar tareas a mis empleados"
        )) 
    }

    val goals = listOf(
        "Organizar mis ventas y gastos",
        "Controlar lo que me deben",
        "Vender online sin depender de apps de terceros",
        "Gestionar mejor mi inventario",
        "Ver estadísticas avanzadas",
        "Llevar un registro de clientes",
        "Delegar tareas a mis empleados"
    )

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                LinearProgressIndicator(
                    progress = { 0.8f },
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
                text = "¿Qué te gustaría lograr con Treinta?",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Selecciona todas las opciones que desees. Te ayudaremos a usarlas.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            goals.forEach { goal ->
                val isSelected = selectedGoals.contains(goal)
                GoalItem(goal, isSelected) {
                    selectedGoals = if (isSelected) selectedGoals - goal else selectedGoals + goal
                }
                Divider(color = Color(0xFFEEEEEE))
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = { 
                    navController.navigate(Screen.OnboardingLoading.route)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Finalizar", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
fun GoalItem(goal: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = goal,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
            color = if (isSelected) Color.Black else Color.Gray
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color(0xFF4CAF50)
            )
        }
    }
}
