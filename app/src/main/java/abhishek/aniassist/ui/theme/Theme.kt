package abhishek.aniassist.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary          = Forest,
    onPrimary        = Color.White,
    primaryContainer = Color(0xFFD9EBE1),
    secondary        = ForestLight,
    onSecondary      = Color.White,
    tertiary         = Amber,
    background       = Cream,
    surface          = Cream,
    surfaceVariant   = CreamDark,
    onBackground     = Ink,
    onSurface        = Ink,
    error            = Color(0xFFB00020)
)

private val DarkColorScheme = darkColorScheme(
    primary          = Green80,
    onPrimary        = Green20,
    primaryContainer = Green40,
    secondary        = GreenGrey80,
    onSecondary      = Green10,
    tertiary         = GreenGrey80,
    background       = Green10,
    surface          = Green10
)

@Composable
fun AniassistTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // App is designed light-only — the dark palette is a legacy stub whose
    // onPrimary breaks button contrast (green text on green). Force light.
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
