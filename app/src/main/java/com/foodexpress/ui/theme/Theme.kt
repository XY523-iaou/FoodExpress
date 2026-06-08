package com.foodexpress.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// Brand colors
private val LightColorScheme = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFFFF6B35),        // 品牌橙
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = androidx.compose.ui.graphics.Color(0xFFFFDBC8),
    secondary = androidx.compose.ui.graphics.Color(0xFF2EC4B6),      // 活力青
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFFC8F7F3),
    tertiary = androidx.compose.ui.graphics.Color(0xFFFFB800),       // 强调黄
    background = androidx.compose.ui.graphics.Color(0xFFF8F9FA),
    surface = androidx.compose.ui.graphics.Color.White,
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFFF0F0F0),
    error = androidx.compose.ui.graphics.Color(0xFFE53935),
    onBackground = androidx.compose.ui.graphics.Color(0xFF1A1A2E),
    onSurface = androidx.compose.ui.graphics.Color(0xFF1A1A2E),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF6B7280),
    outline = androidx.compose.ui.graphics.Color(0xFFE5E7EB)
)

private val DarkColorScheme = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFFFF8A65),
    onPrimary = androidx.compose.ui.graphics.Color(0xFF1A1A2E),
    primaryContainer = androidx.compose.ui.graphics.Color(0xFF6D2C00),
    secondary = androidx.compose.ui.graphics.Color(0xFF5ED9CF),
    onSecondary = androidx.compose.ui.graphics.Color(0xFF1A1A2E),
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFF003A35),
    tertiary = androidx.compose.ui.graphics.Color(0xFFFFD54F),
    background = androidx.compose.ui.graphics.Color(0xFF121212),
    surface = androidx.compose.ui.graphics.Color(0xFF1E1E1E),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFF2C2C2C),
    error = androidx.compose.ui.graphics.Color(0xFFEF5350),
    onBackground = androidx.compose.ui.graphics.Color(0xFFE8E8E8),
    onSurface = androidx.compose.ui.graphics.Color(0xFFE8E8E8),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF9CA3AF),
    outline = androidx.compose.ui.graphics.Color(0xFF3F3F3F)
)

@Composable
fun FoodExpressTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

