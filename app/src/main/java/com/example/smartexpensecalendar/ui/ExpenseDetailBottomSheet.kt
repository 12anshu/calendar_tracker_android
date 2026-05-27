package com.example.smartexpensecalendar.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartexpensecalendar.domain.model.DefaultCategories
import com.example.smartexpensecalendar.domain.model.Expense
import com.example.smartexpensecalendar.presentation.detail.ExpenseDetailViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDetailBottomSheet(
    date: LocalDate,
    onDismiss: () -> Unit,
    viewModel: ExpenseDetailViewModel = hiltViewModel()
) {
    val expenses by viewModel.getExpensesForDate(date).collectAsState(initial = emptyList())
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = Modifier.fillMaxHeight(0.8f)
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Expenses for $date",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Total: ₹${expenses.sumOf { it.amount }}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Add Expense Section
                AddExpenseSection(onAdd = { amount, category ->
                    viewModel.addExpense(amount, category, date)
                })

                Divider(modifier = Modifier.padding(vertical = 16.dp))

                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(expenses) { expense ->
                        ExpenseRow(
                            expense = expense,
                            onDelete = {
                                viewModel.deleteExpense(expense)
                                scope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "Expense deleted",
                                        actionLabel = "Undo"
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        viewModel.addExpense(expense.amount, expense.category, expense.date)
                                    }
                                }
                            },
                            onEdit = { amount, category ->
                                viewModel.updateExpense(expense, amount, category)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseRow(expense: Expense, onDelete: () -> Unit, onEdit: (Double, String) -> Unit) {
    var isEditing by remember { mutableStateOf(false) }
    var editAmount by remember { mutableStateOf(expense.amount.toString()) }
    var editCategory by remember { mutableStateOf(expense.category) }
    var expanded by remember { mutableStateOf(false) }

    if (isEditing) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedButton(onClick = { expanded = true }) {
                        Text(editCategory)
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DefaultCategories.list.forEach { cat ->
                            DropdownMenuItem(text = { Text(cat) }, onClick = { editCategory = cat; expanded = false })
                        }
                    }
                }
                OutlinedTextField(
                    value = editAmount,
                    onValueChange = { editAmount = it },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = { isEditing = false }) { Text("Cancel") }
                Button(onClick = {
                    val amt = editAmount.toDoubleOrNull()
                    if (amt != null) {
                        onEdit(amt, editCategory)
                        isEditing = false
                    }
                }) { Text("Save") }
            }
        }
    } else {
        ListItem(
            headlineContent = { Text(expense.category) },
            supportingContent = {
                Text(
                    text = "Source: ${expense.source}${if (expense.merchant != null) " (${expense.merchant})" else ""}",
                    style = MaterialTheme.typography.bodySmall
                )
            },
            trailingContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "₹${expense.amount}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { isEditing = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        )
    }
}

@Composable
fun AddExpenseSection(onAdd: (Double, String) -> Unit) {
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(DefaultCategories.list.first()) }
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(category)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DefaultCategories.list.forEach { cat ->
                    DropdownMenuItem(text = { Text(cat) }, onClick = { category = cat; expanded = false })
                }
            }
        }
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            modifier = Modifier.weight(1f),
            label = { Text("Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )
        Button(onClick = {
            val amt = amount.toDoubleOrNull()
            if (amt != null) {
                onAdd(amt, category)
                amount = ""
            }
        }) {
            Text("Add")
        }
    }
}
