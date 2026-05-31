package com.example.smartexpensecalendar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smartexpensecalendar.presentation.budget.BudgetViewModel
import com.example.smartexpensecalendar.presentation.budget.CategoryBudgetState
import com.example.smartexpensecalendar.ui.components.CategoryIconView
import com.example.smartexpensecalendar.ui.theme.*
import com.example.smartexpensecalendar.utils.CurrencyUtils.formatIndianCurrency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpendingAnalysisScreen(
    navController: NavController,
    viewModel: BudgetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Spending Analysis", color = TextPrimary, fontWeight = FontWeight.Bold) },
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
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Text(
                    "Budget vs Actual",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold
                )
            }

            items(uiState.categoryBudgets.filter { it.budget > 0 || it.spent > 0 }) { state ->
                AnalysisCategoryCard(state, uiState.currencySymbol)
            }
        }
    }
}

@Composable
fun AnalysisCategoryCard(state: CategoryBudgetState, currencySymbol: String = "₹") {
    val categoryColor = getCategoryColor(state.category)
    val progress = if (state.budget > 0) (state.spent / state.budget).toFloat().coerceIn(0f, 1.2f) else 0f
    val isOverBudget = progress > 1.0f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceGlass)
            .border(1.dp, if (isOverBudget) ColorTransport.copy(alpha = 0.5f) else SurfaceGlassBright, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CategoryIconView(category = state.category, size = 32.dp, iconSize = 16.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(state.category, color = TextPrimary, fontWeight = FontWeight.Bold)
                }
                
                Text(
                    text = "${(progress * 100).toInt()}%",
                    color = if (isOverBudget) ColorTransport else CyanGlow,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(SurfaceGlassBright, CircleShape)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.coerceAtMost(1f))
                        .fillMaxHeight()
                        .background(if (isOverBudget) ColorTransport else categoryColor, CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "Spent: $currencySymbol${formatIndianCurrency(state.spent)}",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Text(
                    text = "Limit: $currencySymbol${formatIndianCurrency(state.budget)}",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }
    }
}
