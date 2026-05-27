package com.example.smartexpensecalendar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartexpensecalendar.domain.model.Expense
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarView(
    yearMonth: YearMonth,
    expenses: List<Expense>,
    onDateClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfMonth = yearMonth.atDay(1).dayOfWeek.value % 7 // 0 for Sunday
    val days = (1..daysInMonth).toList()
    val prevMonthPadding = (0 until firstDayOfMonth).toList()

    Column(modifier = modifier.padding(8.dp)) {
        // Day names
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxSize()
        ) {
            items(prevMonthPadding) {
                Box(modifier = Modifier.aspectRatio(1f))
            }
            items(days) { day ->
                val date = yearMonth.atDay(day)
                val dayExpenses = expenses.filter { it.date == date }
                val totalAmount = dayExpenses.sumOf { it.amount }
                val categories = dayExpenses.map { it.category }.distinct()

                CalendarDayCell(
                    day = day,
                    totalAmount = totalAmount,
                    categories = categories,
                    onClick = { onDateClick(date) }
                )
            }
        }
    }
}

@Composable
fun CalendarDayCell(
    day: Int,
    totalAmount: Double,
    categories: List<String>,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .background(
                if (totalAmount > 0) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                else Color.Transparent,
                shape = MaterialTheme.shapes.small
            )
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = day.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 2.dp)
        )
        
        if (totalAmount > 0) {
            Text(
                text = "₹${"%.0f".format(totalAmount)}",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 7.sp,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1
            )
        }
        
        Row(
            modifier = Modifier.padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            categories.take(4).forEach { category ->
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(getCategoryColor(category), shape = MaterialTheme.shapes.extraSmall)
                )
            }
        }
    }
}
