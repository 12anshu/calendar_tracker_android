package com.example.smartexpensecalendar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.smartexpensecalendar.domain.model.Expense

@Composable
fun MonthlySummary(
    expenses: List<Expense>,
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
            Text(
                text = "Monthly Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row {
                var showExportMenu by remember { mutableStateOf(false) }
                
                Box {
                    TextButton(onClick = { showExportMenu = true }) {
                        Text("Export")
                    }
                    DropdownMenu(expanded = showExportMenu, onDismissRequest = { showExportMenu = false }) {
                        DropdownMenuItem(text = { Text("CSV") }, onClick = { onExportCSV(); showExportMenu = false })
                        DropdownMenuItem(text = { Text("JSON") }, onClick = { onExportJSON(); showExportMenu = false })
                    }
                }
                
                TextButton(onClick = onImportJSON) {
                    Text("Import")
                }
            }
        }
        Text(
            text = "Total: ₹${"%.2f".format(totalMonth)}",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(categoryBreakdown) { (category, amount) ->
                CategoryRow(category, amount, totalMonth)
            }
        }
    }
}

@Composable
fun CategoryRow(category: String, amount: Double, total: Double) {
    val percentage = if (total > 0) (amount / total).toFloat() else 0f
    
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = category, style = MaterialTheme.typography.bodyMedium)
            Text(text = "₹${amount.toInt()}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.extraSmall)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(percentage)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.extraSmall)
            )
        }
    }
}
