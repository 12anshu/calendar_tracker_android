package com.example.smartexpensecalendar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.smartexpensecalendar.domain.model.Expense
import com.example.smartexpensecalendar.presentation.home.SyncSummary

@Composable
fun MonthlySummary(
    expenses: List<Expense>,
    syncSummary: SyncSummary? = null,
    onExportCSV: () -> Unit = {},
    onExportJSON: () -> Unit = {},
    onImportJSON: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val totalMonth = expenses.sumOf { it.amount }
    val categoryBreakdown = expenses.groupBy { it.category }
        .mapValues { it.value.sumOf { e -> e.amount } }
        .toList()
        .sortedByDescending { it.second }

    Column(modifier = modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Monthly Spending",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "₹${"%.2f".format(totalMonth)}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            
            Row {
                var showExportMenu by remember { mutableStateOf(false) }
                var showSyncInfo by remember { mutableStateOf(false) }

                if (syncSummary != null) {
                    IconButton(onClick = { showSyncInfo = true }) {
                        Icon(Icons.Default.Info, contentDescription = "Sync Info", tint = MaterialTheme.colorScheme.secondary)
                    }

                    if (showSyncInfo) {
                        AlertDialog(
                            onDismissRequest = { showSyncInfo = false },
                            title = { Text("Sync Summary") },
                            text = {
                                Column {
                                    Text("Total SMS Scanned: ${syncSummary.totalSmsScanned}")
                                    Text("Financial SMS Found: ${syncSummary.financialSmsFound}")
                                    Text("Total Amount Detected: ₹${"%.2f".format(syncSummary.totalAmount)}")
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("This helps verify if the app is missing or over-counting messages.", style = MaterialTheme.typography.bodySmall)
                                }
                            },
                            confirmButton = {
                                TextButton(onClick = { showSyncInfo = false }) {
                                    Text("Close")
                                }
                            }
                        )
                    }
                }
                
                Box {
                    IconButton(onClick = { showExportMenu = true }) {
                        Text("Export", style = MaterialTheme.typography.labelSmall)
                    }
                    DropdownMenu(expanded = showExportMenu, onDismissRequest = { showExportMenu = false }) {
                        DropdownMenuItem(text = { Text("Export CSV") }, onClick = { onExportCSV(); showExportMenu = false })
                        DropdownMenuItem(text = { Text("Export JSON") }, onClick = { onExportJSON(); showExportMenu = false })
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Categories",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(categoryBreakdown) { (category, amount) ->
                CategoryCard(category, amount, totalMonth)
            }
        }
    }
}

@Composable
fun CategoryCard(category: String, amount: Double, total: Double) {
    val percentage = if (total > 0) (amount / total).toFloat() else 0f
    
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = category,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "₹${"%,.0f".format(amount)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = { percentage },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = getCategoryColor(category),
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            
            Text(
                text = "${(percentage * 100).toInt()}% of total",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun getCategoryColor(category: String): Color {
    return when (category.lowercase()) {
        "food" -> Color(0xFFFF9800)
        "groceries" -> Color(0xFF4CAF50)
        "shopping", "online shopping" -> Color(0xFF2196F3)
        "travel" -> Color(0xFF00BCD4)
        "bill payment" -> Color(0xFFE91E63)
        "utilities" -> Color(0xFF9C27B0)
        "rent" -> Color(0xFF795548)
        "entertainment" -> Color(0xFF673AB7)
        "medical" -> Color(0xFFF44336)
        "fuel" -> Color(0xFF607D8B)
        else -> MaterialTheme.colorScheme.primary
    }
}
