package com.example.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ui.AppViewModel
import com.example.ui.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun PaywallScreen(navController: NavController, viewModel: AppViewModel) {
    var isProTab by remember { mutableStateOf(true) }
    var selectedPlanIndex by remember { mutableStateOf(0) } // 0: Mensual, 1: Trimestral, 2: Anual

    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Recuerda que puedes cancelar cuando quieras. Pago 100% seguro.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { 
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Continuar", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .background(Color(0xFFF9F9F9))
        ) {
            HeaderSection()
            
            FeatureBadges()

            UrgencyBanner()

            CountdownTimer()

            PlanSwitcher(isProTab) { isProTab = it }

            if (isProTab) {
                Text(
                    text = "INCLUIDO EN PRO ✨ Inteligencia Artificial en tu negocio. Herramientas inteligentes que potencian tu empresa",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF673AB7),
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    textAlign = TextAlign.Center
                )
            }

            PricingCards(isProTab, selectedPlanIndex) { selectedPlanIndex = it }

            ComparisonTable()

            FooterLinks()
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun HeaderSection() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "¡Listo! Estas son las funciones para ti",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun FeatureBadges() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        FeatureBadge("Ventas y gastos", Icons.Default.BarChart)
        FeatureBadge("Inventario", Icons.Default.Inventory2)
        FeatureBadge("Deudas", Icons.Default.AccountBox)
    }
}

@Composable
fun FeatureBadge(text: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8F5E9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF4CAF50))
        }
        Text(text = text, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
fun UrgencyBanner() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Para usarlas sin límites y desde hoy...", style = MaterialTheme.typography.bodyMedium)
        Text(
            text = "¡Te activamos 80% dto!",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFFFF9800)
        )
    }
}

@Composable
fun CountdownTimer() {
    var timeLeft by remember { mutableStateOf(259132) } // Approx 3 days in seconds

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
    }

    val days = timeLeft / 86400
    val hours = (timeLeft % 86400) / 3600
    val minutes = (timeLeft % 3600) / 60
    val seconds = timeLeft % 60

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = "Termina en: ", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
        TimerBox("${days}d")
        TimerBox("${hours}h")
        TimerBox("${minutes}m")
        TimerBox("${seconds}s")
    }
}

@Composable
fun TimerBox(text: String) {
    Surface(
        modifier = Modifier.padding(horizontal = 2.dp),
        shape = RoundedCornerShape(4.dp),
        color = Color(0xFFEEEEEE)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun PlanSwitcher(isPro: Boolean, onTabChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFEEEEEE))
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(20.dp))
                .background(if (!isPro) Color.White else Color.Transparent)
                .clickable { onTabChange(false) },
            contentAlignment = Alignment.Center
        ) {
            Text("Plan Básico", fontWeight = FontWeight.Medium, color = if (!isPro) Color.Black else Color.Gray)
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(20.dp))
                .background(if (isPro) Color.White else Color.Transparent)
                .clickable { onTabChange(true) },
            contentAlignment = Alignment.Center
        ) {
            Text("Pro con IA", fontWeight = FontWeight.Medium, color = if (isPro) Color.Black else Color.Gray)
        }
    }
}

@Composable
fun PricingCards(isPro: Boolean, selectedIndex: Int, onSelect: (Int) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        val monthlyPrice = if (isPro) "USD 4" else "USD 2"
        val monthlyStrike = if (isPro) "USD 19.99" else "USD 9.99"
        
        PricingCard(
            title = "Mensual",
            price = monthlyPrice,
            strikePrice = monthlyStrike,
            badge = "⭐ Recomendado",
            isSelected = selectedIndex == 0,
            onClick = { onSelect(0) }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        val quarterlyPrice = if (isPro) "USD 10.79" else "USD 5.39"
        val quarterlyStrike = if (isPro) "USD 53.97" else "USD 26.97"
        val quarterlyMonthly = if (isPro) "USD 3.60 / mes" else "USD 1.80 / mes"

        PricingCard(
            title = "Trimestral",
            price = quarterlyPrice,
            strikePrice = quarterlyStrike,
            subPrice = quarterlyMonthly,
            isSelected = selectedIndex == 1,
            onClick = { onSelect(1) }
        )

        Spacer(modifier = Modifier.height(12.dp))
        
        val annualPrice = if (isPro) "USD 179.88" else "USD 89.88"
        val annualMonthly = if (isPro) "USD 14.99 / mes" else "USD 7.49 / mes"

        PricingCard(
            title = "Anual",
            price = annualPrice,
            subPrice = annualMonthly,
            isSelected = selectedIndex == 2,
            onClick = { onSelect(2) }
        )
    }
}

@Composable
fun PricingCard(
    title: String, 
    price: String, 
    strikePrice: String? = null, 
    subPrice: String? = null, 
    badge: String? = null,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) Color(0xFF4CAF50) else Color(0xFFE0E0E0)),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (badge != null) {
                Surface(
                    color = Color(0xFFFFE0B2),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(text = badge, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), color = Color(0xFFE65100))
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = isSelected, onClick = onClick, colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF4CAF50)))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    if (subPrice != null) {
                        Text(text = subPrice, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = price, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                    if (strikePrice != null) {
                        Text(text = strikePrice, style = MaterialTheme.typography.bodySmall, color = Color.Gray, textDecoration = TextDecoration.LineThrough)
                    }
                }
            }
        }
    }
}

@Composable
fun ComparisonTable() {
    Column(modifier = Modifier.padding(24.dp)) {
        Text(text = "Conoce las diferencias entre planes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "BÁSICO", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.width(60.dp), textAlign = TextAlign.Center)
            Text(text = "PRO", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.width(60.dp), textAlign = TextAlign.Center, color = Color(0xFF4CAF50))
        }

        ComparisonRow("Crea tu sitio web con IA ✨", false, true)
        ComparisonRow("Mejora de fotos con IA ✨", false, true)
        ComparisonRow("Lectura de facturas con IA ✨", false, true)
        ComparisonRow("Uso desde el computador", false, true)
        ComparisonRow("Registro de ventas y gastos", true, true)
        ComparisonRow("Gestión de inventario", true, true)
        ComparisonRow("Control de deudas", true, true)
    }
}

@Composable
fun ComparisonRow(feature: String, hasBasic: Boolean, hasPro: Boolean) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text = feature, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
        Icon(
            imageVector = if (hasBasic) Icons.Default.Check else Icons.Default.Close,
            contentDescription = null,
            tint = if (hasBasic) Color(0xFF4CAF50) else Color.Red,
            modifier = Modifier.width(60.dp).size(20.dp)
        )
        Icon(
            imageVector = if (hasPro) Icons.Default.Check else Icons.Default.Close,
            contentDescription = null,
            tint = if (hasPro) Color(0xFF4CAF50) else Color.Red,
            modifier = Modifier.width(60.dp).size(20.dp)
        )
    }
    Divider(color = Color(0xFFEEEEEE))
}

@Composable
fun FooterLinks() {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.fillMaxWidth().clickable { },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.Gray)
            Spacer(modifier = Modifier.width(8.dp))
            Text("¿Tienes plan o eres empleado?", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth().clickable { },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Headphones, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.Gray)
            Spacer(modifier = Modifier.width(8.dp))
            Text("¿Necesitas ayuda?", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { }) {
            Text("Cambiar de cuenta", color = Color(0xFF4CAF50))
        }
    }
}
