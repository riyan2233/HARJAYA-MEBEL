package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun HarjayaMebelTheme(
  themeIndex: Int = 0,
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val lightColors = when (themeIndex) {
    1 -> lightColorScheme(
      primary = EmeraldPrimary,
      onPrimary = Color.White,
      primaryContainer = Color(0xFFD5E8D4),
      onPrimaryContainer = Color(0xFF0F3818),
      secondary = EmeraldSecondary,
      tertiary = EmeraldAccent,
      background = EmeraldCreamBg,
      surface = Color.White,
      onBackground = Color(0xFF1B241C),
      onSurface = Color(0xFF1B241C)
    )
    2 -> lightColorScheme(
      primary = MahoganyPrimary,
      onPrimary = Color.White,
      primaryContainer = Color(0xFFFADBD8),
      onPrimaryContainer = Color(0xFF4A1213),
      secondary = MahoganySecondary,
      tertiary = MahoganyAccent,
      background = MahoganyCreamBg,
      surface = Color.White,
      onBackground = Color(0xFF2B1B1C),
      onSurface = Color(0xFF2B1B1C)
    )
    3 -> lightColorScheme(
      primary = NavyPrimary,
      onPrimary = Color.White,
      primaryContainer = Color(0xFFD6E4F0),
      onPrimaryContainer = Color(0xFF0F1E33),
      secondary = NavySecondary,
      tertiary = NavyAccent,
      background = NavyCreamBg,
      surface = Color.White,
      onBackground = Color(0xFF161E2E),
      onSurface = Color(0xFF161E2E)
    )
    else -> lightColorScheme(
      primary = TeakGoldPrimary,
      onPrimary = Color.White,
      primaryContainer = Color(0xFFF6E8DC),
      onPrimaryContainer = Color(0xFF5D320B),
      secondary = TeakGoldSecondary,
      tertiary = TeakGoldAccent,
      background = TeakCreamBg,
      surface = Color.White,
      onBackground = Color(0xFF2C2219),
      onSurface = Color(0xFF2C2219)
    )
  }

  val darkColors = darkColorScheme(
    primary = when (themeIndex) {
      1 -> Color(0xFF81C784)
      2 -> Color(0xFFE57373)
      3 -> Color(0xFF64B5F6)
      else -> Color(0xFFFFB74D)
    },
    background = Color(0xFF181411),
    surface = Color(0xFF241D18),
    onBackground = Color(0xFFF5EFEB),
    onSurface = Color(0xFFF5EFEB)
  )

  MaterialTheme(
    colorScheme = if (darkTheme) darkColors else lightColors,
    typography = Typography,
    content = content
  )
}
