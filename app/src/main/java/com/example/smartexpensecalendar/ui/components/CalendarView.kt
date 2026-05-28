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

    Column(modifier = modifier.padding(4.dp)) {
        // Day names
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth(),
            userScrollEnabled = false // Calendar itself shouldn't scroll
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
                    isToday = date == LocalDate.now(),
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
    isToday: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .aspectRatio(0.9f) // Slightly taller for more info space
            .padding(1.dp)
            .background(
                when {
                    isToday -> MaterialTheme.colorScheme.primaryContainer
                    totalAmount > 0 -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    else -> Color.Transparent
                },
                shape = MaterialTheme.shapes.small
            )
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = day.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontSize = 16.sp,
            fontWeight = if (isToday) FontWeight.ExtraBold else FontWeight.SemiBold,
            color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 1.dp)
        )
        
        if (totalAmount > 0) {
            Text(
                text = "₹${"%.0f".format(totalAmount)}",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (totalAmount > 1000) Color.Red else MaterialTheme.colorScheme.primary,
                maxLines = 1,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Spacer(modifier = Modifier.height(10.dp))
        }
        
        Row(
            modifier = Modifier.padding(bottom = 2.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            categories.take(3).forEach { category ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 0.5.dp)
                        .size(5.dp)
                        .background(getCategoryColor(category), shape = MaterialTheme.shapes.extraSmall)
                )
            }
        }
    }
}
