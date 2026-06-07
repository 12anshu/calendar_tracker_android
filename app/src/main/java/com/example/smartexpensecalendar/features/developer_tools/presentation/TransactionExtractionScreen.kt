package com.example.smartexpensecalendar.features.developer_tools.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smartexpensecalendar.core.designsystem.theme.*
import com.example.smartexpensecalendar.domain.model.FinancialEventType
import com.example.smartexpensecalendar.domain.model.TransactionDirection
import com.example.smartexpensecalendar.domain.model.TransactionMode

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TransactionExtractionScreen(
    navController: NavController,
    viewModel: TransactionExtractionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val results by viewModel.filteredResults.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.exportStatus.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Transaction Extraction Lab",
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BackgroundStart
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Text(
                    "TRANSACTION EXTRACTION LAB: Validate extraction quality.",
                    color = PremiumGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Existing Dashboard Cards
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SummaryCard(
                            label = "Total",
                            value = uiState.totalTransactions.toString(),
                            icon = Icons.Default.Sms,
                            modifier = Modifier.weight(1f),
                            color = if (uiState.selectedFilter == ExtractionFilter.ALL) CyanGlow else TextPrimary,
                            onClick = { viewModel.setFilter(ExtractionFilter.ALL) }
                        )
                        SummaryCard(
                            label = "Extracted",
                            value = uiState.amountExtractedCount.toString(),
                            icon = Icons.Default.CheckCircle,
                            modifier = Modifier.weight(1f),
                            color = PrimaryAccent
                        )
                        SummaryCard(
                            label = "Failed",
                            value = uiState.extractionFailedCount.toString(),
                            icon = Icons.Default.Error,
                            modifier = Modifier.weight(1f),
                            color = if (uiState.selectedFilter == ExtractionFilter.FAILED) ColorFood else TextPrimary,
                            onClick = { viewModel.setFilter(ExtractionFilter.FAILED) }
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SummaryCard(
                            label = "Merc Found",
                            value = uiState.merchantExtractedCount.toString(),
                            icon = Icons.Default.Store,
                            modifier = Modifier.weight(1f),
                            color = PrimaryAccent
                        )
                        SummaryCard(
                            label = "Merc Missing",
                            value = uiState.merchantMissingCount.toString(),
                            icon = Icons.Default.QuestionMark,
                            modifier = Modifier.weight(1f),
                            color = if (uiState.selectedFilter == ExtractionFilter.MERCHANT_MISSING) ColorFood else TextPrimary,
                            onClick = { viewModel.setFilter(ExtractionFilter.MERCHANT_MISSING) }
                        )
                        SummaryCard(
                            label = "Coverage",
                            value = "${(uiState.merchantCoverage * 100).toInt()}%",
                            icon = Icons.Default.Percent,
                            modifier = Modifier.weight(1f),
                            color = CyanGlow
                        )
                    }
                }
            }

            // Section 1: Event Type Distribution
            item { SectionHeader("Event Type Distribution") }
            item {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    maxItemsInEachRow = 3,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.eventTypeDistribution.forEach { (type, count) ->
                        val percent = if (uiState.totalTransactions > 0) (count * 100 / uiState.totalTransactions) else 0
                        DistributionSmallCard(
                            label = type,
                            value = "$count ($percent%)",
                            isWarning = type == "UNKNOWN",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Section 2: Confidence Distribution
            item { SectionHeader("Confidence Distribution") }
            item {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    maxItemsInEachRow = 3,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.confidenceDistribution.forEach { (range, count) ->
                        val percent = if (uiState.totalTransactions > 0) (count * 100 / uiState.totalTransactions) else 0
                        val color = when(range) {
                            "100%" -> Color.Green
                            "90-99%" -> CyanGlow
                            "80-89%" -> PrimaryAccent
                            "70-79%" -> ColorFood
                            else -> Color.Red
                        }
                        DistributionSmallCard(
                            label = range,
                            value = "$count ($percent%)",
                            color = color,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Section 5: Extraction Quality Metrics
            item { SectionHeader("Extraction Quality") }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        QualityCard("Amount Rate", uiState.amountExtractionRate, Modifier.weight(1f))
                        QualityCard("Merc Coverage", uiState.merchantCoverage, Modifier.weight(1f))
                        QualityCard("Direction Acc", uiState.directionDetectionRate, Modifier.weight(1f))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        QualityCard("Mode Rate", uiState.modeDetectionRate, Modifier.weight(1f))
                        QualityCard("Event Type Rate", uiState.eventTypeDetectionRate, Modifier.weight(1f))
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            // Section 4: Top Senders (Collapsible)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceGlass)
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.toggleSenderSection() }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Top 10 Senders", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Icon(
                                if (uiState.isSenderSectionExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = TextSecondary
                            )
                        }
                        AnimatedVisibility(visible = uiState.isSenderSectionExpanded) {
                            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                uiState.topSenders.forEach { (sender, count) ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(sender, color = TextSecondary, fontSize = 13.sp)
                                        Text(count.toString(), color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item { SectionHeader("Actions & Tools") }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ActionSmallButton("Run Analysis", Icons.Default.PlayArrow, PrimaryAccent, Modifier.weight(1f)) { viewModel.runExtraction() }
                    ActionSmallButton("Export All", Icons.Default.Download, CyanGlow, Modifier.weight(1f)) { viewModel.exportToCSV() }
                }
            }

            item {
                Text("Developer Tools (Rule Tuning)", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            item {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    maxItemsInEachRow = 2,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ToolButton("Export Failed", Modifier.weight(1f)) { viewModel.exportFiltered("FAILED") }
                    ToolButton("Export No Merchant", Modifier.weight(1f)) { viewModel.exportFiltered("MERCHANT_MISSING") }
                    ToolButton("Export Unknown Event", Modifier.weight(1f)) { viewModel.exportFiltered("UNKNOWN_EVENT") }
                    ToolButton("Export Low Conf", Modifier.weight(1f)) { viewModel.exportFiltered("LOW_CONFIDENCE") }
                }
            }

            // Section 3: Quick Filter Chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Filters", color = CyanGlow, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
            item {
                ScrollableTabRow(
                    selectedTabIndex = ExtractionFilter.values().indexOf(uiState.selectedFilter),
                    containerColor = Color.Transparent,
                    edgePadding = 0.dp,
                    divider = {},
                    indicator = {}
                ) {
                    ExtractionFilter.values().forEach { filter ->
                        FilterChip(
                            selected = uiState.selectedFilter == filter,
                            onClick = { viewModel.setFilter(filter) },
                            label = { Text(filter.name.replace("_", " "), fontSize = 12.sp) },
                            modifier = Modifier.padding(horizontal = 4.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryAccent,
                                containerColor = SurfaceGlassBright,
                                labelColor = TextSecondary,
                                selectedLabelColor = Color.White
                            ),
                            border = null
                        )
                    }
                }
            }

            item { SectionHeader("Results (${results.size})") }

            if (uiState.isRunning) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = CyanGlow)
                    }
                }
            } else if (results.isEmpty()) {
                item {
                    Text("No results matching filter.", color = TextSecondary, fontSize = 14.sp)
                }
            } else {
                items(results, key = { it.sms.id }) { result ->
                    ExtractionResultItem(result, 
                        onToggleExpand = { viewModel.toggleResultExpansion(result) }
                    )
                }
            }
        }
    }
}

@Composable
fun DistributionSmallCard(label: String, value: String, modifier: Modifier = Modifier, isWarning: Boolean = false, color: Color = TextPrimary) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isWarning) ColorFood.copy(alpha = 0.1f) else SurfaceGlass)
            .padding(8.dp)
    ) {
        Column {
            Text(label, color = TextSecondary, fontSize = 9.sp, maxLines = 1)
            Text(value, color = if (isWarning) ColorFood else color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun QualityCard(label: String, value: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceGlass)
            .padding(10.dp)
    ) {
        Column {
            Text(label, color = TextSecondary, fontSize = 9.sp)
            Text("${(value * 100).toInt()}%", color = if (value > 0.9f) PrimaryAccent else if (value > 0.7f) CyanGlow else ColorFood, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ActionSmallButton(label: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = color.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ToolButton(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, SurfaceGlassBright)
    ) {
        Text(label, fontSize = 10.sp)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExtractionResultItem(result: ExtractionResult, onToggleExpand: () -> Unit) {
    val clipboardManager = LocalClipboardManager.current
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceGlass)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // First Row: Sender and Confidence
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(result.sms.sender, color = SecondaryAccent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                
                val confColor = if (result.extraction.confidence >= 90) Color.Green 
                               else if (result.extraction.confidence >= 70) ColorFood 
                               else Color.Red
                Surface(
                    color = confColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        "${result.extraction.confidence}% Conf",
                        color = confColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Amount and Merchant
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("₹${result.extraction.amount ?: "N/A"}", color = PrimaryAccent, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(12.dp))
                Text(result.extraction.merchant ?: "Not Detected", color = if (result.extraction.merchant != null) TextPrimary else ColorFood, fontSize = 13.sp)
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            // Badges Row
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                BadgeChip(result.extraction.direction.name, when(result.extraction.direction) {
                    TransactionDirection.CREDIT -> Color.Green
                    TransactionDirection.DEBIT -> Color.Red
                    else -> Color.Gray
                })
                BadgeChip(result.extraction.mode.name, SecondaryAccent)
                BadgeChip(result.extraction.eventType.name, when(result.extraction.eventType) {
                    FinancialEventType.REFUND -> Color(0xFFA020F0) // Purple
                    FinancialEventType.TRANSFER -> Color.Blue
                    else -> CyanGlow
                })
            }

            Spacer(modifier = Modifier.height(12.dp))
            
            // SMS Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceGlassBright)
                    .clickable { onToggleExpand() }
                    .padding(8.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("SMS Body", color = TextSecondary, fontSize = 9.sp)
                    Icon(
                        if (result.isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Text(
                    result.sms.message, 
                    color = TextPrimary, 
                    fontSize = 11.sp, 
                    maxLines = if (result.isExpanded) Int.MAX_VALUE else 2,
                    lineHeight = 15.sp
                )
            }
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(
                    onClick = { clipboardManager.setText(AnnotatedString(result.sms.message)) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextSecondary, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun BadgeChip(label: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, color.copy(alpha = 0.5f))
    ) {
        Text(
            label,
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
