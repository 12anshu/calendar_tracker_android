package com.example.smartexpensecalendar.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartexpensecalendar.ui.branding.Branding
import com.example.smartexpensecalendar.core.designsystem.theme.CyanGlow
import com.example.smartexpensecalendar.core.designsystem.theme.TextSecondary

@Composable
fun AppLogoText(
    modifier: Modifier = Modifier,
    showTagline: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.headlineSmall
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${Branding.APP_NAME} ",
                style = textStyle.copy(
                    color = CyanGlow,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )
            )

            Text(
                text = Branding.APP_SUBTITLE,
                style = textStyle.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )
            )
        }

        if (showTagline) {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = Branding.APP_TAGLINE,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}
