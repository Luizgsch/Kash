package com.kash.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun KashTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background    = KashColors.Background,
            surface       = KashColors.Surface,
            surfaceVariant= KashColors.SurfaceVariant,
            onBackground  = KashColors.OnSurface,
            onSurface     = KashColors.OnSurface,
            primary       = KashColors.Accent,
            onPrimary     = Color(0xFF1A1A1A),
            outline       = KashColors.Border
        ),
        typography = KashTypography,
        content    = content
    )
}
