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
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import com.example.smartexpensecalendar.presentation.insights.InsightsViewModel
import com.example.smartexpensecalendar.presentation.insights.CategorySpend
import com.example.smartexpensecalendar.presentation.insights.MerchantSpend
import com.example.smartexpensecalendar.ui.components.CategoryIconView
import com.example.smartexpensecalendar.ui.theme.*
import com.example.smartexpensecalendar.utils.CurrencyUtils.formatIndianCurrency
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    navController: NavController,
    viewModel: InsightsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showMonthPicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "S.M.A.R.T Insights",
                            color = CyanGlow,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Box {
                            TextButton(
                                onClick = { showMonthPicker = true },
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.height(24.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${uiState.selectedMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${uiState.selectedMonth.year}",
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Icon(Icons.Default.KeyboardArrowDown, null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                                }
                            }
                            DropdownMenu(
                                expanded = showMonthPicker,
                                onDismissRequest = { showMonthPicker = false },
                                modifier = Modifier.background(BackgroundEnd)
                            ) {
                                val current = YearMonth.now()
                                (-12..12).forEach { offset ->
                                    val month = current.plusMonths(offset.toLong())
                                    DropdownMenuItem(
                                        text = { Text("${month.month.name} ${month.year}", color = TextPrimary) },
                                        onClick = { viewModel.setMonth(month); showMonthPicker = false }
                                    )
                                }
                            }
                        }
                    }
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
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Summary Card
            item {
                TotalSpendCard(uiState.totalSpent, uiState.currencySymbol)
            }

            // Category Breakdown Section
            item {
                SectionHeader("Category Breakdown")
            }
            
            items(uiState.categoryBreakdown) { item ->
                CategoryProgressItem(item, uiState.currencySymbol)
            }

            // Top Merchants Section
            item {
                SectionHeader("Top Merchants")
            }

            items(uiState.topMerchants) { merchant ->
                MerchantListItem(merchant, uiState.currencySymbol)
            }

            // Payment Mode Split
            item {
                SectionHeader("Payment Modes")
                PaymentModeCard(uiState.upiVsCard, uiState.currencySymbol)
            }
        }
    }
}

@Composable
fun TotalSpendCard(amount: Double, currencySymbol: String = "₹") {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    listOf(CyanGlow.copy(alpha = 0.15f), Color.Transparent)
                )
            )
            .border(1.dp, CyanGlow.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Total Spending", color = TextSecondary, style = MaterialTheme.typography.labelLarge)
            Text(
                "$currencySymbol${formatIndianCurrency(amount)}",
                color = TextPrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-1).sp
            )
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = TextPrimary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
fun CategoryProgressItem(item: CategorySpend, currencySymbol: String = "₹") {
    val color = getCategoryColor(item.category)
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CategoryIconView(category = item.category, size = 32.dp, iconSize = 16.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(item.category, color = TextPrimary, fontWeight = FontWeight.Medium)
            }
            Text(
                "$currencySymbol${formatIndianCurrency(item.amount)}",
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(SurfaceGlassBright)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(item.percentage)
                    .fillMaxHeight()
                    .background(color, CircleShape)
            )
        }
        Text(
            "${(item.percentage * 100).toInt()}% of total spend",
            color = TextSecondary,
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun MerchantListItem(merchant: MerchantSpend, currencySymbol: String = "₹") {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceGlass)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(merchant.merchant, color = TextPrimary, fontWeight = FontWeight.Bold)
            Text("${merchant.count} transactions", color = TextSecondary, fontSize = 12.sp)
        }
        Text(
            "$currencySymbol${formatIndianCurrency(merchant.amount)}",
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PaymentModeCard(split: Map<String, Double>, currencySymbol: String = "₹") {
    val upi = split["UPI"] ?: 0.0
    val card = split["Card"] ?: 0.0
    val total = upi + card
    val upiRatio = if (total > 0) (upi / total).toFloat() else 0f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceGlass)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("UPI / Digital", color = TextSecondary, fontSize = 12.sp)
                Text("$currencySymbol${formatIndianCurrency(upi)}", color = CyanGlow, fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Card / Others", color = TextSecondary, fontSize = 12.sp)
                Text("$currencySymbol${formatIndianCurrency(card)}", color = TextPrimary, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(CircleShape)
        ) {
            if (upiRatio > 0) {
                Box(modifier = Modifier.weight(upiRatio).fillMaxHeight().background(CyanGlow))
            }
            if (1f - upiRatio > 0) {
                Box(modifier = Modifier.weight(1f - upiRatio).fillMaxHeight().background(SurfaceGlassBright))
            }
        }
    }
}
