package com.example.smartexpensecalendar.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartexpensecalendar.domain.model.DefaultCategories
import com.example.smartexpensecalendar.domain.model.Expense
import com.example.smartexpensecalendar.presentation.detail.ExpenseDetailViewModel
import com.example.smartexpensecalendar.ui.components.CategoryIconView
import com.example.smartexpensecalendar.ui.theme.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDetailBottomSheet(
    date: LocalDate,
    onDismiss: () -> Unit,
    viewModel: ExpenseDetailViewModel = hiltViewModel()
) {
    val expensesRaw by viewModel.getExpensesForDate(date).collectAsState(initial = emptyList())
    val categories by viewModel.categories.collectAsState()
    val currencySymbol by viewModel.currencySymbol.collectAsState()
    val expenses = expensesRaw.filter { 
        it.type == com.example.smartexpensecalendar.domain.model.TransactionType.DEBIT &&
        it.status == com.example.smartexpensecalendar.domain.model.TransactionStatus.COMPLETED
    }
    
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var showAddSection by remember { mutableStateOf(false) }
    var editingExpenseId by remember { mutableStateOf<Long?>(null) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }

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
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = BackgroundEnd
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = Modifier.fillMaxHeight(0.8f),
        containerColor = BackgroundEnd,
        dragHandle = { BottomSheetDefaults.DragHandle(color = SurfaceGlassBright) }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 4.dp)
                    .fillMaxSize()
            ) {
                // Header Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = date.format(DateTimeFormatter.ofPattern("EEEE, dd MMM")),
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )
                        Text(
                            text = "Today's Transactions",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "$currencySymbol${"%,.0f".format(expenses.sumOf { it.amount })}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = CyanGlow
                        )
                    }
                    
                    IconButton(
                        onClick = { showAddSection = !showAddSection },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (showAddSection) ColorTransport.copy(alpha = 0.1f) else SurfaceGlass)
                            .border(1.dp, SurfaceGlassBright, RoundedCornerShape(12.dp))
                    ) {
                        Icon(
                            imageVector = if (showAddSection) Icons.Default.Close else Icons.Default.Add,
                            contentDescription = "Toggle Add",
                            tint = if (showAddSection) ColorTransport else CyanGlow
                        )
                    }
                }

                AnimatedVisibility(
                    visible = showAddSection,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(20.dp))
                        AddExpenseSection(
                            categories = categories,
                            onAdd = { amount, category ->
                                viewModel.addExpense(amount, category, date)
                                showAddSection = false
                            },
                            onAddCustomCategory = { showAddCategoryDialog = true }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "History",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    if (expenses.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No expenses recorded for this day",
                                    color = TextSecondary.copy(alpha = 0.5f),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                    
                    items(expenses) { expense ->
                        ExpenseRow(
                            expense = expense,
                            categories = categories,
                            isEditing = editingExpenseId == expense.id,
                            currencySymbol = currencySymbol,
                            onEditToggle = { isEditing ->
                                editingExpenseId = if (isEditing) expense.id else null
                                if (isEditing) showAddSection = false
                            },
                            onDelete = {
                                scope.launch {
                                    viewModel.deleteExpense(expense)
                                    val result = snackbarHostState.showSnackbar(
                                        message = "Expense deleted",
                                        actionLabel = "Undo",
                                        duration = SnackbarDuration.Short
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        viewModel.addExpense(expense.amount, expense.category, expense.date)
                                    }
                                }
                            },
                            onEdit = { amount, category, applyToFuture ->
                                viewModel.updateExpense(expense, amount, category, applyToFuture)
                                editingExpenseId = null
                            },
                            onAddCustomCategory = { showAddCategoryDialog = true }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseRow(
    expense: Expense,
    categories: List<String>,
    isEditing: Boolean,
    currencySymbol: String = "₹",
    onEditToggle: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onEdit: (Double, String, Boolean) -> Unit,
    onAddCustomCategory: () -> Unit
) {
    var editAmount by remember(isEditing) { mutableStateOf(expense.amount.toString()) }
    var editCategory by remember(isEditing) { mutableStateOf(expense.category) }
    var applyToFuture by remember(isEditing) { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    val categoryColor = getCategoryColor(expense.category)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceGlass)
            .border(1.dp, SurfaceGlassBright, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        if (isEditing) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedButton(
                            onClick = { expanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(SurfaceGlassBright))
                        ) {
                            Text(editCategory)
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
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Unified Category Icon
                    CategoryIconView(category = expense.category)

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = expense.merchant ?: "Transaction",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = expense.category,
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "$currencySymbol${"%,.0f".format(expense.amount)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                        Row {
                            IconButton(onClick = { onEditToggle(true) }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                            }
                            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = ColorTransport, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddExpenseSection(
    categories: List<String>,
    onAdd: (Double, String) -> Unit,
    onAddCustomCategory: () -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(categories) {
        if (category.isEmpty() && categories.isNotEmpty()) {
            category = categories.first()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(CyanGlow.copy(alpha = 0.1f), Color.Transparent)
                )
            )
            .border(1.dp, CyanGlow.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Add Manually",
            style = MaterialTheme.typography.labelSmall,
            color = CyanGlow,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(modifier = Modifier.weight(1.2f)) {
                Button(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceGlass),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text(category, color = TextPrimary, fontSize = 12.sp)
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
                            onClick = { category = cat; expanded = false }
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
                value = amount,
                onValueChange = { amount = it },
                modifier = Modifier.weight(1f).height(52.dp),
                placeholder = { Text("0.00", color = TextSecondary, fontSize = 14.sp) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanGlow,
                    unfocusedBorderColor = SurfaceGlassBright,
                    focusedContainerColor = SurfaceGlass
                ),
                shape = RoundedCornerShape(8.dp)
            )
            
            IconButton(
                onClick = {
                    val amt = amount.toDoubleOrNull()
                    if (amt != null) {
                        onAdd(amt, category)
                        amount = ""
                    }
                },
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyanGlow)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = BackgroundStart)
            }
        }
    }
}
