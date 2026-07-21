package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = DairyGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = DairyGreenContainer,
    onPrimaryContainer = DairyGreenPrimary,
    secondary = DairyGreenSecondary,
    onSecondary = Color.White,
    secondaryContainer = FarmGoldContainer,
    onSecondaryContainer = FarmGoldAccent,
    tertiary = FarmGoldAccent,
    background = MilkCreamBackground,
    onBackground = TextPrimary,
    surface = MilkCardSurface,
    onSurface = TextPrimary,
    surfaceVariant = DairyGreenContainer,
    onSurfaceVariant = TextSecondary,
    outline = SlateBorder,
    error = AlertRed,
    errorContainer = AlertRedContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF81C784),
    onPrimary = Color(0xFF003300),
    primaryContainer = Color(0xFF1B5E20),
    onPrimaryContainer = Color(0xFFC8E6C9),
    secondary = Color(0xFFFFD54F),
    background = Color(0xFF121411),
    surface = Color(0xFF1E211D),
    error = Color(0xFFEF5350)
)

@Composable
fun DairyTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our signature farm theme for consistency
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
