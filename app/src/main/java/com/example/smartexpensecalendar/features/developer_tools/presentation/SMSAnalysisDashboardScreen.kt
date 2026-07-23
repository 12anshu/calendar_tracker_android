package com.example.smartexpensecalendar.features.developer_tools.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.smartexpensecalendar.features.developer_tools.data.entity.AnalyzedSMS
import com.example.smartexpensecalendar.features.developer_tools.domain.model.PatternGroup
import com.example.smartexpensecalendar.core.designsystem.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SMSAnalysisDashboardScreen(
    navController: NavController,
    viewModel: SMSAnalysisViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var currentScreen by remember { mutableStateOf("dashboard") }
    var previousScreen by remember { mutableStateOf("dashboard") }
    var selectedSms by remember { mutableStateOf<AnalyzedSMS?>(null) }
    var selectedPatternGroup by remember { mutableStateOf<PatternGroup?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    val navigateTo: (String) -> Unit = { screen ->
        previousScreen = currentScreen
        currentScreen = screen
    }

    val goBack: () -> Unit = {
        when (currentScreen) {
            "dashboard" -> navController.popBackStack()
            "pattern_detail" -> currentScreen = "pattern_groups"
            "review" -> currentScreen = previousScreen
            else -> currentScreen = "dashboard"
        }
    }

    BackHandler(enabled = currentScreen != "dashboard") {
        goBack()
    }

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
                        when(currentScreen) {
                            "dashboard" -> "Financial Detection Lab"
                            "all_sms" -> "All SMS Analysis"
                            "pattern_groups" -> "Pattern Groups"
                            "pattern_detail" -> "Pattern Detail"
                            "borderline" -> "Borderline Messages"
                            "misclassifications" -> "Potential Misclassifications"
                            "review" -> "SMS Analysis Detail"
                            "failed" -> "Detection Failures"
                            "stats" -> "Detector Statistics"
                            "transaction_extraction_lab" -> "Transaction Extraction Lab"
                            else -> "Developer Tools"
                        },
                        color = TextPrimary, 
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = goBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundStart)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BackgroundStart
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (currentScreen) {
                "dashboard" -> DashboardView(uiState, viewModel) { 
                    if (it == "transaction_extraction_lab") {
                        navController.navigate("transaction_extraction_lab")
                    } else {
                        navigateTo(it)
                    }
                }
                "all_sms" -> AllSmsView(viewModel) { sms -> selectedSms = sms; navigateTo("review") }
                "pattern_groups" -> PatternGroupsView(uiState.patternGroups) { group -> selectedPatternGroup = group; navigateTo("pattern_detail") }
                "pattern_detail" -> PatternDetailView(selectedPatternGroup, uiState) { sms -> selectedSms = sms; navigateTo("review") }
                "borderline" -> BorderlineView(uiState.borderlineMessages) { sms -> selectedSms = sms; navigateTo("review") }
                "misclassifications" -> ListSmsView(uiState.potentialMisclassifications) { sms -> selectedSms = sms; navigateTo("review") }
                "review" -> MessageReviewView(selectedSms, viewModel) { goBack() }
                "failed" -> FailedCasesView(uiState.failedCases, viewModel)
                "stats" -> DetectorStatisticsView(uiState)
            }
            
            if (uiState.isAnalyzing) {
                AnalysisOverlay(uiState.progress)
            }
        }
    }
}

@Composable
fun DashboardView(
    uiState: SMSAnalysisUiState,
    viewModel: SMSAnalysisViewModel,
    onNavigate: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Text(
                "FINANCIAL DETECTION LAB: Debug and validate the SMS pipeline.",
                color = PremiumGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SummaryCard("Total SMS", uiState.totalCount.toString(), Icons.Default.Sms, Modifier.weight(1f))
                SummaryCard("Financial", uiState.financialCount.toString(), Icons.Default.AccountBalanceWallet, Modifier.weight(1f), PrimaryAccent)
                SummaryCard("Non-Financial", uiState.nonFinancialCount.toString(), Icons.Default.Block, Modifier.weight(1f), ColorTransport)
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SummaryCard("Confirmed Txn", uiState.financialTransactionCount.toString(), Icons.Default.CheckCircle, Modifier.weight(1.2f), CyanGlow)
                SummaryCard("Obligation", uiState.obligationCount.toString(), Icons.Default.PriorityHigh, Modifier.weight(1f), ColorFood)
                SummaryCard("Info", uiState.informationCount.toString(), Icons.Default.Info, Modifier.weight(1f), SecondaryAccent)
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SummaryCard("Debit", uiState.debitCount.toString(), Icons.Default.ArrowOutward, Modifier.weight(1f), ColorFood)
                SummaryCard("Credit", uiState.creditCount.toString(), Icons.Default.ArrowDownward, Modifier.weight(1f), PrimaryAccent)
                SummaryCard("Dir Unknown", uiState.unknownDirectionCount.toString(), Icons.Default.QuestionMark, Modifier.weight(1f), Color.Gray)
                SummaryCard("Unknown Type", uiState.unknownCount.toString(), Icons.Default.Help, Modifier.weight(1f), Color.Gray)
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SummaryCard("Borderline", uiState.borderlineMessages.size.toString(), Icons.Default.Toll, Modifier.weight(1f), ColorFood)
                SummaryCard("Misclass", uiState.failedCases.size.toString(), Icons.Default.Error, Modifier.weight(1f), Color.Red)
                SummaryCard("Patterns", uiState.patternGroups.size.toString(), Icons.Default.Schema, Modifier.weight(1f), SecondaryAccent)
            }
        }

        item { SectionHeader("New SMS Engine (Engine V2)") }

        item {
            val stats = uiState.engineV2Stats
            EngineV2CategoryCard(
                title = "Qualification",
                items = listOf(
                    "Total SMS" to stats.totalSms.toString(),
                    "Qualified" to stats.qualified.toString(),
                    "Rejected" to stats.rejected.toString(),
                    "Rate" to "${"%.1f".format(stats.qualificationRate)}%"
                ),
                onItemClick = { label ->
                    when (label) {
                        "Qualified" -> viewModel.setV2QualificationFilter(true)
                        "Rejected" -> viewModel.setV2QualificationFilter(false)
                    }
                    onNavigate("all_sms")
                }
            )
        }

        item {
            val latest = uiState.latestQualifiedSms
            EngineV2CategoryCard(
                title = "Extraction",
                items = listOf(
                    "Sender" to (latest?.sender ?: "N/A"),
                    "Qualified" to if (latest?.isQualified == true) "✅ Yes" else "❌ No",
                    "Amount" to (latest?.let { "₹${"%.2f".format(it.amount ?: 0.0)} " } ?: "N/A")
                ),
                onItemClick = { label ->
                    if (label == "Sender" && latest != null) {
                        viewModel.setSearchQuery(latest.sender)
                    }
                    onNavigate("all_sms")
                }
            )
        }

        item { SectionHeader("Actions") }
        
        item {
            ActionCard(
                title = "Run Full Analysis",
                subtitle = "Analyze all SMS using current pipeline",
                icon = Icons.Default.PlayArrow,
                color = PrimaryAccent,
                onClick = { viewModel.runFullAnalysis() }
            )
        }

        item {
            ActionCard(
                title = "Export All Analyzed SMS",
                subtitle = "Save all results to a CSV file in Downloads",
                icon = Icons.Default.Download,
                color = CyanGlow,
                onClick = { viewModel.exportToCSV() }
            )
        }

        item {
            ActionCard(
                title = "Export Raw Messages",
                subtitle = "Sender and Body only (CSV)",
                icon = Icons.Default.Message,
                color = PrimaryAccent,
                onClick = { viewModel.exportRawMessagesToCSV() }
            )
        }

        item {
            ActionCard(
                title = "Export Qualification Analysis",
                subtitle = "Sender, Body and Qualification only (CSV)",
                icon = Icons.Default.Message,
                color = PrimaryAccent,
                onClick = { viewModel.exportQualifierToCSV() }
            )
        }

        item {
            ActionCard(
                title = "Export Extraction Analysis",
                subtitle = "Sender, Body and Extraction Entities only (CSV)",
                icon = Icons.Default.Message,
                color = PrimaryAccent,
                onClick = { viewModel.exportQualifierToCSV() }
            )
        }

        item {
            ActionCard(
                title = "Export Diagnostic JSON (All)",
                subtitle = "Share raw messages for engine improvements",
                icon = Icons.Default.BugReport,
                color = PremiumGold,
                onClick = { viewModel.exportDiagnosticData(month = null, minimal = true) }
            )
        }

        item {
            ActionCard(
                title = "Export Diagnostic JSON (Current Month)",
                subtitle = "Share only this month's messages",
                icon = Icons.Default.CalendarMonth,
                color = SecondaryAccent,
                onClick = { viewModel.exportDiagnosticData(month = java.time.YearMonth.now(), minimal = true) }
            )
        }

        item { SectionHeader("Analysis Views") }

        item {
            AnalysisNavigationCard("All SMS Analysis", "Detailed results for each SMS", Icons.Default.List) { onNavigate("all_sms") }
        }
        item {
            AnalysisNavigationCard("Pattern Groups", "Discovered templates and stats", Icons.Default.Schema) { onNavigate("pattern_groups") }
        }
        item {
            AnalysisNavigationCard("Borderline Messages", "Messages needing refinement (40-60)", Icons.Default.Toll) { onNavigate("borderline") }
        }
        item {
            AnalysisNavigationCard("Detection Failures", "Manually flagged misclassifications", Icons.Default.BugReport) { onNavigate("failed") }
        }
        item {
            AnalysisNavigationCard("Detector Statistics", "Pipeline performance and trends", Icons.Default.BarChart) { onNavigate("stats") }
        }
    }
}

@Composable
fun SummaryCard(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    color: Color = TextPrimary,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceGlass)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(12.dp)
    ) {
        Column {
            Icon(icon, contentDescription = null, tint = color.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(label, color = TextSecondary, fontSize = 9.sp, maxLines = 1)
        }
    }
}

@Composable
fun ActionCard(title: String, subtitle: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.1f))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, color = color, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(subtitle, color = color.copy(alpha = 0.7f), fontSize = 12.sp)
        }
    }
}

@Composable
fun AnalysisNavigationCard(title: String, subtitle: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceGlass)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(SurfaceGlassBright),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = TextPrimary)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(subtitle, color = TextSecondary, fontSize = 12.sp)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        color = CyanGlow,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
fun EngineV2CategoryCard(
    title: String,
    items: List<Pair<String, String>>,
    onItemClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceGlass)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = CyanGlow, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(12.dp))
            
            items.forEach { (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(label) }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(label, color = TextSecondary, fontSize = 13.sp)
                    Text(value, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

fun calculatePercent(count: Int, total: Int): Int {
    if (total == 0) return 0
    return (count.toFloat() / total * 100).toInt()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllSmsView(viewModel: SMSAnalysisViewModel, onSmsClick: (AnalyzedSMS) -> Unit) {
    val smsItems = viewModel.analyzedSMSPaged.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val financialFilter by viewModel.financialFilter.collectAsState()
    val messageTypeFilter by viewModel.messageTypeFilter.collectAsState()
    val directionFilter by viewModel.directionFilter.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { 
                    searchQuery = it
                    viewModel.setSearchQuery(it)
                },
                placeholder = { Text("Search keywords...", color = TextSecondary) },
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = SurfaceGlass,
                    unfocusedContainerColor = SurfaceGlass,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                singleLine = true
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            IconButton(
                onClick = { viewModel.toggleScoreSort() },
                modifier = Modifier.background(SurfaceGlass, RoundedCornerShape(12.dp))
            ) {
                Icon(
                    if (viewModel.isScoreAsc()) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                    contentDescription = "Sort Score",
                    tint = CyanGlow
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            FilterChip(
                selected = financialFilter == null && messageTypeFilter == null,
                onClick = {
                    viewModel.setFinancialFilter(null)
                    viewModel.setMessageTypeFilter(null)
                },
                label = { Text("All (${uiState.totalCount})") }
            )

            FilterChip(
                selected = financialFilter == true && messageTypeFilter == null,
                onClick = {
                    viewModel.setFinancialFilter(true)
                    viewModel.setMessageTypeFilter(null)
                },
                label = { Text("Fin (${uiState.financialCount})") }
            )

            FilterChip(
                selected = financialFilter == false,
                onClick = {
                    viewModel.setFinancialFilter(false)
                    viewModel.setMessageTypeFilter(null)
                },
                label = { Text("Non-Fin (${uiState.nonFinancialCount})") }
            )

            FilterChip(
                selected = financialFilter == true && messageTypeFilter == "UNKNOWN",
                onClick = {
                    viewModel.setFinancialFilter(true)
                    viewModel.setMessageTypeFilter("UNKNOWN")
                },
                label = { Text("Unknown (${uiState.unknownCount})") }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            FilterChip(
                selected = messageTypeFilter == "TRANSACTION",
                onClick = {
                    viewModel.setMessageTypeFilter("TRANSACTION")
                    viewModel.setFinancialFilter(true)
                },
                label = { Text("Txn") }
            )

            FilterChip(
                selected = messageTypeFilter == "OBLIGATION",
                onClick = {
                    viewModel.setMessageTypeFilter("OBLIGATION")
                    viewModel.setFinancialFilter(true)
                },
                label = { Text("Obligation") }
            )

            FilterChip(
                selected = messageTypeFilter == "INFORMATION",
                onClick = {
                    viewModel.setMessageTypeFilter("INFORMATION")
                    viewModel.setFinancialFilter(true)
                },
                label = { Text("Info") }
            )

            FilterChip(
                selected = messageTypeFilter == "PROMOTIONAL",
                onClick = {
                    viewModel.setMessageTypeFilter("PROMOTIONAL")
                    viewModel.setFinancialFilter(true)
                },
                label = { Text("Promo") }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            FilterChip(
                selected = directionFilter == null,
                onClick = {
                    viewModel.setDirectionFilter(null)
                },
                label = { Text("All Directions") }
            )

            FilterChip(
                selected = directionFilter == "DEBIT",
                onClick = {
                    viewModel.setDirectionFilter("DEBIT")
                },
                label = { Text("Debit") }
            )

            FilterChip(
                selected = directionFilter == "CREDIT",
                onClick = {
                    viewModel.setDirectionFilter("CREDIT")
                },
                label = { Text("Credit") }
            )

            FilterChip(
                selected = directionFilter == "UNKNOWN",
                onClick = {
                    viewModel.setDirectionFilter("UNKNOWN")
                },
                label = { Text("Unknown") }
            )
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(smsItems.itemCount) { index ->
                val sms = smsItems[index]
                if (sms != null) {
                    SmsItem(sms) { onSmsClick(sms) }
                }
            }
        }
    }
}

@Composable
fun ListSmsView(messages: List<AnalyzedSMS>, onSmsClick: (AnalyzedSMS) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(messages) { sms ->
            SmsItem(sms) { onSmsClick(sms) }
        }
    }
}

@Composable
fun BorderlineView(messages: List<AnalyzedSMS>, onSmsClick: (AnalyzedSMS) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(messages) { sms ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSmsClick(sms) }
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(sms.sender, color = SecondaryAccent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.weight(1f))
                    Text("Score: ${sms.score}", color = ColorFood, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                
                Text(sms.message, color = TextPrimary, fontSize = 13.sp, maxLines = 2)
                
                Spacer(modifier = Modifier.height(4.dp))
                
                val whyBorderline = when {
                    sms.matchedKeywords.isNotEmpty() && sms.matchedPatterns.isEmpty() -> "Keywords found but no patterns (Amount/Account)"
                    sms.matchedPatterns.isNotEmpty() && sms.matchedKeywords.isEmpty() -> "Patterns found (Amount) but no financial keywords"
                    else -> "Mixed signals with low individual scores"
                }
                
                Text("Why Borderline: $whyBorderline", color = PremiumGold, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                
                Divider(modifier = Modifier.padding(top = 12.dp), color = SurfaceGlassBright, thickness = 0.5.dp)
            }
        }
    }
}

@Composable
fun PatternGroupsView(groups: List<PatternGroup>, onGroupClick: (PatternGroup) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(groups) { group ->
            PatternGroupItem(group) { onGroupClick(group) }
        }
    }
}

@Composable
fun PatternDetailView(group: PatternGroup?, uiState: SMSAnalysisUiState, onSmsClick: (AnalyzedSMS) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        PatternGroupItem(group ?: return@Column) {}
        Spacer(modifier = Modifier.height(24.dp))
        Text("Sample Message:", color = TextSecondary, fontSize = 12.sp)
        Box(modifier = Modifier.fillMaxWidth().background(SurfaceGlass, RoundedCornerShape(12.dp)).padding(12.dp)) {
            Text(group.sampleMessage, color = TextPrimary, fontSize = 14.sp)
        }
    }
}

@Composable
fun PatternGroupItem(group: PatternGroup, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .background(SurfaceGlass, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Text(group.template, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(8.dp))
        
        val financialPercent = if (group.count > 0) (group.financialCount.toFloat() / group.count * 100).toInt() else 0
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Count: ${group.count}", color = TextSecondary, fontSize = 11.sp)
                Text("Fin %: $financialPercent%", color = if (financialPercent > 80) PrimaryAccent else ColorFood, fontSize = 11.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Avg Score: ${group.averageScore.toInt()}", color = CyanGlow, fontSize = 11.sp)
                Text("Type: Transaction", color = TextSecondary, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun SmsItem(sms: AnalyzedSMS, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(sms.sender, color = SecondaryAccent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                color = if (sms.isFinancial) PrimaryAccent.copy(alpha = 0.1f) else ColorTransport.copy(alpha = 0.1f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    if (sms.isFinancial) "FINANCIAL" else "NON-FINANCIAL",
                    color = if (sms.isFinancial) PrimaryAccent else ColorTransport,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            if (sms.isFinancial && sms.messageType.isNotBlank()) {

                Spacer(modifier = Modifier.width(4.dp))

                Surface(
                    color = SecondaryAccent.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        sms.messageType,
                        color = SecondaryAccent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            horizontal = 4.dp,
                            vertical = 2.dp
                        )
                    )
                }
            }
            Text("Score: ${sms.score}", color = if (sms.score > 50) CyanGlow else TextSecondary, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            sms.message, 
            color = TextPrimary, 
            fontSize = 13.sp, 
            maxLines = 2,
            lineHeight = 18.sp
        )
        Divider(modifier = Modifier.padding(top = 12.dp), color = SurfaceGlassBright, thickness = 0.5.dp)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MessageReviewView(sms: AnalyzedSMS?, viewModel: SMSAnalysisViewModel, onComplete: () -> Unit) {
    if (sms == null) return
    
    var showWrongDialog by remember { mutableStateOf(false) }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { ReviewSection("Sender", sms.sender) }
        item { ReviewSection("Original SMS", sms.message) }
        
        // 1. Qualification Analysis Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceGlass)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("QUALIFICATION ANALYSIS", color = CyanGlow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Confidence", color = TextSecondary, fontSize = 11.sp)
                            Text("${sms.qualificationConfidence}%", color = PremiumGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Column {
                            Text("Score", color = TextSecondary, fontSize = 11.sp)
                            Text("${sms.qualificationScore}", color = PremiumGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Qualified", color = TextSecondary, fontSize = 11.sp)
                            Text(
                                if (sms.isQualified) "YES" else "NO",
                                color = if (sms.isQualified) PrimaryAccent else ColorTransport,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                    
                    if (sms.qualificationRules.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Executed Rules:", color = TextSecondary, fontSize = 11.sp)
                        sms.qualificationRules.forEach { rule ->
                            Text("✔ $rule", color = TextPrimary.copy(alpha = 0.8f), fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // 2. Classification Analysis Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceGlass)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("CLASSIFICATION ANALYSIS", color = CyanGlow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Direction Section
                    V2ClassificationSection(
                        title = "Direction",
                        value = sms.classifiedDirection,
                        confidence = sms.classifiedDirectionConfidence,
                        score = sms.classifiedDirectionScore,
                        evidence = sms.classifiedDirectionEvidence,
                        matches = sms.classifiedDirectionMatches
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = SurfaceGlassBright.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Message Type Section
                    V2ClassificationSection(
                        title = "Message Type",
                        value = sms.classifiedMessageType,
                        confidence = sms.classifiedMessageTypeConfidence,
                        score = sms.classifiedMessageTypeScore,
                        evidence = sms.classifiedMessageTypeEvidence,
                        matches = sms.classifiedMessageTypeMatches
                    )
                }
            }
        }

        // 3. Merchant Analysis Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceGlass)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("MERCHANT ANALYSIS", color = CyanGlow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Merchant: ${sms.merchant ?: "None"}",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                        Column {
                            Text("Confidence", color = TextSecondary, fontSize = 11.sp)
                            Text("${sms.merchantConfidence}", color = PremiumGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Column {
                            Text("Score", color = TextSecondary, fontSize = 11.sp)
                            Text("${sms.merchantScore}", color = PremiumGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                    if (sms.merchantEvidence.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Evidence:", color = TextSecondary, fontSize = 11.sp)
                        Text(
                            text = sms.merchantEvidence.joinToString("\n") { "• $it" },
                            color = TextPrimary.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        /*
        item { DetectorExplanationPanel(sms.scoreBreakdown) }
        item { SignalList("Matched Keywords", sms.matchedKeywords, PrimaryAccent) }
        */
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Manual Review", color = PremiumGold, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { onComplete() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Correct")
                }
                OutlinedButton(
                    onClick = { showWrongDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ColorFood),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ColorFood)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Wrong")
                }
            }
        }
    }

    if (showWrongDialog) {
        AlertDialog(
            onDismissRequest = { showWrongDialog = false },
            title = { Text("Select Expected Classification", color = TextPrimary) },
            text = {
                Column {
                    val options = listOf("Non Financial", "Transaction", "Obligation", "Information", "Promotional")
                    options.forEach { option ->
                        Text(
                            text = option,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { 
                                    viewModel.flagMisclassification(sms, option)
                                    showWrongDialog = false
                                    onComplete()
                                }
                                .padding(vertical = 12.dp),
                            color = TextPrimary
                        )
                    }
                }
            },
            confirmButton = {},
            containerColor = BackgroundEnd
        )
    }
}

@Composable
fun V2ClassificationSection(
    title: String,
    value: String,
    confidence: Int,
    score: Int,
    evidence: List<String>,
    matches: List<String>
) {
    Column {
        Text(title, color = CyanGlow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Result", color = TextSecondary, fontSize = 10.sp)
                Text(value, color = PremiumGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Column {
                Text("Confidence", color = TextSecondary, fontSize = 10.sp)
                Text("$confidence%", color = PremiumGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Score", color = TextSecondary, fontSize = 10.sp)
                Text("$score", color = PremiumGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }

        if (evidence.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text("Evidence:", color = TextSecondary, fontSize = 11.sp)
            Text(
                text = evidence.joinToString("\n") { "• $it" },
                color = TextPrimary.copy(alpha = 0.8f),
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
        }

        if (matches.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text("Matches:", color = TextSecondary, fontSize = 11.sp)
            Text(
                text = matches.joinToString("\n") { "• $it" },
                color = TextPrimary.copy(alpha = 0.8f),
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun DetectorExplanationPanel(breakdown: Map<String, Int>) {
    Column(modifier = Modifier.fillMaxWidth().background(SurfaceGlass, RoundedCornerShape(12.dp)).padding(16.dp)) {
        Text("DETECTOR EXPLANATION", color = CyanGlow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(8.dp))
        breakdown.forEach { (signal, score) ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(signal, color = TextSecondary, fontSize = 12.sp)
                Text(if (score > 0) "+$score" else "$score", color = if (score > 0) PrimaryAccent else ColorFood, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
        Divider(modifier = Modifier.padding(vertical = 8.dp), color = SurfaceGlassBright)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Final Score", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("${breakdown.values.sum()}", color = CyanGlow, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SignalList(label: String, signals: Set<String>, color: Color) {
    if (signals.isEmpty()) return
    Column {
        Text(label, color = TextSecondary, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(4.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            signals.forEach { signal ->
                Surface(
                    color = color.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, color.copy(alpha = 0.5f))
                ) {
                    Text(signal, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
        }
    }
}

@Composable
fun ReviewSection(label: String, content: String) {
    Column {
        Text(label, color = TextSecondary, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth().background(SurfaceGlass, RoundedCornerShape(12.dp)).padding(12.dp)) {
            Text(content, color = TextPrimary, fontSize = 13.sp)
        }
    }
}

@Composable
fun FailedCasesView(cases: List<com.example.smartexpensecalendar.features.developer_tools.data.entity.MisclassifiedMessage>, viewModel: SMSAnalysisViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Button(
            onClick = { 
                val json = viewModel.exportFailedCases()
                println(json) 
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = SecondaryAccent)
        ) {
            Icon(Icons.Default.Download, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Export Failure Dataset (JSON)")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(cases) { case ->
                Column(modifier = Modifier.fillMaxWidth().background(SurfaceGlass, RoundedCornerShape(12.dp)).padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(case.sender, color = SecondaryAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Spacer(modifier = Modifier.weight(1f))
                        Text("Score: ${case.score}", color = CyanGlow, fontSize = 11.sp)
                    }
                    Text(case.message, color = TextPrimary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Current", color = TextSecondary, fontSize = 10.sp)
                            Text(case.currentClassification, color = ColorFood, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Expected", color = TextSecondary, fontSize = 10.sp)
                            Text(case.expectedClassification, color = PrimaryAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Signals: ${case.matchedSignals.joinToString(", ")}", color = TextSecondary, fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Reviewed: ${java.text.SimpleDateFormat("MMM dd, HH:mm", java.util.Locale.getDefault()).format(java.util.Date(case.reviewTimestamp))}", color = TextSecondary, fontSize = 9.sp)
                }
            }
        }
    }
}

@Composable
fun DetectorStatisticsView(uiState: SMSAnalysisUiState) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        item {
            StatSection("Top Financial Keywords", uiState.topFinancialKeywords.map { "${it.first} (${it.second})" })
        }
        item {
            StatSection("Top Negative Keywords", uiState.topNegativeKeywords.map { "${it.first} (${it.second})" })
        }
        item {
            StatSection("Top Financial Senders", uiState.topFinancialSenders.map { "${it.sender} (${it.count})" })
        }
        item {
            StatSection("Top Non-Financial Senders", uiState.topNonFinancialSenders.map { "${it.sender} (${it.count})" })
        }
    }
}

@Composable
fun StatSection(title: String, items: List<String>) {
    Column {
        Text(title, color = CyanGlow, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Column(modifier = Modifier.fillMaxWidth().background(SurfaceGlass, RoundedCornerShape(12.dp)).padding(12.dp)) {
            if (items.isEmpty()) {
                Text("No data available", color = TextSecondary, fontSize = 12.sp)
            } else {
                items.forEach { item ->
                    Text(item, color = TextPrimary, fontSize = 12.sp, modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}

@Composable
fun AnalysisOverlay(progress: Float) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(progress = { progress }, color = CyanGlow)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Analyzing SMS Messages...", color = TextPrimary, fontWeight = FontWeight.Bold)
            Text("${(progress * 100).toInt()}%", color = TextSecondary, fontSize = 12.sp)
        }
    }
}
