package com.example.smartexpensecalendar.features.developer_tools.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.PriceCheck
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.smartexpensecalendar.ui.navigation.Screen
import com.example.smartexpensecalendar.core.designsystem.theme.BackgroundStart
import com.example.smartexpensecalendar.core.designsystem.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeveloperDashboardScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Developer Dashboard",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundStart)
            )
        },
        containerColor = BackgroundStart
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
        ) {
            item {
                AnalysisNavigationCard(
                    title = "Financial Detection Lab",
                    subtitle = "Debug and validate the SMS financial pipeline",
                    icon = Icons.Default.AccountBalanceWallet
                ) {
                    navController.navigate("financial_detection_lab")
                }
            }

            item {
                AnalysisNavigationCard(
                    title = "Transaction Extraction Lab",
                    subtitle = "Validate amount extraction from transaction SMS",
                    icon = Icons.Default.PriceCheck
                ) {
                    navController.navigate(Screen.TransactionExtractionLab.route)
                }
            }
        }
    }
}
