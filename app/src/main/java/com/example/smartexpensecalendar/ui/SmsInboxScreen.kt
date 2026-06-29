package com.example.smartexpensecalendar.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smartexpensecalendar.core.designsystem.theme.*
import com.example.smartexpensecalendar.features.developer_tools.data.entity.AnalyzedSMS
import com.example.smartexpensecalendar.presentation.sms_inbox.SmsInboxViewModel
import com.example.smartexpensecalendar.ui.components.MonthYearPicker
import com.example.smartexpensecalendar.ui.components.FintechBottomNav
import com.example.smartexpensecalendar.utils.CurrencyUtils.formatIndianCurrency
import com.example.smartexpensecalendar.utils.ExpenseDisplayUtils
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import com.example.smartexpensecalendar.domain.model.TransactionDirection
import com.example.smartexpensecalendar.presentation.sms_inbox.ReviewStatus

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun SmsInboxScreen(
    navController: NavController,
    viewModel: SmsInboxViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showMonthPicker by remember { mutableStateOf(false) }
    var selectedSmsForDetail by remember { mutableStateOf<AnalyzedSMS?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextButton(onClick = { showMonthPicker = true }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${uiState.selectedMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${uiState.selectedMonth.year}",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(Icons.Default.KeyboardArrowDown, null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                        }
                    }
                    if (showMonthPicker) {
                        MonthYearPicker(
                            initialMonth = uiState.selectedMonth,
                            onDismiss = { showMonthPicker = false },
                            onConfirm = { viewModel.setMonth(it); showMonthPicker = false }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundStart)
            )
        },
        bottomBar = {
            FintechBottomNav(navController = navController)
        },
        containerColor = BackgroundStart
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {

            // Filter Row 0: Financial Decision (Detector Result)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple("ALL", null as Boolean?, Color.White),
                    Triple("FINANCIAL (${uiState.financialCount})", true, CyanGlow),
                    Triple("NON-FINANCIAL (${uiState.nonFinancialCount})", false, ColorTransport)
                ).forEach { (label, value, color) ->
                    FilterChip(
                        selected = uiState.financialFilter == value,
                        onClick = { viewModel.setFinancialFilter(value) },
                        label = { Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = color,
                            selectedLabelColor = BackgroundStart
                        )
                    )
                }
            }

            // Filter Row 1: Direction
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val directions = listOf(
                    "DEBIT" to "DEBIT (${uiState.debitCount})",
                    "CREDIT" to "CREDIT (${uiState.creditCount})",
                    "UNKNOWN" to "UNKNOWN (${uiState.unknownDirectionCount})"
                )
                directions.forEach { (dir, label) ->
                    FilterChip(
                        selected = uiState.directionFilter == dir,
                        onClick = { viewModel.setDirectionFilter(if (uiState.directionFilter == dir) null else dir) },
                        label = { Text(label, fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyanGlow,
                            selectedLabelColor = BackgroundStart
                        )
                    )
                }
            }

            // Filter Row 2: Message Type
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val types = listOf(
                    "TRANSACTION" to "TRANSACTION (${uiState.financialTransactionCount})",
                    "OBLIGATION" to "OBLIGATION",
                    "INFORMATION" to "INFORMATION",
                    "UNKNOWN" to "UNKNOWN"
                )
                types.forEach { (type, label) ->
                    FilterChip(
                        selected = uiState.messageTypeFilter == type,
                        onClick = { viewModel.setMessageTypeFilter(if (uiState.messageTypeFilter == type) null else type) },
                        label = { Text(label, fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyanGlow,
                            selectedLabelColor = BackgroundStart
                        )
                    )
                }
            }

            // Filter Row 3: Financial Event Type
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 16.dp, end = 16.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val events = listOf("EXPENSE", "INCOME", "REFUND", "TRANSFER", "EMI_PAYMENT")
                events.forEach { event ->
                    FilterChip(
                        selected = uiState.financialEventTypeFilter == event,
                        onClick = { viewModel.setFinancialEventTypeFilter(if (uiState.financialEventTypeFilter == event) null else event) },
                        label = { Text(event, fontSize = 9.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryAccent,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Filter Row 4: Review Status
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ReviewStatus.values().forEach { status ->
                    FilterChip(
                        selected = uiState.reviewStatusFilter == status,
                        onClick = { viewModel.setReviewStatusFilter(status) },
                        label = { Text(status.name, fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PremiumGold,
                            selectedLabelColor = BackgroundStart
                        )
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                if (uiState.smsByDate.isEmpty()) {
                    item {
                        Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No messages found", color = TextSecondary)
                        }
                    }
                }

                uiState.smsByDate.keys.sortedDescending().forEach { date ->
                    stickyHeader {
                        SmsDateHeader(date)
                    }

                    items(uiState.smsByDate[date] ?: emptyList(), key = { it.id }) { sms ->
                        SmsInboxItem(
                            sms = sms,
                            onDoneClick = { viewModel.toggleReviewStatus(sms.id, sms.isReviewed) },
                            onFlagClick = { viewModel.toggleFlagStatus(sms.id, sms.isFlagged) },
                            getDirectionColor = ::getDirectionColor,
                            onClick = { selectedSmsForDetail = sms }
                        )
                    }
                }
            }
        }
    }

    if (selectedSmsForDetail != null) {
        SmsDetailDialog(sms = selectedSmsForDetail!!) {
            selectedSmsForDetail = null
        }
    }
}

@Composable
fun SmsDateHeader(date: LocalDate) {
    val formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy")
    val dateText = when (date) {
        LocalDate.now() -> "Today"
        LocalDate.now().minusDays(1) -> "Yesterday"
        else -> date.format(formatter)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundStart)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = dateText,
            style = MaterialTheme.typography.labelLarge,
            color = TextSecondary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SmsInboxItem(
    sms: AnalyzedSMS,
    onDoneClick: () -> Unit,
    onFlagClick: () -> Unit,
    getDirectionColor: (TransactionDirection) -> Color = ::getDirectionColor,
    onClick: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (sms.isReviewed) SurfaceGlass.copy(alpha = 0.4f) else SurfaceGlass)
            .border(
                width = 1.dp,
                color = if (sms.isFlagged) Color.Red.copy(alpha = 0.5f) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(10.dp)
    ) {
        // Line 1: Financial status | Message Type | Event Type | Mode
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Tag(
                sms.direction.name,
                getDirectionColor(sms.direction)
            )
            Text("|", color = TextSecondary.copy(alpha = 0.3f), fontSize = 10.sp)
            Tag(if (sms.isFinancial) "FIN" else "NON-FIN", if (sms.isFinancial) CyanGlow else TextSecondary)
            Text("|", color = TextSecondary.copy(alpha = 0.3f), fontSize = 10.sp)
            Tag(sms.messageType, TextSecondary)
            Text("|", color = TextSecondary.copy(alpha = 0.3f), fontSize = 10.sp)
            Tag(sms.financialEventType, PrimaryAccent)
            Text("|", color = TextSecondary.copy(alpha = 0.3f), fontSize = 10.sp)
            Tag(sms.transactionMode, SecondaryAccent)
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Line 2: Merchant | Category * Account/Card Name vessel | Amount
        Row(verticalAlignment = Alignment.CenterVertically) {
            val vesselInfo = buildAnnotatedString {
                withStyle(SpanStyle(color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)) {
                    append(sms.merchant?.uppercase() ?: "NONE")
                }
                append(" | ")
                withStyle(SpanStyle(color = TextSecondary, fontSize = 11.sp)) {
                    append(sms.category ?: "UNCATEGORIZED")
                }
                withStyle(SpanStyle(color = TextSecondary.copy(alpha = 0.5f))) {
                    append(" | ")
                }
                withStyle(SpanStyle(color = SecondaryAccent.copy(alpha = 0.8f), fontSize = 11.sp)) {
                    append(ExpenseDisplayUtils.getVesselDisplay(sms.accountName))
                }
            }
            Text(text = vesselInfo, modifier = Modifier.weight(1f), maxLines = 1)

            if (sms.amount != null) {
                val isCredit = sms.financialEventType == "INCOME" || sms.financialEventType == "REFUND"
                Text(
                    text = "${if (isCredit) "+" else "-"} ₹${formatIndianCurrency(sms.amount)}",
                    color = if (isCredit) ColorGroceries else TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Line 3: Message body
        Text(
            text = sms.message,
            color = if (sms.isReviewed) TextPrimary.copy(alpha = 0.5f) else TextPrimary,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Line 4: Signals | Score + Action Buttons
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "S:",
                    color = PremiumGold,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = sms.matchedSignals.joinToString(", "),
                    color = TextSecondary.copy(alpha = 0.6f),
                    fontSize = 9.sp,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                if (sms.directionEvidence.isNotEmpty()) {
                    Text(
                        text = "Dir: ${
                            sms.directionEvidence
                                .take(3)
                                .joinToString(", ")
                        }",
                        color = PremiumGold.copy(alpha = 0.8f),
                        fontSize = 9.sp,
                        maxLines = 1
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Score: ${sms.score}",
                    color = if (sms.score >= 50) PremiumGold else TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            // Action Buttons at the end
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = { 
                    val debugInfo = """
                        SENDER: ${sms.sender}
                        MESSAGE: ${sms.message}
                        AMOUNT: ${sms.amount ?: "NONE"}
                        FINANCIAL: ${if (sms.isFinancial) "YES" else "NO"}
                        SCORE: ${sms.score}
                        TYPE: ${sms.messageType}
                        
                        QUALIFICATION ANALYSIS:
                        - QUALIFIED: ${if (sms.isQualified) "YES" else "NO"}
                        - CONFIDENCE: ${sms.qualificationConfidence}%
                        - SCORE: ${sms.qualificationScore}
                        - RULES: ${sms.qualificationRules.joinToString(", ")}
                        - EVIDENCE: ${sms.qualificationEvidence.joinToString(", ")}

                        DIRECTION ANALYSIS:
                        - DIRECTION: ${sms.direction},
                        - DIRECTION_CONFIDENCE: ${sms.directionConfidence}
                        - DIRECTION_SCORE: ${sms.directionScore}
                        - DIRECTION_EVIDENCE: ${sms.directionEvidence.joinToString("\n")}
                        
                        MESSAGE TYPE ANALYSIS:
                        - TRANSACTION SCORE: ${sms.transactionScore}
                        - OBLIGATION SCORE: ${sms.obligationScore}
                        - INFORMATION SCORE: ${sms.informationScore}
                        - FINAL DECISION: ${sms.messageType}
                        
                        MERCHANT ANALYSIS:
                        - MERCHANT: ${sms.merchant ?: "NONE"}
                        - CONFIDENCE: ${sms.merchantConfidence}
                        - SCORE: ${sms.merchantScore}
                        - EVIDENCE: ${sms.merchantEvidence.joinToString("\n")}

                        EVENT: ${sms.financialEventType}
                        CATEGORY: ${sms.category ?: "NONE"}
                        MERCHANT: ${sms.merchant ?: "NONE"}
                        MODE: ${sms.transactionMode}
                        ACCOUNT: ${sms.accountName ?: "UNKNOWN"}
                        SIGNALS: ${sms.matchedSignals.joinToString(", ")}
                    """.trimIndent()
                    clipboardManager.setText(AnnotatedString(debugInfo)) 
                }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.ContentCopy, null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                }
                
                IconButton(onClick = onFlagClick, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = if (sms.isFlagged) Icons.Default.Flag else Icons.Outlined.Flag,
                        contentDescription = null,
                        tint = if (sms.isFlagged) Color.Red else TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(onClick = onDoneClick, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = if (sms.isReviewed) Icons.Default.Done else Icons.Outlined.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (sms.isReviewed) PrimaryAccent else TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun Tag(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.18f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text,
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SmsFilterBottomSheet(
    selectedFinancial: Boolean?,
    selectedType: String?,
    selectedEvent: String?,
    onDismiss: () -> Unit,
    onApply: (Boolean?, String?, String?) -> Unit
) {
    var tempFinancial by remember { mutableStateOf(selectedFinancial) }
    var tempType by remember { mutableStateOf(selectedType) }
    var tempEvent by remember { mutableStateOf(selectedEvent) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = BackgroundEnd,
        dragHandle = { BottomSheetDefaults.DragHandle(color = SurfaceGlassBright) }
    ) {
        Column(modifier = Modifier.padding(16.dp).padding(bottom = 32.dp)) {
            Text("Filter Messages", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Domain", color = TextSecondary, fontSize = 12.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
                FilterChip(selected = tempFinancial == null, onClick = { tempFinancial = null }, label = { Text("All") })
                FilterChip(selected = tempFinancial == true, onClick = { tempFinancial = true }, label = { Text("Financial") })
                FilterChip(selected = tempFinancial == false, onClick = { tempFinancial = false }, label = { Text("Non-Fin") })
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Message Type", color = TextSecondary, fontSize = 12.sp)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
                val types = listOf("TRANSACTION", "OBLIGATION", "INFORMATION", "PROMOTIONAL", "UNKNOWN")
                FilterChip(selected = tempType == null, onClick = { tempType = null }, label = { Text("Any") })
                types.forEach { type ->
                    FilterChip(selected = tempType == type, onClick = { tempType = type }, label = { Text(type) })
                }
            }

            if (tempType == "TRANSACTION") {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Financial Event", color = TextSecondary, fontSize = 12.sp)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
                    val events = listOf("EXPENSE", "INCOME", "REFUND", "TRANSFER", "EMI_PAYMENT")
                    FilterChip(selected = tempEvent == null, onClick = { tempEvent = null }, label = { Text("Any") })
                    events.forEach { event ->
                        FilterChip(selected = tempEvent == event, onClick = { tempEvent = event }, label = { Text(event) })
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { onApply(tempFinancial, tempType, tempEvent) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = CyanGlow, contentColor = BackgroundStart)
            ) {
                Text("Apply Filters", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SmsDetailDialog(sms: AnalyzedSMS, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("SMS Analysis", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Column {
                    Text("Sender", color = TextSecondary, fontSize = 11.sp)
                    Text(sms.sender, color = TextPrimary, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Message", color = TextSecondary, fontSize = 11.sp)
                    Text(sms.message, color = TextPrimary, fontSize = 13.sp)
                }
                
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    if (sms.amount != null) {
                        Column {
                            Text("Amount", color = TextSecondary, fontSize = 11.sp)
                            Text("₹${formatIndianCurrency(sms.amount)}", color = TextPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                    Column {
                        Text("Category", color = TextSecondary, fontSize = 11.sp)
                        Text(sms.category ?: "None", color = CyanGlow, fontWeight = FontWeight.Bold)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    if (sms.directionEvidence.isNotEmpty()) {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    SurfaceGlass.copy(alpha = 0.3f),
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(8.dp)
                        ) {
                            Text("Direction Analysis", color = TextSecondary, fontSize = 11.sp)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    "Dir: ${sms.direction}",
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Conf: ${sms.directionConfidence}%",
                                    color = PremiumGold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    "Score: ${sms.directionScore}",
                                    color = PremiumGold,
                                    fontSize = 12.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Evidence:\n" + sms.directionEvidence.joinToString("\n"),
                                color = TextPrimary.copy(alpha = 0.8f),
                                fontSize = 10.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                // Qualification Analysis Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            SurfaceGlass.copy(alpha = 0.3f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                ) {
                    Text("QUALIFICATION ANALYSIS", color = CyanGlow, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Confidence", color = TextSecondary, fontSize = 10.sp)
                            Text("${sms.qualificationConfidence}%", color = PremiumGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Column {
                            Text("Score", color = TextSecondary, fontSize = 10.sp)
                            Text("${sms.qualificationScore}", color = PremiumGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Qualified", color = TextSecondary, fontSize = 10.sp)
                            Text(
                                if (sms.isQualified) "YES" else "NO",
                                color = if (sms.isQualified) PrimaryAccent else ColorTransport,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                    
                    if (sms.qualificationRules.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Executed Rules:", color = TextSecondary, fontSize = 10.sp)
                        sms.qualificationRules.forEach { rule ->
                            Text("✔ $rule", color = TextPrimary.copy(alpha = 0.8f), fontSize = 10.sp)
                        }
                    }

                    if (sms.qualificationEvidence.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Evidence:", color = TextSecondary, fontSize = 10.sp)
                        Text(
                            text = sms.qualificationEvidence.joinToString(", "),
                            color = TextPrimary.copy(alpha = 0.8f),
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                }

                // Message Type Analysis Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            SurfaceGlass.copy(alpha = 0.3f),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(8.dp)
                ) {
                    Text("Message Type Analysis", color = TextSecondary, fontSize = 11.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Transaction", color = TextSecondary, fontSize = 10.sp)
                            Text("${sms.transactionScore}", color = if (sms.transactionScore > 0) CyanGlow else TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Column {
                            Text("Obligation", color = TextSecondary, fontSize = 10.sp)
                            Text("${sms.obligationScore}", color = if (sms.obligationScore > 0) ColorFood else TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Column {
                            Text("Information", color = TextSecondary, fontSize = 10.sp)
                            Text("${sms.informationScore}", color = if (sms.informationScore > 0) SecondaryAccent else TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row {
                        Text("Current Type: ", color = TextSecondary, fontSize = 11.sp)
                        Text(sms.messageType, color = PremiumGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }

                // Merchant Analysis Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            SurfaceGlass.copy(alpha = 0.3f),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(8.dp)
                ) {
                    Text("Merchant Analysis", color = TextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Merchant: ${sms.merchant ?: "None"}",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Column {
                            Text("Confidence", color = TextSecondary, fontSize = 10.sp)
                            Text("${sms.merchantConfidence}", color = PremiumGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Column {
                            Text("Score", color = TextSecondary, fontSize = 10.sp)
                            Text("${sms.merchantScore}", color = PremiumGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                    if (sms.merchantEvidence.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Evidence:", color = TextSecondary, fontSize = 10.sp)
                        Text(
                            text = sms.merchantEvidence.joinToString("\n"),
                            color = TextPrimary.copy(alpha = 0.8f),
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    Column {
                        Text("Type", color = TextSecondary, fontSize = 11.sp)
                        Text(sms.messageType, color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Event", color = TextSecondary, fontSize = 11.sp)
                        Text(sms.financialEventType, color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    Column {
                        Text("Merchant", color = TextSecondary, fontSize = 11.sp)
                        Text(sms.merchant ?: "None", color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Mode", color = TextSecondary, fontSize = 11.sp)
                        Text(sms.transactionMode, color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                if (!sms.accountName.isNullOrBlank()) {
                    Column {
                        Text("Account/Card", color = TextSecondary, fontSize = 11.sp)
                        Text(ExpenseDisplayUtils.getVesselDisplay(sms.accountName), color = SecondaryAccent, fontWeight = FontWeight.Bold)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    Column {
                        Text("Financial", color = TextSecondary, fontSize = 11.sp)
                        Text(if (sms.isFinancial) "Yes" else "No", color = if (sms.isFinancial) PrimaryAccent else ColorTransport, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Score", color = TextSecondary, fontSize = 11.sp)
                        Text("${sms.score}", color = PremiumGold, fontWeight = FontWeight.Bold)
                    }
                }

                if (sms.matchedSignals.isNotEmpty()) {
                    Column {
                        Text("Signals", color = TextSecondary, fontSize = 11.sp)
                        Text(sms.matchedSignals.joinToString(", "), color = TextPrimary, fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = CyanGlow)
            }
        },
        containerColor = BackgroundEnd
    )
}


private fun getDirectionColor(
    direction: TransactionDirection
): Color {
    return when (direction) {
        TransactionDirection.DEBIT -> ColorTransport
        TransactionDirection.CREDIT -> ColorGroceries
        TransactionDirection.UNKNOWN -> TextSecondary
    }
}
