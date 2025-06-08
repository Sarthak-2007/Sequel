package com.personalplanner.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Define Light Color Scheme using specified colors
private val LightColors = lightColorScheme(
    primary = Orange, // Using Orange as primary, adjust if needed
    onPrimary = WhiteCardBackground, // Text/icons on primary color
    background = LightGrayBackground,
    surface = WhiteCardBackground, // Card backgrounds
    onBackground = DarkGrayText,
    onSurface = DarkGrayText, // Text on cards/surfaces
    outline = LightGrayBorder, // Borders

    // Other colors can be defaulted or customized
    // secondary = ...,
    // tertiary = ...,
)

// Define Dark Color Scheme (placeholder, customize as per Dark Mode spec if provided)
// For now, create a basic dark theme. The issue mentions "System theme support" for Dark Mode.
private val DarkColors = darkColorScheme(
    primary = Orange, // Keep Orange prominent, or choose a dark theme variant
    onPrimary = DarkGrayText,
    background = Color(0xFF121212), // Common dark background
    surface = Color(0xFF1E1E1E), // Darker card background
    onBackground = Color(0xFFE0E0E0), // Light text on dark background
    onSurface = Color(0xFFE0E0E0),
    outline = Color(0xFF424242), // Darker borders

    // Other colors
    // secondary = ...,
    // tertiary = ...,
)

@Composable
fun PersonalPlannerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb() // Use background for status bar
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography, // Use our custom typography
        shapes = Shapes, // Assuming Shapes.kt will be created or use default
        content = content
    )
}

// Placeholder for Shapes.kt if not using defaults.
// If Shapes.kt is needed, it would define Material shapes (small, medium, large).
// e.g. app/src/main/java/com/personalplanner/ui/theme/Shape.kt
// import androidx.compose.foundation.shape.RoundedCornerShape
// import androidx.compose.material3.Shapes
// import androidx.compose.ui.unit.dp
// val Shapes = Shapes(
//    small = RoundedCornerShape(4.dp),
//    medium = RoundedCornerShape(8.dp),
//    large = RoundedCornerShape(16.dp)
// )
