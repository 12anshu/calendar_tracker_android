package com.example.smartexpensecalendar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.smartexpensecalendar.core.designsystem.theme.*
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@Composable
fun MonthYearPicker(
    initialMonth: YearMonth,
    onDismiss: () -> Unit,
    onConfirm: (YearMonth) -> Unit
) {
    var selectedYear by remember { mutableIntStateOf(initialMonth.year) }
    var selectedMonth by remember { mutableIntStateOf(initialMonth.monthValue) }

    val years = (2024..2027).toList()
    val months = (1..12).toList()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = BackgroundEnd)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select Period",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Year Selector
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(years) { year ->
                        val isSelected = year == selectedYear
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) CyanGlow.copy(alpha = 0.15f) else SurfaceGlass)
                                .border(
                                    1.dp,
                                    if (isSelected) CyanGlow else SurfaceGlassBright,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedYear = year }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = year.toString(),
                                color = if (isSelected) CyanGlow else TextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Month Grid (3x4)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.height(240.dp)
                ) {
                    items(months) { monthValue ->
                        val isSelected = monthValue == selectedMonth
                        val monthName = Month.of(monthValue).getDisplayName(TextStyle.SHORT, Locale.getDefault())
                        
                        Box(
                            modifier = Modifier
                                .aspectRatio(1.5f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) CyanGlow else SurfaceGlass)
                                .clickable { 
                                    selectedMonth = monthValue 
                                }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = monthName,
                                color = if (isSelected) BackgroundStart else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = TextSecondary)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = { onConfirm(YearMonth.of(selectedYear, selectedMonth)) },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanGlow, contentColor = BackgroundStart),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Confirm", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
