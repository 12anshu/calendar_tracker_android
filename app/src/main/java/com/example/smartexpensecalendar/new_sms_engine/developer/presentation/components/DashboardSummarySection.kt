package com.example.smartexpensecalendar.new_sms_engine.developer.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartexpensecalendar.core.designsystem.theme.*
import com.example.smartexpensecalendar.new_sms_engine.developer.presentation.DashboardFilter
import com.example.smartexpensecalendar.new_sms_engine.developer.presentation.DashboardSummary
import com.example.smartexpensecalendar.new_sms_engine.classification.model.MessageType

@Composable
fun DashboardSummarySection(
    summary: DashboardSummary,
    selectedFilters: Set<DashboardFilter>,
    onFilterClick: (DashboardFilter) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QualificationCard(summary, selectedFilters, onFilterClick)
        ClassificationSummaryCard(summary, selectedFilters, onFilterClick)
        ExtractionStatsCard(summary, selectedFilters, onFilterClick)
        
        // Temporarily removed Classification Evidence, Pattern Match Statistics and Token Category Statistics
        /*
        ClassificationEvidenceCard(summary, selectedFilters, onFilterClick)
        PatternMatchStatsCard(summary, selectedFilters, onFilterClick)
        TokenCategoryStatsCard(summary, selectedFilters, onFilterClick)
        */
    }
}

@Composable
private fun QualificationCard(
    summary: DashboardSummary,
    selectedFilters: Set<DashboardFilter>,
    onClick: (DashboardFilter) -> Unit
) {
    var expanded by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceGlass)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("ENGINE OVERVIEW", color = CyanGlow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        SummaryMiniCard(
                            label = "Total SMS",
                            value = summary.totalSms.toString(),
                            icon = Icons.Default.Sms,
                            modifier = Modifier.weight(1f)
                        )
                        SummaryMiniCard(
                            label = "Qualified",
                            value = summary.qualified.toString(),
                            percentage = calculatePercent(summary.qualified, summary.totalSms),
                            icon = Icons.Default.CheckCircle,
                            color = PrimaryAccent,
                            selected = selectedFilters.contains(DashboardFilter.QUALIFIED),
                            modifier = Modifier.weight(1f)
                        ) { onClick(DashboardFilter.QUALIFIED) }
                        SummaryMiniCard(
                            label = "Rejected",
                            value = summary.notQualified.toString(),
                            percentage = calculatePercent(summary.notQualified, summary.totalSms),
                            icon = Icons.Default.Block,
                            color = ColorFood,
                            selected = selectedFilters.contains(DashboardFilter.NOT_QUALIFIED),
                            modifier = Modifier.weight(1f)
                        ) { onClick(DashboardFilter.NOT_QUALIFIED) }
                    }
                }
            }
        }
    }
}

@Composable
private fun ClassificationSummaryCard(
    summary: DashboardSummary,
    selectedFilters: Set<DashboardFilter>,
    onClick: (DashboardFilter) -> Unit
) {
    var expanded by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceGlass)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("CLASSIFICATION SUMMARY", color = CyanGlow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        SummaryMiniCard(
                            label = "Transaction",
                            value = summary.transactionCount.toString(),
                            percentage = calculatePercent(summary.transactionCount, summary.qualified),
                            icon = Icons.Default.ReceiptLong,
                            color = PrimaryAccent,
                            selected = selectedFilters.any { it is DashboardFilter.MESSAGE_TYPE && it.type == MessageType.TRANSACTION },
                            modifier = Modifier.weight(1f)
                        ) { onClick(DashboardFilter.MESSAGE_TYPE(MessageType.TRANSACTION)) }
                        
                        SummaryMiniCard(
                            label = "Obligation",
                            value = summary.obligationCount.toString(),
                            percentage = calculatePercent(summary.obligationCount, summary.qualified),
                            icon = Icons.Default.PendingActions,
                            color = ColorFood,
                            selected = selectedFilters.any { it is DashboardFilter.MESSAGE_TYPE && it.type == MessageType.OBLIGATION },
                            modifier = Modifier.weight(1f)
                        ) { onClick(DashboardFilter.MESSAGE_TYPE(MessageType.OBLIGATION)) }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        SummaryMiniCard(
                            label = "Information",
                            value = summary.informationCount.toString(),
                            percentage = calculatePercent(summary.informationCount, summary.qualified),
                            icon = Icons.Default.Info,
                            color = SecondaryAccent,
                            selected = selectedFilters.any { it is DashboardFilter.MESSAGE_TYPE && it.type == MessageType.INFORMATION },
                            modifier = Modifier.weight(1f)
                        ) { onClick(DashboardFilter.MESSAGE_TYPE(MessageType.INFORMATION)) }
                        
                        SummaryMiniCard(
                            label = "Unknown",
                            value = summary.unknownTypeCount.toString(),
                            percentage = calculatePercent(summary.unknownTypeCount, summary.qualified),
                            icon = Icons.Default.QuestionMark,
                            color = Color.Gray,
                            selected = selectedFilters.any { it is DashboardFilter.MESSAGE_TYPE && it.type == MessageType.UNKNOWN },
                            modifier = Modifier.weight(1f)
                        ) { onClick(DashboardFilter.MESSAGE_TYPE(MessageType.UNKNOWN)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExtractionStatsCard(
    summary: DashboardSummary,
    selectedFilters: Set<DashboardFilter>,
    onClick: (DashboardFilter) -> Unit
) {
    var expanded by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceGlass)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("EXTRACTION SUMMARY", color = CyanGlow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    ExtractionRow("Amount", summary.amountFound, summary.amountMissing, summary.transactionCount, onClick, DashboardFilter.AMOUNT_FOUND, DashboardFilter.AMOUNT_MISSING, selectedFilters)
                    Spacer(modifier = Modifier.height(8.dp))
                    ExtractionRow("Merchant", summary.merchantFound, summary.merchantMissing, summary.transactionCount, onClick, DashboardFilter.MERCHANT_FOUND, DashboardFilter.MERCHANT_MISSING, selectedFilters)
                    Spacer(modifier = Modifier.height(8.dp))
                    ExtractionRow("Direction", summary.directionFound, summary.directionMissing, summary.transactionCount, onClick, DashboardFilter.DIRECTION_FOUND, DashboardFilter.DIRECTION_MISSING, selectedFilters)
                    Spacer(modifier = Modifier.height(8.dp))
                    ExtractionRow("Payment Mode", summary.modeFound, summary.modeMissing, summary.transactionCount, onClick, DashboardFilter.MODE_FOUND, DashboardFilter.MODE_MISSING, selectedFilters)
                }
            }
        }
    }
}

@Composable
private fun ExtractionRow(
    label: String,
    found: Int,
    missing: Int,
    total: Int,
    onClick: (DashboardFilter) -> Unit,
    foundFilter: DashboardFilter,
    missingFilter: DashboardFilter,
    selectedFilters: Set<DashboardFilter>
) {
    Column {
        Text(label, color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SummaryMiniCard(
                label = "Found",
                value = found.toString(),
                percentage = calculatePercent(found, total),
                icon = Icons.Default.Check,
                color = PrimaryAccent,
                selected = selectedFilters.contains(foundFilter),
                modifier = Modifier.weight(1f)
            ) { onClick(foundFilter) }
            SummaryMiniCard(
                label = "Missing",
                value = missing.toString(),
                percentage = calculatePercent(missing, total),
                icon = Icons.Default.Close,
                color = ColorFood,
                selected = selectedFilters.contains(missingFilter),
                modifier = Modifier.weight(1f)
            ) { onClick(missingFilter) }
        }
    }
}

@Composable
fun SummaryMiniCard(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    color: Color = TextPrimary,
    percentage: Int? = null,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) color.copy(alpha = 0.2f) else SurfaceGlassBright)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(8.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = color.copy(alpha = 0.6f), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(label, color = TextSecondary, fontSize = 9.sp, maxLines = 1, modifier = Modifier.weight(1f))
                if (percentage != null) {
                    Text("$percentage%", color = color.copy(alpha = 0.8f), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = color, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

private fun calculatePercent(count: Int, total: Int): Int {
    if (total == 0) return 0
    return (count.toFloat() / total * 100).toInt()
}

private fun String.capitalize(): String =
    this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
