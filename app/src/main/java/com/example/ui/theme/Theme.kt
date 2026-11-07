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

private val DarkColorScheme = darkColorScheme(
    primary = TealLight,
    onPrimary = Color(0xFF003733),
    primaryContainer = TealDark,
    onPrimaryContainer = Color(0xFF99F6E4),
    secondary = OceanBlueLight,
    onSecondary = Color(0xFF00344F),
    secondaryContainer = Color(0xFF075985),
    onSecondaryContainer = Color(0xFFBAE6FD),
    tertiary = GoldAmber,
    onTertiary = Color(0xFF452B00),
    tertiaryContainer = Color(0xFF784D00),
    onTertiaryContainer = Color(0xFFFFDF9E),
    background = DarkBg,
    onBackground = Color(0xFFF1F5F9),
    surface = DarkSurface,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Slate400,
    outline = DarkBorder,
    error = ExpenseRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = TealDark,
    onPrimary = Color.White,
    primaryContainer = TealContainer,
    onPrimaryContainer = Color(0xFF115E59),
    secondary = OceanBlue,
    onSecondary = Color.White,
    secondaryContainer = OceanContainer,
    onSecondaryContainer = Color(0xFF0369A1),
    tertiary = GoldAmber,
    onTertiary = Color.White,
    tertiaryContainer = GoldContainer,
    onTertiaryContainer = Color(0xFFB45309),
    background = LightBg,
    onBackground = Slate900,
    surface = LightSurface,
    onSurface = Slate900,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Slate600,
    outline = Slate200,
    error = ExpenseRedDark,
    onError = Color.White
)

@Composable
fun KasKeluargaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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
