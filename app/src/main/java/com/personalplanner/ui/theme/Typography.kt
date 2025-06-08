package com.personalplanner.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Define custom text styles based on issue spec
val DayHeaderStyle = TextStyle(
    fontFamily = FontFamily.Default, // Replace with custom font if available
    fontWeight = FontWeight.Bold,
    fontSize = 24.sp,
    lineHeight = 28.sp,
    letterSpacing = 0.sp
)

val DateTextStyle = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.5.sp
)

val TaskTextStyle = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = 18.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.5.sp
)

// Completed task text is same as TaskTextStyle but with strikethrough, handled at component level.

// Default Material 3 Typography (can be customized)
val AppTypography = Typography(
    // Override display, headline, title, body, label as needed
    // For example, map our custom styles to Material roles if appropriate
    // This is a basic setup, can be expanded.
    headlineSmall = DayHeaderStyle, // Example mapping
    bodyLarge = TaskTextStyle,
    bodyMedium = DateTextStyle,

    // Default styles below, customize if specific needs arise
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp,
    ),
    // ... other default Material 3 styles
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
