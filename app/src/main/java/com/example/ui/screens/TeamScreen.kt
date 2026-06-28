package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamScreen(navController: NavController) {
    var teamMembers by remember { mutableStateOf(listOf(
        Pair("Juan Pérez", "Administrador"),
        Pair("Pedro Gómez", "Cajero"),
        Pair("Ana López", "Gerente de Tienda")
    )) }
    
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var currentMemberIndex by remember { mutableStateOf(-1) }
    var memberName by remember { mutableStateOf("") }
    var memberRole by remember { mutableStateOf("") }

    if (showAddDialog || showEditDialog) {
        AlertDialog(
            onDismissRequest = { 
                showAddDialog = false
                showEditDialog = false
            },
            title = { Text(if (showAddDialog) "Añadir Empleado" else "Editar Empleado") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = memberName,
                        onValueChange = { memberName = it },
                        label = { Text("Nombre") }
                    )
                    OutlinedTextField(
                        value = memberRole,
                        onValueChange = { memberRole = it },
                        label = { Text("Rol") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (memberName.isNotBlank() && memberRole.isNotBlank()) {
                        if (showAddDialog) {
                            teamMembers = teamMembers + Pair(memberName, memberRole)
                        } else if (showEditDialog && currentMemberIndex >= 0) {
                            val newList = teamMembers.toMutableList()
                            newList[currentMemberIndex] = Pair(memberName, memberRole)
                            teamMembers = newList
                        }
                    }
                    showAddDialog = false
                    showEditDialog = false
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showAddDialog = false
                    showEditDialog = false
                }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Equipo y Empleados", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    memberName = ""
                    memberRole = ""
                    showAddDialog = true 
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir empleado")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .padding(16.dp)
            ) {
                Column {
                    Text("Gestión de Accesos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiaryContainer)
                    Text("Invita a tu equipo y asigna roles específicos para controlar el acceso a la información.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha=0.8f))
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(teamMembers.size) { index ->
                    val member = teamMembers[index]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(member.first, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text(member.second, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        TextButton(onClick = { 
                            memberName = member.first
                            memberRole = member.second
                            currentMemberIndex = index
                            showEditDialog = true
                        }) {
                            Text("Editar")
                        }
                    }
                }
            }
        }
    }
}
