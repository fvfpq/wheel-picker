package com.example.wheelpicker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFFE64A19),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFCCBC),
    onPrimaryContainer = Color(0xFF331006),
    secondary = Color(0xFF3F51B5),
    background = Color(0xFFFFF8F3),
    surface = Color(0xFFFFFFFF),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFFF8A65),
    onPrimary = Color(0xFF4E1500),
    primaryContainer = Color(0xFF7B2E12),
    onPrimaryContainer = Color(0xFFFFDBCF),
    secondary = Color(0xFF7986CB),
)

@Composable
fun WheelPickerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
