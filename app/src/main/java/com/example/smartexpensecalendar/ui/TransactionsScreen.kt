package com.example.smartexpensecalendar.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.SwapHorizontalCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smartexpensecalendar.domain.model.DefaultCategories
import com.example.smartexpensecalendar.domain.model.Expense
import com.example.smartexpensecalendar.domain.model.TransactionStatus
import com.example.smartexpensecalendar.domain.model.TransactionType
import com.example.smartexpensecalendar.presentation.transactions.TransactionsViewModel
import com.example.smartexpensecalendar.ui.components.CategoryIconView
import com.example.smartexpensecalendar.ui.components.MonthYearPicker
import com.example.smartexpensecalendar.ui.components.CategoryGridPicker
import com.example.smartexpensecalendar.ui.components.FintechBottomNav
import com.example.smartexpensecalendar.core.designsystem.theme.*
import com.example.smartexpensecalendar.utils.CurrencyUtils.formatIndianCurrency
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalLayoutApi::class
)
@Composable
fun TransactionsScreen(
    navController: NavController,
    viewModel: TransactionsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showMonthPicker by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var isSearchExpanded by remember { mutableStateOf(false) }
    var searchQueryLocal by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    var selectedSmsForDetail by remember { mutableStateOf<String?>(null) }
    var editingExpenseId by remember { mutableStateOf<Long?>(null) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }

    // Sync local search query with ViewModel state (e.g., when search is cleared externally)
    LaunchedEffect(uiState.searchQuery) {
        if (uiState.searchQuery != searchQueryLocal) {
            searchQueryLocal = uiState.searchQuery
        }
    }

    if (showAddCategoryDialog) {
        var newCategoryName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("Add Custom Category", color = TextPrimary) },
            text = {
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    label = { Text("Category Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanGlow,
                        unfocusedBorderColor = SurfaceGlassBright
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newCategoryName.isNotBlank()) {
                            viewModel.addCustomCategory(newCategoryName.trim())
                            showAddCategoryDialog = false
                        }
                    }
                ) {
                    Text("Add", color = CyanGlow)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) {
                    Text("Close", color = TextSecondary)
                }
            },
            containerColor = BackgroundEnd
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isSearchExpanded) {
                        TextField(
                            value = searchQueryLocal,
                            onValueChange = { 
                                searchQueryLocal = it
                                viewModel.setSearchQuery(it) 
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester),
                            placeholder = { Text("Search transactions...", color = TextSecondary) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                cursorColor = CyanGlow,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = TextPrimary
                            ),
                            singleLine = true
                        )
                        LaunchedEffect(Unit) {
                            focusRequester.requestFocus()
                        }
                    } else {
                        Box {
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
                        }
                    }
                },
                actions = {
                    if (isSearchExpanded) {
                        IconButton(onClick = { 
                            isSearchExpanded = false
                            searchQueryLocal = ""
                            viewModel.setSearchQuery("")
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Close Search", tint = TextPrimary)
                        }
                    } else {
                        IconButton(onClick = { isSearchExpanded = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = TextPrimary)
                        }
                    }
                    IconButton(onClick = { showFilterSheet = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = if (uiState.selectedCategory != null || uiState.selectedType != null) CyanGlow else TextPrimary)
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
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                if (uiState.transactions.isEmpty()) {
                    item {
                        Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No transactions found", color = TextSecondary)
                        }
                    }
                }

                uiState.transactions.keys.sortedDescending().forEach { date ->
                    val dailyDebit = uiState.transactions[date]?.sumOf { 
                        if (it.type == TransactionType.DEBIT && it.status == TransactionStatus.COMPLETED) it.amount else 0.0 
                    } ?: 0.0

                    stickyHeader {
                        TransactionDateHeader(date, dailyDebit, uiState.currencySymbol)
                    }

                    // Pre-process items to identify movements
                    val dayItems = uiState.transactions[date] ?: emptyList()
                    val allMonthItems = uiState.transactions.values.flatten()
                    val handledIds = mutableSetOf<Long>()

                    dayItems.forEach { expense ->
                        if (expense.id in handledIds) return@forEach

                        if (expense.status == TransactionStatus.SETTLEMENT && expense.linkedId != null) {
                            // Global partner search: Find partner anywhere in the month
                            val partner = allMonthItems.find { it.id == expense.linkedId }
                            if (partner != null) {
                                // Rule: Only render the Movement Card on the date of the DEBIT 
                                // to avoid double rendering on both dates.
                                val debit = if (expense.type == TransactionType.DEBIT) expense else partner
                                val credit = if (expense.type == TransactionType.CREDIT) expense else partner
                                
                                if (debit.date == date) {
                                    item(key = "move_${debit.id}") {
                                        MovementTransactionItem(
                                            debit = debit,
                                            credit = credit,
                                            currencySymbol = uiState.currencySymbol,
                                            onClick = { 
                                                selectedSmsForDetail = "DEBIT: ${debit.originalSmsBody ?: "N/A"}\n\nCREDIT: ${credit.originalSmsBody ?: "N/A"}"
                                            }
                                        )
                                    }
                                }
                                handledIds.add(expense.id)
                                // If the partner was also on this same day, mark it handled
                                if (partner.date == date) handledIds.add(partner.id)
                                return@forEach
                            }
                        }

                        item(key = expense.id) {
                            TransactionItem(
                                expense = expense,
                                categories = uiState.categories,
                                isEditing = editingExpenseId == expense.id,
                                currencySymbol = uiState.currencySymbol,
                                onEditToggle = { isEditing ->
                                    editingExpenseId = if (isEditing) expense.id else null
                                },
                                onDelete = {
                                    viewModel.deleteExpense(expense)
                                },
                                onEdit = { amount, category, applyToFuture ->
                                    viewModel.updateExpense(expense, amount, category, applyToFuture)
                                    editingExpenseId = null
                                },
                                onClick = { 
                                    selectedSmsForDetail = expense.originalSmsBody ?: "Manual transaction - No SMS available"
                                },
                                onAddCustomCategory = { showAddCategoryDialog = true },
                                onConfirmReview = {
                                    viewModel.updateExpenseStatus(expense.id, TransactionStatus.COMPLETED)
                                }
                            )
                        }
                        handledIds.add(expense.id)
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            selectedCategory = uiState.selectedCategory,
            selectedType = uiState.selectedType,
            categories = uiState.categories,
            onDismiss = { showFilterSheet = false },
            onApply = { cat, type ->
                viewModel.setCategory(cat)
                viewModel.setType(type)
                showFilterSheet = false
            }
        )
    }

    if (selectedSmsForDetail != null) {
        AlertDialog(
            onDismissRequest = { selectedSmsForDetail = null },
            title = { Text("Original SMS", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { 
                Text(
                    text = selectedSmsForDetail!!,
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                ) 
            },
            confirmButton = {
                TextButton(onClick = { selectedSmsForDetail = null }) {
                    Text("Close", color = CyanGlow)
                }
            },
            containerColor = BackgroundEnd
        )
    }
}

@Composable
fun TransactionDateHeader(date: LocalDate, totalDebit: Double, currencySymbol: String = "₹") {
    val formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy")
    val isToday = date == LocalDate.now()
    val isYesterday = date == LocalDate.now().minusDays(1)
    
    val dateText = when {
        isToday -> "Today"
        isYesterday -> "Yesterday"
        else -> date.format(formatter)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundStart)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = dateText,
            style = MaterialTheme.typography.labelLarge,
            color = TextSecondary,
            fontWeight = FontWeight.Bold
        )
        if (totalDebit > 0) {
            Text(
                text = "$currencySymbol${formatIndianCurrency(totalDebit)}",
                style = MaterialTheme.typography.labelLarge,
                color = TextSecondary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MovementTransactionItem(
    debit: Expense,
    credit: Expense,
    currencySymbol: String = "₹",
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceGlass.copy(alpha = 0.2f)) // Reduced background intensity
            .border(
                1.dp, 
                SecondaryAccent.copy(alpha = 0.12f), // Subtle border instead of bright background
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                // Shared Movement Icon with softer background
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SecondaryAccent.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHorizontalCircle,
                        contentDescription = "Movement",
                        tint = SecondaryAccent.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${debit.accountName ?: "SOURCE"} → ${credit.accountName ?: "TARGET"}".uppercase(),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary.copy(alpha = 0.9f),
                            maxLines = 1,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = SecondaryAccent.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, SecondaryAccent.copy(alpha = 0.15f))
                        ) {
                            Text(
                                "SETTLED",
                                color = SecondaryAccent.copy(alpha = 0.6f),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "Internal Movement • ${debit.category}",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary.copy(alpha = 0.7f)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$currencySymbol${formatIndianCurrency(debit.amount)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary.copy(alpha = 0.6f)
                )
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Settled",
                    tint = SecondaryAccent.copy(alpha = 0.4f),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun TransactionItem(
    expense: Expense,
    categories: List<String>,
    isEditing: Boolean,
    currencySymbol: String = "₹",
    onEditToggle: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onEdit: (Double, String, Boolean) -> Unit,
    onClick: () -> Unit,
    onAddCustomCategory: () -> Unit,
    onConfirmReview: () -> Unit
) {
    val categoryColor = getCategoryColor(expense.category)
    var editAmount by remember(isEditing) { mutableStateOf(expense.amount.toString()) }
    var editCategory by remember(isEditing) { mutableStateOf(expense.category) }
    var applyToFuture by remember(isEditing) { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceGlass)
            .border(
                1.dp, 
                if (isEditing) CyanGlow.copy(alpha = 0.5f) else if (expense.status == TransactionStatus.PENDING_REVIEW) PremiumGold.copy(alpha = 0.3f) else Color.Transparent, 
                RoundedCornerShape(12.dp)
            )
    ) {
        if (isEditing) {
            // ... [Same editing code]
        } else {
            Column {
                if (expense.status == TransactionStatus.PENDING_REVIEW) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PremiumGold.copy(alpha = 0.1f))
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Uncertain Transaction - Please verify",
                            color = PremiumGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(
                            onClick = onConfirmReview,
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.height(20.dp)
                        ) {
                            Text("Confirm", color = CyanGlow, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onClick() }
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        CategoryIconView(category = expense.category, size = 42.dp, iconSize = 20.dp)

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val displayName = when {
                                    !expense.merchant.isNullOrBlank() -> expense.merchant
                                    !expense.accountName.isNullOrBlank() -> expense.accountName
                                    expense.financialEventType == com.example.smartexpensecalendar.domain.model.FinancialEventType.TRANSFER -> "Account Transfer"
                                    expense.financialEventType == com.example.smartexpensecalendar.domain.model.FinancialEventType.EMI_PAYMENT -> "EMI Payment"
                                    expense.financialEventType == com.example.smartexpensecalendar.domain.model.FinancialEventType.EMI_CONVERSION -> "EMI Conversion"
                                    expense.financialEventType == com.example.smartexpensecalendar.domain.model.FinancialEventType.CASH_WITHDRAWAL -> "Cash Withdrawal"
                                    expense.financialEventType == com.example.smartexpensecalendar.domain.model.FinancialEventType.CREDIT_CARD_PAYMENT -> "Card Payment"
                                    expense.financialEventType == com.example.smartexpensecalendar.domain.model.FinancialEventType.INVESTMENT -> "Investment"
                                    expense.financialEventType == com.example.smartexpensecalendar.domain.model.FinancialEventType.REFUND -> "Refund"
                                    expense.financialEventType == com.example.smartexpensecalendar.domain.model.FinancialEventType.SALARY -> "Salary"
                                    expense.financialEventType == com.example.smartexpensecalendar.domain.model.FinancialEventType.CASHBACK -> "Cashback"
                                    expense.type == TransactionType.DEBIT -> "Payment"
                                    else -> "Received"
                                }

                                Text(
                                    text = displayName.uppercase(),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                
                                if (expense.status == TransactionStatus.SETTLEMENT) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = SecondaryAccent.copy(alpha = 0.08f),
                                        shape = RoundedCornerShape(4.dp),
                                        border = BorderStroke(1.dp, SecondaryAccent.copy(alpha = 0.15f))
                                    ) {
                                        Text(
                                            "SETTLED",
                                            color = SecondaryAccent.copy(alpha = 0.6f),
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                
                                if (expense.status == TransactionStatus.REFUNDED) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = ColorGroceries.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            "REFUNDED",
                                            color = ColorGroceries,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            
                            val subtitle = buildAnnotatedString {
                                withStyle(SpanStyle(color = TextSecondary)) {
                                    append(expense.category)
                                }
                                if (!expense.accountName.isNullOrBlank()) {
                                    withStyle(SpanStyle(color = TextSecondary.copy(alpha = 0.5f))) {
                                        append("  •  ")
                                    }
                                    withStyle(SpanStyle(
                                        color = SecondaryAccent.copy(alpha = 0.8f),
                                        fontWeight = FontWeight.Medium
                                    )) {
                                        append(expense.accountName.uppercase())
                                    }
                                }
                            }

                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${if (expense.type == TransactionType.DEBIT) "-" else "+"} $currencySymbol${formatIndianCurrency(expense.amount)}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (expense.status == TransactionStatus.SETTLEMENT) TextSecondary 
                                    else if (expense.type == TransactionType.DEBIT) TextPrimary 
                                    else ColorGroceries
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Only show suffix if full accountName is missing
                            if (expense.accountSuffix != null && expense.accountName.isNullOrBlank()) {
                                Text(
                                    text = "XX${expense.accountSuffix}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            IconButton(onClick = { onEditToggle(true) }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TextSecondary, modifier = Modifier.size(16.dp))
                            }
                            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ColorTransport, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalLayoutApi::class
)
@Composable
fun FilterBottomSheet(
    selectedCategory: String?,
    selectedType: TransactionType?,
    categories: List<String>,
    onDismiss: () -> Unit,
    onApply: (String?, TransactionType?) -> Unit
) {
    var tempCategory by remember { mutableStateOf(selectedCategory) }
    var tempType by remember { mutableStateOf(selectedType) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = BackgroundEnd,
        dragHandle = { BottomSheetDefaults.DragHandle(color = SurfaceGlassBright) }
    ) {
        Column(modifier = Modifier.padding(16.dp).padding(bottom = 32.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Filter Transactions", 
                    style = MaterialTheme.typography.headlineSmall, 
                    fontWeight = FontWeight.Bold, 
                    color = TextPrimary
                )
                
                if (tempCategory != null || tempType != null) {
                    TextButton(onClick = {
                        tempCategory = null
                        tempType = null
                        onApply(null, null)
                    }) {
                        Text("Clear All", color = ColorTransport, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Type", style = MaterialTheme.typography.labelLarge, color = TextSecondary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
                FilterChip(
                    selected = tempType == null,
                    onClick = { tempType = null },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = tempType == TransactionType.DEBIT,
                    onClick = { tempType = TransactionType.DEBIT },
                    label = { Text("Debit") }
                )
                FilterChip(
                    selected = tempType == TransactionType.CREDIT,
                    onClick = { tempType = TransactionType.CREDIT },
                    label = { Text("Credit") }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Category", style = MaterialTheme.typography.labelLarge, color = TextSecondary)
            FlowRow(
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = tempCategory == null,
                    onClick = { tempCategory = null },
                    label = { Text("All Categories") }
                )
                categories.forEach { category ->
                    FilterChip(
                        selected = tempCategory == category,
                        onClick = { tempCategory = category },
                        label = { 
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CategoryIconView(category = category, size = 18.dp, iconSize = 10.dp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(category) 
                            }
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { onApply(tempCategory, tempType) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = CyanGlow, contentColor = BackgroundStart),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Apply Filters", fontWeight = FontWeight.Bold)
            }
        }
    }
}
