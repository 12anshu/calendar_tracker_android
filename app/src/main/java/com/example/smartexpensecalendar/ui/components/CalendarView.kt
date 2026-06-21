package com.example.smartexpensecalendar.ui.components

import android.icu.util.Calendar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartexpensecalendar.domain.model.Expense
import com.example.smartexpensecalendar.core.designsystem.theme.*
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarView(
    yearMonth: YearMonth,
    expenses: List<Expense>,
    onDateClick: (LocalDate) -> Unit,
    selectedDate: LocalDate? = null,
    currencySymbol: String = "₹",
    modifier: Modifier = Modifier
) {
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfMonth = yearMonth.atDay(1).dayOfWeek.value % 7 // 0 for Sunday
    val days = (1..daysInMonth).toList()
    val prevMonthPadding = (0 until firstDayOfMonth).toList()

    Column(modifier = modifier.padding(horizontal = 4.dp, vertical = 0.dp)) {
        // Day names
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth(),
            userScrollEnabled = false,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(prevMonthPadding) {
                Box(modifier = Modifier.aspectRatio(1f))
            }
            items(days) { day ->
                val date = yearMonth.atDay(day)
                val dayExpenses = expenses.filter { 
                    it.date == date && 
                    it.type == com.example.smartexpensecalendar.domain.model.TransactionType.DEBIT &&
                    it.status == com.example.smartexpensecalendar.domain.model.TransactionStatus.COMPLETED
                }
                val totalAmount = dayExpenses.sumOf { it.amount }
                val categories = dayExpenses.map { it.category }.distinct()

                CalendarDayCell(
                    day = day,
                    totalAmount = totalAmount,
                    categories = categories,
                    currencySymbol = currencySymbol,
                    isSelected = date == selectedDate,
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
    currencySymbol: String = "₹",
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit
) {
    val heatmapColor = when {
        totalAmount > 5001 -> HeatmapHigh.copy(alpha = 0.2f)
        totalAmount > 1001 -> HeatmapMedium.copy(alpha = 0.2f)
        totalAmount > 0 -> HeatmapLow.copy(alpha = 0.2f)
        else -> Color.Transparent
    }
    val calendarTextColor = when {
        totalAmount > 5001 -> CalendarTextHigh.copy(alpha = 0.9f)
        totalAmount > 1001 -> CalendarTextMedium.copy(alpha = 0.9f)
        totalAmount > 0 -> CalendarTextSmall.copy(alpha = 0.9f)
        else -> Color.Transparent
    }

    val cellModifier = Modifier
        .aspectRatio(1f)
        .clip(RoundedCornerShape(8.dp))
        .then(
            when {

                isSelected && isToday -> {
                    Modifier
                        .border(
                            2.dp,
                            CyanGlow,
                            RoundedCornerShape(8.dp)
                        )
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    CyanGlow.copy(alpha = 0.28f),
                                    CyanGlow.copy(alpha = 0.10f)
                                )
                            )
                        )
                }

                isSelected -> {
                    Modifier
                        .border(
                            2.dp,
                            CyanGlow,
                            RoundedCornerShape(8.dp)
                        )
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    CyanGlow.copy(alpha = 0.22f),
                                    CyanGlow.copy(alpha = 0.08f)
                                )
                            )
                        )
                }

                isToday -> {
                    Modifier
                        .border(
                            1.5.dp,
                            PrimaryAccent.copy(alpha = 0.7f),
                            RoundedCornerShape(8.dp)
                        )
                        .background(
                            PrimaryAccent.copy(alpha = 0.06f)
                        )
                }

                else -> {
                    Modifier
                        .background(SurfaceGlass)
                        .border(
                            1.dp,
                            SurfaceGlassBright,
                            RoundedCornerShape(8.dp)
                        )
                }
            }
        )
        .background(heatmapColor)
        .clickable { onClick() }

    Box(modifier = cellModifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = day.toString(),
                style = MaterialTheme.typography.labelLarge,
                fontSize = 17.sp,
                letterSpacing = (-0.3).sp,
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isToday) PrimaryAccent else TextPrimary
            )

            if (totalAmount > 0) {
                Text(
                    text = "$currencySymbol${"%.0f".format(totalAmount)}",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = calendarTextColor,
                    maxLines = 1
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
