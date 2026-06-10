package com.example.smartexpensecalendar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.smartexpensecalendar.ui.navigation.Screen

@Composable
fun FintechBottomNav(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(
        modifier = modifier
            .navigationBarsPadding()
            .padding(horizontal = 4.dp, vertical = 8.dp)
            .fillMaxWidth()
            .height(82.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FintechNavItem(
                icon = Icons.Default.Home, 
                label = "Home", 
                isSelected = currentRoute == Screen.Home.route
            ) {
                if (currentRoute != Screen.Home.route) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            
            FintechNavItem(
                icon = Icons.AutoMirrored.Filled.ReceiptLong, 
                label = "Transactions", 
                isSelected = currentRoute == Screen.Transactions.route
            ) {
                if (currentRoute != Screen.Transactions.route) {
                    navController.navigate(Screen.Transactions.route)
                }
            }

            FintechNavItem(
                icon = Icons.Default.Sms, 
                label = "SMS", 
                isSelected = currentRoute == Screen.SmsInbox.route
            ) {
                if (currentRoute != Screen.SmsInbox.route) {
                    navController.navigate(Screen.SmsInbox.route)
                }
            }

            FintechNavItem(
                icon = Icons.Default.AutoGraph, 
                label = "Insights", 
                isSelected = currentRoute == Screen.Insights.route
            ) {
                if (currentRoute != Screen.Insights.route) {
                    navController.navigate(Screen.Insights.route)
                }
            }

            FintechNavItem(
                icon = Icons.Default.Person, 
                label = "Profile", 
                isSelected = currentRoute == Screen.Profile.route
            ) {
                if (currentRoute != Screen.Profile.route) {
                    navController.navigate(Screen.Profile.route)
                }
            }
        }
    }
}

@Composable
fun FintechNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(
                    if (isSelected)
                        Color(0xFF14B8A6).copy(alpha = 0.18f)
                    else
                        Color.Transparent
                )
                .padding(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected)
                    Color(0xFF2DD4BF)
                else
                    Color(0xFF94A3B8),
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            color = if (isSelected)
                Color(0xFF2DD4BF)
            else
                Color(0xFF94A3B8),
            fontSize = 11.sp,
            fontWeight = if (isSelected)
                FontWeight.Bold
            else
                FontWeight.Medium
        )
    }
}
