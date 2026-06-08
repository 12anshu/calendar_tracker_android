package com.example.smartexpensecalendar

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.smartexpensecalendar.ui.HomeScreen
import com.example.smartexpensecalendar.core.designsystem.theme.SmartExpenseCalendarTheme
import dagger.hilt.android.AndroidEntryPoint

import androidx.navigation.compose.rememberNavController
import com.example.smartexpensecalendar.ui.navigation.NavGraph

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        // Permissions handled
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        val permissions = mutableListOf(
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS
        )
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            permissions.add("android.permission.POST_NOTIFICATIONS")
        }
        requestPermissionLauncher.launch(permissions.toTypedArray())

        setContent {
            SmartExpenseCalendarTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(navController = navController)
                }
            }
        }
    }
}
