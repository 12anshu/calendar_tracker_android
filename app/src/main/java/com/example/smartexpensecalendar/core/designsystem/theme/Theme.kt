package com.example.smartexpensecalendar.core.designsystem.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val FintechColorScheme = darkColorScheme(
    primary = PrimaryAccent,
    secondary = SecondaryAccent,
    tertiary = CyanGlow,
    background = BackgroundStart,
    surface = BackgroundEnd,
    onPrimary = TextPrimary,
    onSecondary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary
)

@Composable
fun SmartExpenseCalendarTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = BackgroundStart.toArgb()
            window.navigationBarColor = BackgroundEnd.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = FintechColorScheme,
        typography = AppTypography,
        content = content
    )
}
