package com.example.smartexpensecalendar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartexpensecalendar.domain.model.Expense
import com.example.smartexpensecalendar.presentation.home.SyncSummary
import com.example.smartexpensecalendar.ui.theme.*
import com.example.smartexpensecalendar.utils.CurrencyUtils.formatIndianCurrency

@Composable
fun MonthlySummary(
    expenses: List<Expense>,
    syncSummary: SyncSummary? = null,
    totalBudget: Double = 0.0,
    onExportCSV: () -> Unit = {},
    onExportJSON: () -> Unit = {},
    onImportJSON: () -> Unit = {},
    onAnalyticsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val totalMonth = expenses.filter { 
        it.type == com.example.smartexpensecalendar.domain.model.TransactionType.DEBIT &&
        it.status == com.example.smartexpensecalendar.domain.model.TransactionStatus.COMPLETED
    }.sumOf { it.amount }

    Column(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .padding(top = 16.dp)
    ) {
        AnalyticsCard(
            totalAmount = totalMonth,
            budget = totalBudget,
            onCardClick = onAnalyticsClick
        )
    }
}

@Composable
fun AnalyticsCard(
    totalAmount: Double,
    budget: Double,
    onCardClick: () -> Unit
) {
    val budgetUsedPercentage = if (budget > 0) ((totalAmount / budget) * 100).coerceAtMost(100.0) else 0.0
    val remaining = budget - totalAmount

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceGlass)
            .border(
                width = 1.dp,
                color = SurfaceGlassBright,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onCardClick() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // LEFT: Monthly Spending
            Column(modifier = Modifier.weight(1.3f)) {
                Text(
                    text = "Monthly Spending",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "₹${formatIndianCurrency(totalAmount)}",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-1.sp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "▲ 12.5%",
                        color = ColorGroceries,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = " vs Apr 2026",
                        color = TextSecondary,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            // CENTER: Donut
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                BudgetDonut(
                    progress = budgetUsedPercentage.toFloat(),
                    modifier = Modifier.size(90.dp)
                )
            }

            // RIGHT: Budget & Remaining
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Open Analytics",
                    tint = TextSecondary,
                    modifier = Modifier.size(22.dp)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Budget",
                    color = TextSecondary,
                    style = MaterialTheme.typography.labelSmall
                )
                if (budget > 0) {
                    Text(
                        text = "₹${formatIndianCurrency(budget)}",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onCardClick() }
                    ) {
                        Text(
                            text = "Set Budget",
                            color = CyanGlow,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = CyanGlow,
                            modifier = Modifier.size(12.dp).padding(start = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Remaining",
                    color = TextSecondary,
                    style = MaterialTheme.typography.labelSmall
                )
                if (budget > 0) {
                    Text(
                        text = "₹${formatIndianCurrency(kotlin.math.abs(remaining))}",
                        color = if (remaining >= 0) ColorGroceries else ColorTransport,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Text(
                        text = "--",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun BudgetDonut(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.fillMaxSize(),
            color = SurfaceGlassBright,
            strokeWidth = 7.dp,
            trackColor = ProgressIndicatorDefaults.circularTrackColor,
            strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
        )

        CircularProgressIndicator(
            progress = { progress / 100f },
            modifier = Modifier.fillMaxSize(),
            color = ProgressColor,
            strokeWidth = 7.dp,
            trackColor = ProgressIndicatorDefaults.circularTrackColor,
            strokeCap = StrokeCap.Round,
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${progress.toInt()}%",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "budget",
                color = TextSecondary,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun PremiumCategoryCard(category: String, amount: Double, total: Double) {
    val percentage = if (total > 0) (amount / total).toFloat() else 0f
    val categoryColor = getCategoryColor(category)
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(SurfaceGlass)
            .border(
                1.dp,
                SurfaceGlassBright,
                RoundedCornerShape(6.dp)
            )
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    )
    {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(categoryColor.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.size(8.dp).background(categoryColor, CircleShape))
        }

        Spacer(modifier = Modifier.width(0.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = category,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Normal,
                    color = TextSecondary
                )
                Text(
                    text = "₹${"%,.0f".format(amount)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Normal,
                    color = TextSecondary
                )
            }
            
            Spacer(modifier = Modifier.height(0.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(SurfaceGlass, CircleShape)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(percentage)
                        .fillMaxHeight()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    categoryColor.copy(alpha = 0.5f),
                                    categoryColor
                                )
                            ),
                            CircleShape
                        )
                )
            }
            
            Text(
                text = "${(percentage * 100).toInt()}% of total spend",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
