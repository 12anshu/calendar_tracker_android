package com.example.smartexpensecalendar.ui

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smartexpensecalendar.domain.model.DefaultCategories
import com.example.smartexpensecalendar.domain.model.Expense
import com.example.smartexpensecalendar.domain.model.TransactionType
import com.example.smartexpensecalendar.presentation.transactions.TransactionsViewModel
import com.example.smartexpensecalendar.ui.components.CategoryIconView
import com.example.smartexpensecalendar.ui.theme.*
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
                            DropdownMenu(
                                expanded = showMonthPicker,
                                onDismissRequest = { showMonthPicker = false },
                                modifier = Modifier.background(SurfaceGlass)
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
                    stickyHeader {
                        TransactionDateHeader(date, uiState.transactions[date]?.sumOf { if (it.type == TransactionType.DEBIT) it.amount else 0.0 } ?: 0.0)
                    }

                    items(uiState.transactions[date] ?: emptyList()) { expense ->
                        TransactionItem(
                            expense = expense,
                            categories = uiState.categories,
                            isEditing = editingExpenseId == expense.id,
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
                            onAddCustomCategory = { showAddCategoryDialog = true }
                        )
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
fun TransactionDateHeader(date: LocalDate, totalDebit: Double) {
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
                text = "₹${formatIndianCurrency(totalDebit)}",
                style = MaterialTheme.typography.labelLarge,
                color = TextSecondary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TransactionItem(
    expense: Expense,
    categories: List<String>,
    isEditing: Boolean,
    onEditToggle: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onEdit: (Double, String, Boolean) -> Unit,
    onClick: () -> Unit,
    onAddCustomCategory: () -> Unit
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
                if (isEditing) CyanGlow.copy(alpha = 0.5f) else Color.Transparent, 
                RoundedCornerShape(12.dp)
            )
    ) {
        if (isEditing) {
            Column(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1.2f)) {
                        OutlinedButton(
                            onClick = { expanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(SurfaceGlassBright))
                        ) {
                            Text(editCategory, fontSize = 12.sp, maxLines = 1)
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(BackgroundEnd)
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { 
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            CategoryIconView(category = cat, size = 24.dp, iconSize = 14.dp)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(cat, color = TextPrimary) 
                                        }
                                    },
                                    onClick = { editCategory = cat; expanded = false }
                                )
                            }
                            HorizontalDivider(color = SurfaceGlassBright)
                            DropdownMenuItem(
                                text = { Text("+ Add Custom", color = CyanGlow) },
                                onClick = {
                                    expanded = false
                                    onAddCustomCategory()
                                }
                            )
                        }
                    }
                    OutlinedTextField(
                        value = editAmount,
                        onValueChange = { editAmount = it },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanGlow,
                            unfocusedBorderColor = SurfaceGlassBright
                        )
                    )
                }

                if (expense.merchant != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp).clickable { applyToFuture = !applyToFuture }
                    ) {
                        Checkbox(
                            checked = applyToFuture,
                            onCheckedChange = { applyToFuture = it },
                            colors = CheckboxDefaults.colors(checkedColor = CyanGlow)
                        )
                        Text(
                            text = "Apply to all future ${expense.merchant} transactions",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { onEditToggle(false) }) { Text("Cancel", color = TextSecondary) }
                    TextButton(
                        onClick = {
                            val amt = editAmount.toDoubleOrNull()
                            if (amt != null) {
                                onEdit(amt, editCategory, applyToFuture)
                            }
                        }
                    ) { Text("Save", color = CyanGlow) }
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() }
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    // Unified Category Icon
                    CategoryIconView(category = expense.category, size = 42.dp, iconSize = 20.dp)

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = expense.merchant ?: "Transaction",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1
                        )
                        Text(
                            text = expense.category,
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${if (expense.type == TransactionType.DEBIT) "-" else "+"}₹${formatIndianCurrency(expense.amount)}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (expense.type == TransactionType.DEBIT) TextPrimary else ColorGroceries
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (expense.accountSuffix != null) {
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
