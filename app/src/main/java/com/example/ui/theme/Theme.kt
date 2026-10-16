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
    primary = PrimaryBlueLight,
    onPrimary = Color.Black,
    primaryContainer = DarkKeyOperator,
    onPrimaryContainer = Color.White,
    secondary = AccentCyan,
    onSecondary = Color.Black,
    secondaryContainer = Slate800,
    onSecondaryContainer = Color.White,
    tertiary = AccentEmerald,
    onTertiary = Color.Black,
    tertiaryContainer = EqualColor,
    onTertiaryContainer = Color.White,
    background = DarkBg,
    onBackground = Color.White,
    surface = DarkSurface,
    onSurface = Color.White,
    surfaceVariant = Slate800,
    onSurfaceVariant = Color(0xFF94A3B8),
    error = FunctionColorDark,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = LightKeyOperator,
    onPrimaryContainer = PrimaryBlue,
    secondary = AccentTeal,
    onSecondary = Color.White,
    secondaryContainer = LightKeyNumber,
    onSecondaryContainer = Slate900,
    tertiary = EqualColor,
    onTertiary = Color.White,
    tertiaryContainer = EqualColor,
    onTertiaryContainer = Color.White,
    background = LightBg,
    onBackground = Slate900,
    surface = LightSurface,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Color(0xFF64748B),
    error = FunctionColorLight,
    onError = Color.White
)

@Composable
fun CalculatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent tailored calculator colors by default
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
