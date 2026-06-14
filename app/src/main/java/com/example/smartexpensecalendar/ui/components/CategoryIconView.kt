package com.example.smartexpensecalendar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.smartexpensecalendar.core.designsystem.theme.getCategoryColor
import com.example.smartexpensecalendar.utils.CategoryIconsUtils

@Composable
fun CategoryIconView(
    category: String,
    size: Dp = 40.dp,
    iconSize: Dp = 20.dp,
    shape: RoundedCornerShape = RoundedCornerShape(10.dp),
    backgroundColor: Color? = null,
    iconColor: Color? = null
) {
    val categoryColor = getCategoryColor(category)
    val finalBackgroundColor = backgroundColor ?: categoryColor.copy(alpha = 0.25f)
    val finalIconColor = iconColor ?: categoryColor

    Box(
        modifier = Modifier
            .size(size)
            .clip(shape)
            .background(finalBackgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = CategoryIconsUtils.getIcon(category),
            contentDescription = category,
            tint = finalIconColor,
            modifier = Modifier.size(iconSize)
        )
    }
}
