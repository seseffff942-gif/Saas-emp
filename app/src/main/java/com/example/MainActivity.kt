package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.data.AppDatabase
import com.example.data.repository.AppRepository
import com.example.ui.AppViewModel
import com.example.ui.AppViewModelFactory
import com.example.ui.navigation.AppNavigation
import com.example.ui.theme.MicroManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val database = AppDatabase.getDatabase(this)
        val repository = AppRepository()
        
        setContent {
            MicroManagerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: AppViewModel = viewModel(
                        factory = AppViewModelFactory(repository)
                    )
                    val navController = rememberNavController()
                    AppNavigation(navController = navController, viewModel = viewModel)
                }
            }
        }
    }
}
