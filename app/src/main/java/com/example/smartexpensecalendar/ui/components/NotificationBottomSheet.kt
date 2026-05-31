package com.example.smartexpensecalendar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartexpensecalendar.domain.model.SMSProcessingLog
import com.example.smartexpensecalendar.ui.theme.*
import com.example.smartexpensecalendar.utils.CurrencyUtils.formatIndianCurrency
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationBottomSheet(
    notifications: List<SMSProcessingLog>,
    onClearAll: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = BackgroundEnd,
        dragHandle = { BottomSheetDefaults.DragHandle(color = SurfaceGlassBright) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 40.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Activity",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                
                if (notifications.isNotEmpty()) {
                    TextButton(onClick = onClearAll) {
                        Text("Clear All", color = ColorTransport, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (notifications.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No new notifications", color = TextSecondary)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(notifications) { log ->
                        NotificationItem(log)
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(log: SMSProcessingLog) {
    val date = Instant.ofEpochMilli(log.date).atZone(ZoneId.systemDefault()).toLocalDateTime()
    val formatter = DateTimeFormatter.ofPattern("dd MMM, hh:mm a")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceGlass, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Transaction Synced",
                color = CyanGlow,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = date.format(formatter),
                color = TextSecondary,
                fontSize = 10.sp
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Successfully parsed ₹${formatIndianCurrency(log.parsedAmount ?: 0.0)} spent at ${log.parsedMerchant ?: "Unknown Merchant"}",
            color = TextPrimary,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
