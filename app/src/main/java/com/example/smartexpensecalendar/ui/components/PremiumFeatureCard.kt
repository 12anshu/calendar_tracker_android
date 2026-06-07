package com.example.smartexpensecalendar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartexpensecalendar.R
import com.example.smartexpensecalendar.core.designsystem.theme.*

@Composable
fun PremiumFeatureCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        PremiumGold.copy(alpha = 0.08f),
                        Color.Transparent
                    )
                )
            )
            .background(SurfaceGlass)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(
                        PremiumGold.copy(alpha = 0.4f),
                        SurfaceGlassBright
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon with soft glow
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PremiumGold.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_military_tech_24),
                    contentDescription = null,
                    tint = PremiumGold,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "SMART Pro",
                    color = PremiumGold,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                
                Text(
                    text = "Access expenses older than 3 months and sync securely.",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 16.sp
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Upgrade",
                tint = PremiumGold.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
