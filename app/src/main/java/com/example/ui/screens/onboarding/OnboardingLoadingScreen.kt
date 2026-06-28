package com.example.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Storefront
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
import kotlinx.coroutines.delay

@Composable
fun OnboardingLoadingScreen(navController: NavController, viewModel: AppViewModel) {
    var textState by remember { mutableStateOf(1) }

    LaunchedEffect(Unit) {
        delay(2000)
        textState = 2
        delay(2500)
        navController.navigate(Screen.Paywall.route) {
            popUpTo(Screen.OnboardingUserInfo.route) { inclusive = true }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LinearProgressIndicator(
            progress = { 1f },
            modifier = Modifier.fillMaxWidth().height(8.dp).padding(horizontal = 16.dp).align(Alignment.CenterHorizontally).absoluteOffset(y = (-200).dp),
            color = Color(0xFF4CAF50),
            trackColor = Color(0xFFE0E0E0),
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Central Asset Placeholder (Icon since generation failed)
        Icon(
            imageVector = Icons.Default.Storefront,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = Color(0xFF4CAF50)
        )

        Spacer(modifier = Modifier.height(32.dp))

        AnimatedContent(
            targetState = textState,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            }
        ) { state ->
            if (state == 1) {
                Text(
                    text = "Analizando tu negocio",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Gray
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Estamos armando ",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Gray
                    )
                    Text(
                        text = "Treinta ",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                    Text(
                        text = "para ti",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Gray
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        CircularProgressIndicator(color = Color(0xFF4CAF50))
    }
}
