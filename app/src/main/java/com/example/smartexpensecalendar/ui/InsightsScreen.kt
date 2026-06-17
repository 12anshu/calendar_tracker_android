package com.example.smartexpensecalendar.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smartexpensecalendar.presentation.insights.*
import com.example.smartexpensecalendar.ui.components.CategoryIconView
import com.example.smartexpensecalendar.ui.components.FintechBottomNav
import com.example.smartexpensecalendar.core.designsystem.theme.*
import com.example.smartexpensecalendar.utils.CurrencyUtils.formatIndianCurrency
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    navController: NavController,
    viewModel: InsightsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("OVERVIEW", "SPENDING", "ANALYTICS")

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(BackgroundStart)) {
                TopAppBar(
                    title = {
                        Text(
                            text = "${uiState.selectedMonth.month.name} ${uiState.selectedMonth.year}",
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
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = BackgroundStart,
                    contentColor = CyanGlow,
                    divider = { HorizontalDivider(color = SurfaceGlassBright) },
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = CyanGlow
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                    }
                }
            }
        },
        bottomBar = {
            FintechBottomNav(navController = navController)
        },
        containerColor = BackgroundStart
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(horizontal = 16.dp)) {
            when (selectedTabIndex) {
                0 -> OverviewTab(uiState)
                1 -> SpendingTab(uiState, onSeeAllCategories = { navController.navigate(com.example.smartexpensecalendar.ui.navigation.Screen.SpendingAnalysis.route) })
                2 -> AnalyticsTab(uiState)
            }
        }
    }
}

@Composable
fun OverviewTab(uiState: InsightsUiState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SnapshotMiniCard(
                    title = "Total Spent",
                    amount = uiState.totalSpent,
                    symbol = uiState.currencySymbol,
                    comparison = uiState.spentComparison,
                    icon = Icons.Default.ShoppingCart,
                    iconTint = ColorFood,
                    modifier = Modifier.weight(1f)
                )
                SnapshotMiniCard(
                    title = "Total Budget",
                    amount = uiState.totalBudget,
                    symbol = uiState.currencySymbol,
                    comparison = uiState.budgetComparison,
                    icon = Icons.Default.AccountBalanceWallet,
                    iconTint = PremiumGold,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        item {
            SnapshotMiniCard(
                title = "Remaining Budget",
                amount = uiState.remainingBudget,
                symbol = uiState.currencySymbol,
                comparison = uiState.remainingComparison,
                icon = Icons.Default.Savings,
                iconTint = CyanGlow,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        item {
            SectionHeader("Smart Highlights")
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                uiState.smartInsights.forEach { insight ->
                    InsightCard(insight)
                }
            }
        }

        item {
            SectionHeader("Daily Breakdown")
            CashFlowMiniCard(uiState.totalIncome, uiState.totalSpent, uiState.currencySymbol)
        }
    }
}

@Composable
fun SpendingTab(uiState: InsightsUiState, onSeeAllCategories: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
    ) {
        item {
            SectionHeader("Spending Trend")
            SpendingTrendCard(uiState)
        }

        item {
            SectionHeader("Category Breakdown")
//            CategoryDonutSection(uiState, onSeeAllCategories)
            CategoryBreakdownCard(uiState, onSeeAllCategories)
        }
    }
}

@Composable
fun AnalyticsTab(uiState: InsightsUiState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
    ) {
        item {
            PaymentChannelsSection(uiState)
        }

        if (uiState.mealCardTotal > 0) {
            item {
                MealCardInsightsSection(uiState)
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceGlass)
                    .padding(16.dp)
            ) {
                Column {
                    Text("TOP MERCHANTS", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    if (uiState.topMerchants.isEmpty()) {
                        Text("No merchant data found", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(vertical = 16.dp).align(Alignment.CenterHorizontally))
                    } else {
                        uiState.topMerchants.forEachIndexed { index, merchant ->
                            MerchantListItem(merchant, index, uiState.currencySymbol)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SnapshotMiniCard(
    title: String,
    amount: Double,
    symbol: String,
    comparison: Comparison?,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color = CyanGlow,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceGlass)
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = iconTint, modifier = Modifier.size(30.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(title, color = TextSecondary, fontSize = 11.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "$symbol${formatIndianCurrency(amount)}",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
            if (comparison != null) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(
                        if (comparison.isIncrease) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        null,
                        tint = if (comparison.isIncrease) ColorFood else ColorGroceries,
                        modifier = Modifier.size(10.dp)
                    )
                    Text(
                        "${comparison.percentChange}% vs last month",
                        color = if (comparison.isIncrease) ColorFood else ColorGroceries,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SpendingTrendCard(uiState: InsightsUiState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceGlass)
            .padding(16.dp)
    ) {
        Column {
            SpendingTrendChartInteractive(uiState.dailyTrend, uiState.currencySymbol)
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = SurfaceGlassBright)
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Highest Spending Day", color = TextSecondary, fontSize = 11.sp)
                    Text(
                        "${uiState.highestSpendingDay?.first?.dayOfMonth ?: "N/A"} ${uiState.highestSpendingDay?.first?.month?.name?.take(3) ?: ""} (${uiState.currencySymbol}${formatIndianCurrency(uiState.highestSpendingDay?.second ?: 0.0)})",
                        color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Average Daily Spend", color = TextSecondary, fontSize = 11.sp)
                    Text("${uiState.currencySymbol}${formatIndianCurrency(uiState.averageDailySpend)}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun CategoryDonutSection(uiState: InsightsUiState, onSeeAllClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceGlass)
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Donut Chart
                Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
                    CategoryDonutChart(uiState.categoryBreakdown, uiState.totalSpent)
//                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                        Text("${uiState.currencySymbol}${formatIndianCurrency(uiState.totalSpent)}", color = TextPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
//                        Text("TOTAL", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
//                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Legend
                Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                    uiState.categoryBreakdown.take(5).forEach { cat ->
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(12.dp).clip(CircleShape).background(getCategoryColor(cat.category), CircleShape))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(cat.category.uppercase(), color = TextPrimary, modifier = Modifier.weight(1f))
                            Text("${uiState.currencySymbol}${formatIndianCurrency(cat.amount)}", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("${(cat.percentage * 100).toInt()}%)", color = TextSecondary, fontSize = 9.sp)
//                            Column {
//                                Text(cat.category.uppercase(), color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
//                                Text("${uiState.currencySymbol}${formatIndianCurrency(cat.amount)} (${(cat.percentage * 100).toInt()}%)", color = TextSecondary, fontSize = 9.sp)
//                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                onClick = onSeeAllClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("VIEW ALL CATEGORIES", color = CyanGlow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Icon(Icons.Default.ChevronRight, null, tint = CyanGlow, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun CategoryBreakdownCard(
    uiState: InsightsUiState, onSeeAllClick: () -> Unit
) {
    val total = uiState.breakdownTotal

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceGlass
        ),
        border = BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.05f)
        )
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 20.dp,
                vertical = 20.dp
            )
        )  {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.weight(0.42f),
                    contentAlignment = Alignment.Center
                ) {
                    CategoryDonutChart(
                        categories = uiState.categoryBreakdown,
                        totalAmount = total
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                Box(
                    modifier = Modifier.weight(0.58f),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    CategoryLegend(
                        categories = uiState.categoryBreakdown.take(5),
                        onSeeAllClick = onSeeAllClick
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryLegend(
    categories: List<CategorySpend>, onSeeAllClick: () -> Unit
) {

    Column {
        categories.forEach { category ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            getCategoryColor(category.category),
                            CircleShape
                        )
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = category.category,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                    fontSize = 12.sp
                )
                Text(
                    text = "₹${category.amount.toInt()}",
                    modifier = Modifier.width(60.dp),
                    textAlign = TextAlign.End,
                    color = TextPrimary,
                    fontSize = 12.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(
            onClick = onSeeAllClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("VIEW ALL CATEGORIES", color = CyanGlow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Icon(Icons.Default.ChevronRight, null, tint = CyanGlow, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun DonutChart(data: List<CategorySpend>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.padding(12.dp)) {
        var startAngle = -90f
        data.forEach { cat ->
            val sweep = cat.percentage * 360f
            drawArc(
                color = getCategoryColor(cat.category),
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = false,
                style = Stroke(width = 42f.dp.toPx(), cap = StrokeCap.Round),
                size = Size(size.width, size.height)
            )
            startAngle += sweep
        }
    }
}

@Composable
fun CategoryDonutChart(
    categories: List<CategorySpend>,
    totalAmount: Double
) {
    val textMeasurer = rememberTextMeasurer()
    val displayTotal = if (totalAmount > 0) totalAmount else categories.sumOf { it.amount.toDouble() }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(150.dp)
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize().padding(12.dp)
        ) {
            val strokeWidth = 24.dp.toPx()
            val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
            val topLeftOffset = androidx.compose.ui.geometry.Offset(strokeWidth / 2, strokeWidth / 2)

            if (displayTotal <= 0 || categories.isEmpty()) {
                // Draw a grey placeholder donut
                drawArc(
                    color = SurfaceGlassBright,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt),
                    size = arcSize,
                    topLeft = topLeftOffset
                )
                return@Canvas
            }

            var startAngle = -90f

            categories.forEach { category ->
                val percentage = if (displayTotal > 0) category.amount / displayTotal else 0.0
                val sweepAngle = (percentage * 360f).toFloat()
                if (sweepAngle < 0.1f) return@forEach
                
                val color = getCategoryColor(category = category.category)
                
                // Draw Segment
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Butt
                    ),
                    size = arcSize,
                    topLeft = topLeftOffset
                )

                // Draw percentage on donut if segment is large enough
                if (sweepAngle > 30f) {
                    val midAngle = startAngle + sweepAngle / 2f
                    val angleRad = Math.toRadians(midAngle.toDouble())
                    
                    val radius = (size.minDimension - strokeWidth) / 2f
                    val x = center.x + radius * cos(angleRad).toFloat()
                    val y = center.y + radius * sin(angleRad).toFloat()

                    val percentage = (category.amount / displayTotal * 100).toInt()
                    val percentageText = "$percentage%"
                    val textLayoutResult = textMeasurer.measure(
                        text = percentageText,
                        style = androidx.compose.ui.text.TextStyle(
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    
                    drawText(
                        textLayoutResult = textLayoutResult,
                        topLeft = androidx.compose.ui.geometry.Offset(
                            x - textLayoutResult.size.width / 2f,
                            y - textLayoutResult.size.height / 2f
                        )
                    )
                }
                
                startAngle += sweepAngle
            }
        }
    }
}

@Composable
fun PaymentModeSplitChart(split: Map<String, Double>) {
    val upi = split["UPI"] ?: 0.0
    val card = split["Card"] ?: 0.0
    val total = upi + card
    val upiRatio = if (total > 0) (upi / total).toFloat() else 0f
    
    Box(contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(80.dp)) {
            drawArc(
                color = SurfaceGlassBright,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 8.dp.toPx())
            )
            drawArc(
                color = CyanGlow,
                startAngle = -90f,
                sweepAngle = upiRatio * 360f,
                useCenter = false,
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        Text("${(upiRatio * 100).toInt()}%", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SmallInsightRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = PremiumGold, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(label, color = TextSecondary, fontSize = 9.sp)
            Text(value, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun BudgetStatusCard(budget: Double, spent: Double, remaining: Double, symbol: String) {
    val progress = if (budget > 0) (spent / budget).toFloat().coerceIn(0f, 1f) else 0f
    val isOverBudget = spent > budget && budget > 0

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.verticalGradient(listOf(CyanGlow.copy(alpha = 0.15f), SurfaceGlass)))
            .border(1.dp, CyanGlow.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Column {
            Text(if (isOverBudget) "Budget Exceeded" else "Remaining Budget", color = TextSecondary, fontSize = 12.sp)
            Text(
                "$symbol${formatIndianCurrency(if (isOverBudget) spent - budget else remaining)}",
                color = if (isOverBudget) ColorFood else CyanGlow,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = if (isOverBudget) ColorFood else CyanGlow,
                trackColor = SurfaceGlassBright
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Spent: $symbol${formatIndianCurrency(spent)}", color = TextSecondary, fontSize = 11.sp)
                Text("Budget: $symbol${formatIndianCurrency(budget)}", color = TextSecondary, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun SpendingTrendChartInteractive(data: List<DailySpend>, symbol: String) {
    var selectedPoint by remember { mutableStateOf<DailySpend?>(null) }
    val maxAmount = (data.maxOfOrNull { it.amount } ?: 0.0).coerceAtLeast(1000.0)
    val textMeasurer = rememberTextMeasurer()

    // Nice round number for the top of the graph (multiples of 5k or 1k)
    val gridMax = if (maxAmount < 5000) {
        (((maxAmount / 1000).toInt() + 1) * 1000).toDouble()
    } else {
        (((maxAmount / 5000).toInt() + 1) * 5000).toDouble()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(data) {
                    detectTapGestures { offset ->
                        val labelWidth = 35.dp.toPx()
                        val chartWidth = size.width - labelWidth
                        val x = offset.x - labelWidth
                        if (x >= 0 && chartWidth > 0) {
                            val step = if (data.size > 1) chartWidth / (data.size - 1) else 0f
                            val index = if (step > 0) (x / step + 0.5f).toInt().coerceIn(0, data.size - 1) else 0
                            selectedPoint = data[index]
                        }
                    }
                }
        ) {
                val labelWidth = 35.dp.toPx()
                val labelHeight = 24.dp.toPx()
                val width = size.width - labelWidth
                val height = size.height - labelHeight

                val stepX = if (data.size > 1) width / (data.size - 1) else 0f

                val points = data.mapIndexed { index, daily ->
                    val x = labelWidth + index * stepX
                    val y = height - (daily.amount.toFloat() / gridMax.toFloat() * height).coerceIn(0f, height)
                    androidx.compose.ui.geometry.Offset(x, y)
                }

                // 1. Grid & Y Labels
                val divisions = 4
                for (i in 0..divisions) {
                    val progress = i / divisions.toFloat()
                    val y = height - (progress * height)

                    // Grid Line
                    drawLine(
                        color = TextSecondary.copy(alpha = 0.1f),
                        start = androidx.compose.ui.geometry.Offset(labelWidth, y),
                        end = androidx.compose.ui.geometry.Offset(size.width, y),
                        strokeWidth = 1.dp.toPx()
                    )

                    // Label
                    val amountVal = (gridMax * progress).toInt()
                    val label = when {
                        amountVal >= 1000 -> "$symbol${amountVal / 1000}k"
                        else -> "$symbol$amountVal"
                    }
                    drawText(
                        textMeasurer = textMeasurer,
                        text = label,
                        topLeft = androidx.compose.ui.geometry.Offset(0f, y - 7.dp.toPx()),
                        style = androidx.compose.ui.text.TextStyle(color = TextSecondary, fontSize = 9.sp)
                    )
                }

                // 2. X Labels (Dates)
                if (data.size >= 2) {
                    val labelIndices = if (data.size <= 5) {
                        data.indices.toList()
                    } else {
                        listOf(0, data.size / 4, data.size / 2, 3 * data.size / 4, data.size - 1).distinct()
                    }
                    
                    labelIndices.forEach { idx ->
                        val p = points[idx]
                        val date = data[idx].date
                        val label = "${date.dayOfMonth} ${date.month.name.take(3)}"
                        drawText(
                            textMeasurer = textMeasurer,
                            text = label,
                            topLeft = androidx.compose.ui.geometry.Offset(p.x - 15.dp.toPx(), height + 8.dp.toPx()),
                            style = androidx.compose.ui.text.TextStyle(color = TextSecondary, fontSize = 9.sp)
                        )
                    }
                }

                // 3. Shaded Fill
                if (points.isNotEmpty()) {
                    val fillPath = androidx.compose.ui.graphics.Path().apply {
                        moveTo(points.first().x, height)
                        points.forEach { lineTo(it.x, it.y) }
                        lineTo(points.last().x, height)
                        close()
                    }
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(CyanGlow.copy(alpha = 0.25f), Color.Transparent),
                            startY = 0f,
                            endY = height
                        )
                    )
                }

                // 4. Smooth Line
                val linePath = androidx.compose.ui.graphics.Path().apply {
                    if (points.isNotEmpty()) {
                        moveTo(points.first().x, points.first().y)
                        points.drop(1).forEach { lineTo(it.x, it.y) }
                    }
                }
                drawPath(
                    path = linePath,
                    color = CyanGlow,
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                )

                // 5. Interaction & Selected Point
                selectedPoint?.let { sel ->
                    val idx = data.indexOf(sel)
                    if (idx != -1) {
                        val p = points[idx]
                        
                        // Vertical indicator line
                        drawLine(
                            color = CyanGlow.copy(alpha = 0.3f),
                            start = androidx.compose.ui.geometry.Offset(p.x, 0f),
                            end = androidx.compose.ui.geometry.Offset(p.x, height),
                            strokeWidth = 1.dp.toPx()
                        )
                        
                        drawCircle(Color.White, radius = 6.dp.toPx(), center = p)
                        drawCircle(CyanGlow, radius = 4.dp.toPx(), center = p)

                        // Value Bubble
                        val tooltip = "$symbol${formatIndianCurrency(sel.amount)}"
                        val textLayoutResult = textMeasurer.measure(
                            text = tooltip,
                            style = androidx.compose.ui.text.TextStyle(
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        
                        val bubbleWidth = textLayoutResult.size.width.toFloat() + 16.dp.toPx()
                        val bubbleHeight = textLayoutResult.size.height.toFloat() + 8.dp.toPx()
                        
                        drawRoundRect(
                            color = CyanGlow,
                            topLeft = androidx.compose.ui.geometry.Offset(
                                (p.x - bubbleWidth / 2).coerceIn(0f, size.width - bubbleWidth),
                                p.y - bubbleHeight - 10.dp.toPx()
                            ),
                            size = Size(bubbleWidth, bubbleHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx())
                        )
                        
                        drawText(
                            textMeasurer = textMeasurer,
                            text = tooltip,
                            topLeft = androidx.compose.ui.geometry.Offset(
                                (p.x - textLayoutResult.size.width / 2).coerceIn(8.dp.toPx(), size.width - textLayoutResult.size.width - 8.dp.toPx()),
                                p.y - bubbleHeight - 6.dp.toPx()
                            ),
                            style = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }

@Composable
fun CashFlowMiniCard(income: Double, expense: Double, symbol: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceGlass)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Total Income", color = TextSecondary, fontSize = 11.sp)
            Text("$symbol${formatIndianCurrency(income)}", color = ColorGroceries, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Box(modifier = Modifier.width(1.dp).height(32.dp).background(SurfaceGlassBright).align(Alignment.CenterVertically))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Total Expense", color = TextSecondary, fontSize = 11.sp)
            Text("$symbol${formatIndianCurrency(expense)}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = TextPrimary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
fun CategoryProgressItem(item: CategorySpend, currencySymbol: String = "₹") {
    val color = getCategoryColor(item.category)
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CategoryIconView(category = item.category, size = 32.dp, iconSize = 16.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(item.category, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
            Text("$currencySymbol${formatIndianCurrency(item.amount)}", color = TextPrimary, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { item.percentage },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
            color = color,
            trackColor = SurfaceGlassBright
        )
    }
}

@Composable
fun VesselProgressItem(vessel: VesselSpend, symbol: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(vessel.accountName.uppercase(), color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text("$symbol${formatIndianCurrency(vessel.amount)}", color = TextPrimary, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { vessel.percentage },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
            color = SecondaryAccent,
            trackColor = SurfaceGlassBright,
        )
    }
}

@Composable
fun MerchantListItem(merchant: MerchantSpend, index: Int, currencySymbol: String = "₹") {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Text(
                text = "${index + 1}",
                color = TextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.width(20.dp)
            )
            
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceGlassBright),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Storefront,
                    null,
                    tint = PremiumGold,
                    modifier = Modifier.size(16.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    merchant.merchant.uppercase(),
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text("${merchant.count} orders", color = TextSecondary, fontSize = 10.sp)
            }
        }
        
        Column(horizontalAlignment = Alignment.End) {
            Text(
                "$currencySymbol${formatIndianCurrency(merchant.amount)}",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun PaymentModeCard(split: Map<String, Double>, currencySymbol: String = "₹") {
    val upi = split["UPI"] ?: 0.0
    val card = split["Card"] ?: 0.0
    val total = upi + card
    val upiRatio = if (total > 0) (upi / total).toFloat() else 0f

    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(SurfaceGlass).padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("UPI / DIGITAL", color = TextSecondary, fontSize = 11.sp)
                Text("$currencySymbol${formatIndianCurrency(upi)}", color = CyanGlow, fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("CARD / OTHERS", color = TextSecondary, fontSize = 11.sp)
                Text("$currencySymbol${formatIndianCurrency(card)}", color = TextPrimary, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape)) {
            if (upiRatio > 0) Box(modifier = Modifier.weight(upiRatio).fillMaxHeight().background(CyanGlow))
            if (1f - upiRatio > 0) Box(modifier = Modifier.weight(1f - upiRatio).fillMaxHeight().background(SurfaceGlassBright))
        }
    }
}

@Composable
fun InsightCard(insight: SmartInsight) {
    Card(
        modifier = Modifier.width(220.dp).height(85.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceGlass)
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(PremiumGold.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when(insight.icon) {
                        "warning" -> Icons.Default.Warning
                        "check" -> Icons.Default.CheckCircle
                        "store" -> Icons.Default.Storefront
                        else -> Icons.AutoMirrored.Filled.TrendingUp
                    },
                    contentDescription = null,
                    tint = when(insight.icon) {
                        "warning" -> ColorFood
                        "check" -> ColorGroceries
                        else -> PremiumGold
                    },
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(insight.title, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(insight.description, color = TextSecondary, fontSize = 10.sp, lineHeight = 14.sp)
            }
        }
    }
}

@Composable
fun MealCardInsightsSection(uiState: InsightsUiState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceGlass)
            .padding(16.dp)
    ) {
        Column {
            Text("MEAL CARD USAGE", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Spent", color = TextSecondary, fontSize = 10.sp)
                    Text("${uiState.currencySymbol}${formatIndianCurrency(uiState.mealCardTotal)}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Transactions", color = TextSecondary, fontSize = 10.sp)
                    Text("${uiState.mealCardCount}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            
            if (uiState.topMealCardMerchants.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = SurfaceGlassBright)
                Spacer(modifier = Modifier.height(12.dp))
                Text("TOP MEAL CARD MERCHANTS", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                
                uiState.topMealCardMerchants.forEachIndexed { index, merchant ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${index + 1}. ${merchant.merchant.uppercase()}", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Text("${uiState.currencySymbol}${formatIndianCurrency(merchant.amount)}", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentChannelsSection(uiState: InsightsUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Multi-Vessel Split
        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(SurfaceGlass).padding(16.dp)) {
            Column {
                Text("SPENDING BY SOURCE", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                
                if (uiState.vesselBreakdown.isEmpty()) {
                    Text("No source data found", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(vertical = 8.dp).align(Alignment.CenterHorizontally))
                } else {
                    uiState.vesselBreakdown.forEach { vessel ->
                        VesselProgressItem(vessel, uiState.currencySymbol)
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth().height(200.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.weight(0.45f).fillMaxHeight().clip(RoundedCornerShape(16.dp)).background(SurfaceGlass).padding(16.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("UPI vs CARD", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
                    Spacer(modifier = Modifier.weight(1f))
                    PaymentModeSplitChart(uiState.upiVsCard)
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Small legend
                    val upi = uiState.upiVsCard["UPI"] ?: 0.0
                    val card = uiState.upiVsCard["Card"] ?: 0.0
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(8.dp).background(CyanGlow, CircleShape))
                            Spacer(Modifier.width(6.dp))
                            Text("UPI: ${uiState.currencySymbol}${formatIndianCurrency(upi)}", fontSize = 10.sp, color = TextPrimary)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(8.dp).background(SurfaceGlassBright, CircleShape))
                            Spacer(Modifier.width(6.dp))
                            Text("Card: ${uiState.currencySymbol}${formatIndianCurrency(card)}", fontSize = 10.sp, color = TextPrimary)
                        }
                    }
                }
            }
            
            Box(modifier = Modifier.weight(0.55f).fillMaxHeight().clip(RoundedCornerShape(16.dp)).background(SurfaceGlass).padding(16.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("MORE INSIGHTS", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    SmallInsightRow(Icons.Default.CalendarToday, "Most Active", uiState.mostActiveDay)
                    SmallInsightRow(Icons.Default.AccessTime, "Peak Time", uiState.peakSpendingTime)
                    SmallInsightRow(Icons.Default.CurrencyRupee, "Avg Txn", "${uiState.currencySymbol}${uiState.avgTransactionSize.toInt()}")
                    
                    val p2p = uiState.p2pVsMerchantSplit.first
                    SmallInsightRow(Icons.Default.CompareArrows, "P2P vs Merchant", "$p2p% / ${100-p2p}%")
                }
            }
        }
    }
}
