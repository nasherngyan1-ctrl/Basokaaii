package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BasokaDarkColorScheme = darkColorScheme(
  primary = NeonCyan,
  onPrimary = Color(0xFF04101A),
  primaryContainer = Color(0xFF0C2438),
  onPrimaryContainer = NeonCyan,
  secondary = ElectricViolet,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFF261845),
  onSecondaryContainer = Color(0xFFD8B4FE),
  tertiary = ElectricBlue,
  background = DarkNavyCanvas,
  onBackground = TextPrimary,
  surface = DarkSurfaceDeep,
  onSurface = TextPrimary,
  surfaceVariant = DarkSurfaceCard,
  onSurfaceVariant = TextSecondary,
  outline = DarkSurfaceBorder
)

private val BasokaLightColorScheme = lightColorScheme(
  primary = Color(0xFF0284C7),
  onPrimary = Color.White,
  primaryContainer = Color(0xFFE0F2FE),
  onPrimaryContainer = Color(0xFF0369A1),
  secondary = ElectricViolet,
  onSecondary = Color.White,
  background = Color(0xFFF8FAFC),
  onBackground = Color(0xFF0F172A),
  surface = Color.White,
  onSurface = Color(0xFF0F172A),
  surfaceVariant = Color(0xFFF1F5F9),
  onSurfaceVariant = Color(0xFF475569),
  outline = Color(0xFFCBD5E1)
)

@Composable
fun BasokaTheme(
  darkTheme: Boolean = true,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) BasokaDarkColorScheme else BasokaLightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

// Backward compatibility alias
@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  BasokaTheme(darkTheme = darkTheme, content = content)
}

