package com.example.smartexpensecalendar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smartexpensecalendar.presentation.budget.BudgetViewModel
import com.example.smartexpensecalendar.presentation.budget.CategoryBudgetState
import com.example.smartexpensecalendar.ui.theme.*
import com.example.smartexpensecalendar.utils.CurrencyUtils.formatIndianCurrency
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetDetailScreen(
    navController: NavController,
    viewModel: BudgetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedMonth by viewModel.selectedMonth.collectAsState()
    var showEditDialog by remember { mutableStateOf<String?>(null) } // Category name or "Total"
    var showMonthPicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box {
                        TextButton(onClick = { showMonthPicker = true }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${selectedMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${selectedMonth.year} Budget",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
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
                                    text = {
                                        Text(
                                            "${month.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${month.year}",
                                            color = TextPrimary
                                        )
                                    },
                                    onClick = {
                                        viewModel.setMonth(month)
                                        showMonthPicker = false
                                    }
                                )
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
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Overall Monthly Budget Card
            item {
                BudgetOverviewCard(
                    totalSpent = uiState.totalSpent,
                    totalBudget = uiState.totalBudget,
                    onEditClick = { showEditDialog = "Total" }
                )
            }

            item {
                Text(
                    text = "Category Budgets",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            // Category List
            items(uiState.categoryBudgets) { categoryState ->
                CategoryBudgetCard(
                    state = categoryState,
                    onEditClick = { showEditDialog = categoryState.category }
                )
            }
        }
    }

    // Simple Edit Dialog
    if (showEditDialog != null) {
        var editValue by remember { 
            mutableStateOf(
                if (showEditDialog == "Total") uiState.totalBudget.toString()
                else uiState.categoryBudgets.find { it.category == showEditDialog }?.budget?.toString() ?: "0"
            )
        }
        
        AlertDialog(
            onDismissRequest = { showEditDialog = null },
            containerColor = BackgroundEnd,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary,
            title = { 
                Text(
                    text = "Set Budget for ${showEditDialog}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ) 
            },
            text = {
                Column {
                    Text(
                        text = "Enter the monthly limit for this category.",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    OutlinedTextField(
                        value = editValue,
                        onValueChange = { editValue = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text("Amount (₹)") },
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary, fontWeight = FontWeight.Bold),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanGlow,
                            unfocusedBorderColor = SurfaceGlassBright,
                            focusedLabelColor = CyanGlow,
                            cursorColor = CyanGlow
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = editValue.toDoubleOrNull() ?: 0.0
                        viewModel.updateBudget(showEditDialog!!, amount)
                        showEditDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanGlow, contentColor = BackgroundStart),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun BudgetOverviewCard(
    totalSpent: Double,
    totalBudget: Double,
    onEditClick: () -> Unit
) {
    val progress = if (totalBudget > 0) (totalSpent / totalBudget).toFloat().coerceIn(0f, 1f) else 0f
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    listOf(SurfaceGlassBright, Color.Transparent)
                )
            )
            .border(1.dp, SurfaceGlassBright, RoundedCornerShape(24.dp))
            .padding(24.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Monthly Budget", color = TextSecondary, style = MaterialTheme.typography.labelMedium)
                    Text("₹${formatIndianCurrency(totalBudget)}", color = TextPrimary, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
                }
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.background(SurfaceGlass, CircleShape)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = CyanGlow, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(CircleShape),
                color = if (progress > 0.9f) ColorTransport else CyanGlow,
                trackColor = SurfaceGlassBright
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Spent: ₹${formatIndianCurrency(totalSpent)}", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                Text("${(progress * 100).toInt()}% Used", color = if (progress > 0.9f) ColorTransport else CyanGlow, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun CategoryBudgetCard(
    state: CategoryBudgetState,
    onEditClick: () -> Unit
) {
    val categoryColor = getCategoryColor(state.category)
    val progress = if (state.budget > 0) (state.spent / state.budget).toFloat().coerceIn(0f, 1f) else 0f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceGlass)
            .border(1.dp, SurfaceGlassBright, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(12.dp).background(categoryColor, CircleShape))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(state.category, color = TextPrimary, fontWeight = FontWeight.Bold)
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onEditClick() }
                ) {
                    Text(
                        text = if (state.budget > 0) "₹${formatIndianCurrency(state.budget)}" else "Set Budget",
                        color = if (state.budget > 0) TextSecondary else CyanGlow,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.Edit, contentDescription = null, tint = TextSecondary.copy(alpha = 0.5f), modifier = Modifier.size(14.dp))
                }
            }

            if (state.budget > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(SurfaceGlassBright, CircleShape)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .background(categoryColor, CircleShape)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Spent ₹${formatIndianCurrency(state.spent)} of ₹${formatIndianCurrency(state.budget)}",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}
