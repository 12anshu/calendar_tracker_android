package com.example.smartexpensecalendar.new_sms_engine.developer.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import com.example.smartexpensecalendar.core.designsystem.theme.*
import com.example.smartexpensecalendar.new_sms_engine.developer.model.DeveloperSmsResult
import com.example.smartexpensecalendar.new_sms_engine.extraction.direction.models.Direction
import com.example.smartexpensecalendar.new_sms_engine.extraction.paymentmode.models.PaymentMode

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DeveloperSmsCardV2(
    sms: DeveloperSmsResult
) {
    var expanded by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceGlass)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(12.dp)
        ) {
            // Summary Header (Always visible)
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top Left - Sender id
                    Text(
                        text = sms.sender,
                        color = SecondaryAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.weight(1f)
                    )

                    // Center - Extracted Merchant
                    Text(
                        text = sms.merchant ?: "---",
                        color = CyanGlow,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 10.sp,
                        modifier = Modifier.weight(1.5f),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )

                    // Top Right - Message Type
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (sms.qualified) {
                            DecisionBadge(sms.messageType.name, CyanGlow)
                        } else {
                            DecisionBadge("REJECTED", ColorTransport)
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        Icon(
                            if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Raw Message Body (Full)
                Text(
                    text = sms.message,
                    color = TextPrimary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    
                    // 1. RAW SMS
                    PipelineSection(title = "RAW SMS") {
                        DetailRow("Sender", sms.sender)
                        DetailRow("Message", sms.message)
                        DetailRow("Date", java.util.Date(sms.timestamp).toString())
                    }

                    // 2. QUALIFICATION
                    PipelineSection(title = "QUALIFICATION") {
                        DetailRow("Status", if (sms.qualified) "Qualified" else "Rejected")
                        DetailRow("Reason", sms.qualificationReason ?: "Sender Format Check")
                    }

                    // 3. TOKENIZATION
                    PipelineSection(title = "TOKENIZATION") {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            sms.tokens.forEach { token ->
                                TokenChip(token.text)
                            }
                        }
                    }

                    // 4. TOKEN CATEGORIES
                    PipelineSection(title = "TOKEN CATEGORIES") {
                        sms.tokens.forEach { token ->
                            if (token.categories.isNotEmpty() && !token.has(com.example.smartexpensecalendar.new_sms_engine.common.tokenizer.TokenCategory.UNKNOWN)) {
                                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                    Text(token.text, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(80.dp))
                                    Text("→ ", color = TextSecondary, fontSize = 11.sp)
                                    Text(token.categories.joinToString(", ") { it.name }, color = CyanGlow, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    // 5. MATCHED PATTERNS
                    PipelineSection(title = "MATCHED PATTERNS") {
                        if (sms.matchedPatterns.isEmpty()) {
                            Text("None", color = TextSecondary, fontSize = 11.sp)
                        } else {
                            sms.matchedPatterns.forEach { match ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Check, null, tint = PrimaryAccent, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(match.patternName, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // 6. GENERATED EVIDENCE
                    PipelineSection(title = "GENERATED EVIDENCE") {
                        if (sms.evidence.isEmpty()) {
                            Text("None", color = TextSecondary, fontSize = 11.sp)
                        } else {
                            sms.evidence.forEach { evidence ->
                                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Text(evidence.type.name, color = CyanGlow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        "Strength: ${evidence.strength} | Source: ${evidence.source}",
                                        color = TextSecondary,
                                        fontSize = 9.sp
                                    )
                                    Text("Matched: \"${evidence.matchedText}\"", color = TextPrimary, fontSize = 10.sp)
                                }
                            }
                        }
                    }

                    // 7. MESSAGE CLASSIFICATION
                    PipelineSection(title = "MESSAGE CLASSIFICATION") {
                        DetailRow("Type", sms.messageType.name)
                    }

                    // 8. TRANSACTION EXTRACTION
                    PipelineSection(title = "TRANSACTION EXTRACTION") {
                        DetailRow("Amount", sms.amount?.let { "${it.currency.symbol}${it.amount}" } ?: "Missing")
                        DetailRow("Direction", sms.direction.name)
                        DetailRow("Merchant", sms.merchant ?: "Missing")
                        DetailRow("Merchant Confidence", "${(sms.merchantConfidence * 100).toInt()}%")
                        DetailRow("Mode", sms.paymentMode.name)
                        DetailRow("Account", sms.account ?: "Missing")
                    }

                    // Copy Action
                    Button(
                        onClick = {
                            val report = buildString {
                                appendLine("--- RAW SMS ---")
                                appendLine("Sender: ${sms.sender}")
                                appendLine("Message: ${sms.message}")
                                appendLine("Date: ${java.util.Date(sms.timestamp)}")
                                appendLine()
                                appendLine("--- QUALIFICATION ---")
                                appendLine("Status: ${if (sms.qualified) "Qualified" else "Rejected"}")
                                appendLine("Reason: ${sms.qualificationReason ?: "Sender Format Check"}")
                                appendLine()
                                appendLine("--- TOKENIZATION ---")
                                appendLine("Tokens: ${sms.tokens.joinToString(", ") { it.text }}")
                                appendLine()
                                appendLine("--- TOKEN CATEGORIES ---")
                                sms.tokens.forEach { token ->
                                    if (token.categories.isNotEmpty() && !token.has(com.example.smartexpensecalendar.new_sms_engine.common.tokenizer.TokenCategory.UNKNOWN)) {
                                        appendLine("${token.text} -> ${token.categories.joinToString(", ") { it.name }}")
                                    }
                                }
                                appendLine()
                                appendLine("--- MATCHED PATTERNS ---")
                                if (sms.matchedPatterns.isEmpty()) appendLine("None")
                                else sms.matchedPatterns.forEach { appendLine("✓ ${it.patternName}") }
                                appendLine()
                                appendLine("--- GENERATED EVIDENCE ---")
                                if (sms.evidence.isEmpty()) appendLine("None")
                                else sms.evidence.forEach { 
                                    appendLine("[${it.type.name}] Strength: ${it.strength} | Source: ${it.source} | Match: \"${it.matchedText}\"")
                                }
                                appendLine()
                                appendLine("--- CLASSIFICATION ---")
                                appendLine("Type: ${sms.messageType.name}")
                                appendLine()
                                appendLine("--- EXTRACTION ---")
                                appendLine("Amount: ${sms.amount?.let { "${it.currency.symbol}${it.amount}" } ?: "Missing"} [Conf: ${(sms.amountConfidence * 100).toInt()}%]")
                                appendLine("Direction: ${sms.direction.name} [Conf: ${(sms.directionConfidence * 100).toInt()}%]")
                                appendLine("Merchant: ${sms.merchant ?: "Missing"} [Conf: ${(sms.merchantConfidence * 100).toInt()}%]")
                                appendLine("Mode: ${sms.paymentMode.name} [Conf: ${(sms.paymentModeConfidence * 100).toInt()}%]")
                                appendLine("Account: ${sms.account ?: "Missing"}")
                            }
                            clipboardManager.setText(AnnotatedString(report))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceGlassBright)
                    ) {
                        Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Copy Full Analysis", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun PipelineSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(title, color = CyanGlow, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = SurfaceGlassBright, thickness = 0.5.dp)
        content()
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text(label, color = TextSecondary, fontSize = 11.sp, modifier = Modifier.width(80.dp))
        Text(value, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun TokenChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(SurfaceGlassBright)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(text, color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun DecisionBadge(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, color.copy(alpha = 0.5f))
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

private fun PaymentMode.toIcon(): ImageVector? = when (this) {
    PaymentMode.UPI -> Icons.Default.QrCode
    PaymentMode.CARD -> Icons.Default.CreditCard
    PaymentMode.BANK_TRANSFER -> Icons.Default.AccountBalance
    PaymentMode.WALLET -> Icons.Default.AccountBalanceWallet
    PaymentMode.AUTO_DEBIT -> Icons.Default.Autorenew
    PaymentMode.MEAL_CARD -> Icons.Default.Restaurant
    PaymentMode.CASH -> Icons.Default.Payments
    PaymentMode.CHEQUE -> Icons.Default.Description
    PaymentMode.UNKNOWN -> Icons.Default.QuestionMark
    else -> {
        null
    }
}
