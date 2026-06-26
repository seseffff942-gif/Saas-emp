package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.BuildConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SubscriptionViewModel : ViewModel() {
    var isProcessing by mutableStateOf(false)
    var targetPlan by mutableStateOf("")
    var showSuccessDialog by mutableStateOf(false)
    var currentPlan by mutableStateOf("BÁSICO")

    // Lista de beneficios del Plan Básico (puedes modificarlos aquí)
    var basicFeatures by mutableStateOf(
        listOf(
            "Inventario hasta 50 productos",
            "Registro de ventas básico",
            "Soporte de la comunidad"
        )
    )
    
    // Lista de beneficios del Plan PRO SAAS (puedes modificarlos aquí)
    var proFeatures by mutableStateOf(
        listOf(
            "Inventario Ilimitado",
            "Reportes avanzados y gráficas",
            "Exportación de datos a Excel y PDF",
            "Soporte Prioritario 24/7 por chat",
            "Gestión de Múltiples Usuarios y Roles",
            "Sincronización en la nube en tiempo real",
            "Alertas de stock bajo e informes automáticos",
            "Integración con impresoras térmicas",
            "Soporte para lector de código de barras",
            "Sin anuncios ni marcas de agua"
        )
    )

    fun subscribeToPro() {
        // En una implementación real, aquí se inicializa el SDK de Stripe o RevenueCat
        // utilizando BuildConfig.STRIPE_PUBLISHABLE_KEY y BuildConfig.SUPABASE_URL
        val stripeKey = BuildConfig.STRIPE_PUBLISHABLE_KEY
        val supabaseUrl = BuildConfig.SUPABASE_URL
        
        targetPlan = "PRO SAAS"
        isProcessing = true
        viewModelScope.launch {
            // Simulamos la llamada a la pasarela de pagos y a la base de datos
            delay(2500)
            isProcessing = false
            currentPlan = "PRO SAAS"
            targetPlan = ""
            showSuccessDialog = true
        }
    }

    fun cancelSubscription() {
        targetPlan = "BÁSICO"
        isProcessing = true
        viewModelScope.launch {
            delay(1500)
            isProcessing = false
            currentPlan = "BÁSICO"
            targetPlan = ""
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(navController: NavController, viewModel: SubscriptionViewModel = viewModel()) {
    val coroutineScope = rememberCoroutineScope()

    if (viewModel.showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showSuccessDialog = false },
            title = { Text("¡Suscripción Exitosa!") },
            text = { Text("Tu cuenta ha sido actualizada al plan PRO SAAS correctamente. Las nuevas funciones ya están activas y registradas en Supabase.") },
            confirmButton = {
                TextButton(onClick = { viewModel.showSuccessDialog = false }) {
                    Text("Continuar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Planes y Suscripción", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
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
            Text(
                "Desbloquea el Potencial\nde tu Negocio",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Text(
                "Elige el plan que mejor se adapte a las necesidades de tu empresa.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // FREE PLAN
            PlanCard(
                title = "BÁSICO",
                price = "Gratis",
                description = "Para empezar tu negocio.",
                features = viewModel.basicFeatures,
                buttonText = if (viewModel.currentPlan == "BÁSICO") "Plan Actual" else "Cambiar a Básico",
                isCurrent = viewModel.currentPlan == "BÁSICO",
                isPro = false,
                isProcessing = viewModel.isProcessing && viewModel.targetPlan == "BÁSICO",
                onClick = { viewModel.cancelSubscription() }
            )

            // PRO PLAN
            PlanCard(
                title = "PRO SAAS",
                price = "Q 149 / mes",
                description = "Todo lo que necesitas para crecer.",
                features = viewModel.proFeatures,
                buttonText = if (viewModel.currentPlan == "PRO SAAS") "Plan Actual" else "Actualizar a PRO",
                isCurrent = viewModel.currentPlan == "PRO SAAS",
                isPro = true,
                isProcessing = viewModel.isProcessing && viewModel.targetPlan == "PRO SAAS",
                onClick = { viewModel.subscribeToPro() }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PlanCard(
    title: String,
    price: String,
    description: String,
    features: List<String>,
    buttonText: String,
    isCurrent: Boolean,
    isPro: Boolean,
    isProcessing: Boolean = false,
    onClick: () -> Unit = {}
) {
    val bgColor = if (isPro) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerLowest
    val contentColor = if (isPro) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    val labelColor = if (isPro) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primary
    val borderColor = if (isPro) Color.Transparent else MaterialTheme.colorScheme.outlineVariant

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .then(
                if (isPro) {
                    Modifier.background(
                        androidx.compose.ui.graphics.Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        )
                    )
                } else {
                    Modifier.background(bgColor)
                }
            )
            .border(if (isPro) 0.dp else 1.dp, borderColor, RoundedCornerShape(32.dp))
            .padding(32.dp)
    ) {
        Column {
            Text(title, style = MaterialTheme.typography.labelMedium, color = labelColor, letterSpacing = 1.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(price, style = MaterialTheme.typography.displaySmall, color = contentColor, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Text(description, style = MaterialTheme.typography.bodyMedium, color = if(isPro) contentColor.copy(alpha=0.8f) else MaterialTheme.colorScheme.onSurfaceVariant)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            features.forEach { feature ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 6.dp)) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = if (isPro) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(feature, style = MaterialTheme.typography.bodyMedium, color = contentColor)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isCurrent && !isProcessing,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPro) MaterialTheme.colorScheme.surfaceContainerLowest else MaterialTheme.colorScheme.surfaceContainerLow,
                    contentColor = if (isPro) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha=0.5f),
                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha=0.5f)
                )
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.primary, strokeWidth = 2.dp)
                } else {
                    Text(buttonText, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }
        }
    }
}
