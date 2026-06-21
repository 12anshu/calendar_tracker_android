package com.example.smartexpensecalendar.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smartexpensecalendar.core.designsystem.theme.*
import com.example.smartexpensecalendar.domain.model.Expense
import com.example.smartexpensecalendar.domain.model.TransactionType
import com.example.smartexpensecalendar.presentation.transactions.MerchantInsights
import com.example.smartexpensecalendar.presentation.transactions.TransactionDetailsViewModel
import com.example.smartexpensecalendar.ui.components.CategoryGridPicker
import com.example.smartexpensecalendar.ui.components.CategoryIconView
import com.example.smartexpensecalendar.utils.CurrencyUtils.formatIndianCurrency
import com.example.smartexpensecalendar.utils.ExpenseDisplayUtils
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TransactionDetailsScreen(
    navController: NavController,
    viewModel: TransactionDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transaction Details", color = TextPrimary, fontWeight = FontWeight.Bold) },
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
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CyanGlow)
            }
        } else if (uiState.expense == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Transaction not found", color = TextSecondary)
            }
        } else {
            val expense = uiState.expense!!
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SummaryCard(expense, uiState.merchantInsights)
                
//                // --- TRANSACTION INFORMATION ---
//                Card(
//                    modifier = Modifier.fillMaxWidth(),
//                    shape = RoundedCornerShape(18.dp),
//                    colors = CardDefaults.cardColors(containerColor = SurfaceGlass)
//                ) {
//                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
//                        Text("Transaction Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
//                        HorizontalDivider(color = SurfaceGlassBright.copy(alpha = 0.5f))
//
//                        if (!expense.merchant.isNullOrBlank()) {
//                            LabelValueRow("Merchant", expense.merchant)
//                        }
//                        LabelValueRow("Category", expense.category)
//                        LabelValueRow("Account", ExpenseDisplayUtils.getVesselDisplay(expense.accountName))
//                        LabelValueRow("Status", expense.status.name.lowercase().replaceFirstChar { it.uppercase() })
//                    }
//                }

                if (uiState.merchantInsights != null) {
                    MerchantInsightsCard(uiState.merchantInsights!!)
                }
                
                OriginalSmsCard(expense.originalSmsBody, expense.senderId)
                IdentifiedCard(expense)
                EditTransactionSection(
                    expense = expense,
                    categories = uiState.categories,
                    isEditMode = uiState.isEditMode,
                    onToggleEdit = { viewModel.toggleEditMode() },
                    onUpdate = { category, applyToFuture ->
                        viewModel.updateExpenseCategory(category, applyToFuture)
                    }
                )
                ReportIssueCard(expense) { issueType, details ->
                    viewModel.submitReport(issueType, details)
                    sendIssueEmail(context, expense, issueType, details)
                }
                DeleteTransactionSection {
                    viewModel.deleteExpense {
                        navController.popBackStack()
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun SummaryCard(expense: Expense, insights: MerchantInsights?) {
    val gradient = Brush.verticalGradient(listOf(Color(0xFF171F35), Color(0xFF1D2640)))
    val isDebit = expense.type == TransactionType.DEBIT
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(gradient)
            .padding(24.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.fillMaxWidth()) {
                if (insights != null) {
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = CircleShape,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Text(
                            text = "${insights.totalTransactions} Transactions",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                            color = TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    CategoryIconView(category = expense.category, size = 64.dp, iconSize = 32.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = ExpenseDisplayUtils.getDisplayName(expense),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "${expense.category.uppercase()}  •  ${ExpenseDisplayUtils.getVesselDisplay(expense.accountName)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "${if (isDebit) "-" else "+"} ₹${formatIndianCurrency(expense.amount)}",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                color = if (isDebit) Color(0xFFF87171) else ColorGroceries
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            val time = expense.transactionTime ?: expense.createdAt
            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy • hh:mm a")
            val dateTimeStr = Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).format(formatter)
            
            Text(
                text = dateTimeStr,
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun DetailsCard(expense: Expense) {
    // Removed as per request. Content moved to Summary and OriginalSms cards.
}

@Composable
fun MerchantInsightsCard(insights: MerchantInsights) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceGlass)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Merchant Insights", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
            HorizontalDivider(color = SurfaceGlassBright.copy(alpha = 0.5f))
            LabelValueRow("Total Transactions", insights.totalTransactions.toString())
            LabelValueRow("Total Spend", "₹${formatIndianCurrency(insights.totalSpend)}")
            LabelValueRow("Average Spend", "₹${formatIndianCurrency(insights.averageSpend)}")
            
            val lastDateStr = insights.lastTransactionDate?.let { date ->
                val days = ChronoUnit.DAYS.between(date, LocalDate.now())
                when {
                    days == 0L -> "Today"
                    days == 1L -> "Yesterday"
                    days < 7L -> "$days days ago"
                    else -> date.format(DateTimeFormatter.ofPattern("dd MMM"))
                }
            } ?: "N/A"
            LabelValueRow("Last Transaction", lastDateStr)
        }
    }
}

@Composable
fun OriginalSmsCard(smsBody: String?, senderId: String?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceGlass)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Original SMS", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                if (senderId != null) {
                    Surface(
                        color = Color.White.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = senderId,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.2f))
                    .padding(12.dp)
            ) {
                SelectionContainer {
                    Text(
                        text = smsBody ?: "Original SMS not available",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            lineHeight = 20.sp
                        ),
                        color = if (smsBody != null) TextPrimary else TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun IdentifiedCard(expense: Expense) {
    var isExpanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceGlass)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("How was this identified?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                Icon(if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextSecondary)
            }
            
            AnimatedVisibility(visible = isExpanded, enter = expandVertically(), exit = shrinkVertically()) {
                Column(modifier = Modifier.padding(top = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    HorizontalDivider(color = SurfaceGlassBright.copy(alpha = 0.5f))
                    LabelValueRow("Merchant", expense.merchant ?: "N/A")
                    LabelValueRow("Payment Method", expense.paymentMethod.name)
                    LabelValueRow("Financial Event", expense.financialEventType.name)
                    LabelValueRow("Sender", expense.senderId ?: "N/A")
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Confidence", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                        val badgeColor = when {
                            expense.confidence >= 90 -> ColorGroceries
                            expense.confidence >= 70 -> CyanGlow
                            else -> PremiumGold
                        }
                        Surface(
                            color = badgeColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "${expense.confidence}%",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = badgeColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditTransactionSection(
    expense: Expense,
    categories: List<String>,
    isEditMode: Boolean,
    onToggleEdit: () -> Unit,
    onUpdate: (String, Boolean) -> Unit
) {
    var editCategory by remember(isEditMode) { mutableStateOf(expense.category) }
    var applyToFuture by remember(isEditMode) { mutableStateOf(false) }
    var showCategoryPicker by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (!isEditMode) {
            Button(
                onClick = onToggleEdit,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyanGlow, contentColor = BackgroundStart),
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Transaction", fontWeight = FontWeight.Bold)
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceGlass),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyanGlow.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Edit Transaction", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                    
                    Box {
                        OutlinedButton(
                            onClick = { showCategoryPicker = true },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceGlassBright)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(editCategory, color = TextPrimary)
                                Icon(Icons.Default.ArrowDropDown, null, tint = TextSecondary)
                            }
                        }
                        if (showCategoryPicker) {
                            CategoryGridPicker(
                                categories = categories,
                                selectedCategory = editCategory,
                                onDismiss = { showCategoryPicker = false },
                                onSelect = {
                                    editCategory = it
                                    showCategoryPicker = false
                                }
                            )
                        }
                    }

                    if (!expense.merchant.isNullOrBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { applyToFuture = !applyToFuture }) {
                            Checkbox(checked = applyToFuture, onCheckedChange = { applyToFuture = it }, colors = CheckboxDefaults.colors(checkedColor = CyanGlow))
                            Text("Apply this category to future transactions from the same merchant", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        TextButton(onClick = onToggleEdit, modifier = Modifier.weight(1f)) {
                            Text("Cancel", color = TextSecondary)
                        }
                        Button(
                            onClick = { onUpdate(editCategory, applyToFuture) },
                            modifier = Modifier.weight(1f).height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyanGlow, contentColor = BackgroundStart),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Save Changes", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReportIssueCard(expense: Expense, onSubmit: (String, String) -> Unit) {
    val issueTypes = listOf("Wrong Merchant", "Wrong Category", "Wrong Amount", "Duplicate Transaction", "Missing Account", "Other")
    var selectedIssue by remember { mutableStateOf<String?>(null) }
    var details by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceGlass)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Report Issue", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                Icon(if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextSecondary)
            }
            
            AnimatedVisibility(visible = isExpanded, enter = expandVertically(), exit = shrinkVertically()) {
                Column(modifier = Modifier.padding(top = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("What's wrong with this transaction?", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                    
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        issueTypes.forEach { issue ->
                            FilterChip(
                                selected = selectedIssue == issue,
                                onClick = { selectedIssue = issue },
                                label = { Text(issue, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CyanGlow, selectedLabelColor = BackgroundStart)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = details,
                        onValueChange = { details = it },
                        label = { Text("Additional Details") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanGlow, unfocusedBorderColor = SurfaceGlassBright)
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { isExpanded = false }) { Text("Cancel", color = TextSecondary) }
                        Button(
                            onClick = { selectedIssue?.let { onSubmit(it, details) } },
                            enabled = selectedIssue != null,
                            colors = ButtonDefaults.buttonColors(containerColor = CyanGlow, contentColor = BackgroundStart),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Submit Report", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteTransactionSection(onDelete: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = BackgroundEnd,
            title = { Text("Delete Transaction?", color = TextPrimary) },
            text = { Text("This action cannot be undone.", color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = { 
                    onDelete()
                    showDialog = false
                }) {
                    Text("Delete", color = ColorTransport, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel", color = TextPrimary)
                }
            }
        )
    }

    OutlinedButton(
        onClick = { showDialog = true },
        modifier = Modifier.fillMaxWidth().height(56.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ColorTransport.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = ColorTransport)
    ) {
        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Delete Transaction", fontWeight = FontWeight.Bold)
    }
}

@Composable
fun LabelValueRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
        Text(value, color = TextPrimary, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

fun sendIssueEmail(context: android.content.Context, expense: Expense, issueType: String, details: String) {
    val emailBody = """
        Transaction ID: ${expense.id}
        Merchant: ${expense.merchant ?: "N/A"}
        Amount: ₹${expense.amount}
        Category: ${expense.category}
        Sender: ${expense.senderId ?: "N/A"}
        Issue Type: $issueType
        
        User Comments:
        $details
        
        System Info:
        Confidence: ${expense.confidence}%
        Source: ${expense.source.name}
        Status: ${expense.status}
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_SUBJECT, "SMART Expense Tracker - Transaction Issue")
        putExtra(Intent.EXTRA_TEXT, emailBody)
    }
    context.startActivity(Intent.createChooser(intent, "Send Issue Report"))
}
