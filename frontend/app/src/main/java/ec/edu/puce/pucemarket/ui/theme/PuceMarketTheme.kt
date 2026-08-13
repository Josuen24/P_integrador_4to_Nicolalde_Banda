package ec.edu.puce.pucemarket.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object PuceColors {
    val Blue = Color(0xFF003B70)
    val BlueLight = Color(0xFFEAF3FB)
    val Gold = Color(0xFFF2B233)
    val Ink = Color(0xFF172B3A)
    val Background = Color(0xFFF5F9FD)
    val Surface = Color.White
    val WhatsApp = Color(0xFF1FAF5A)
}

@Composable
fun PuceMarketTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = PuceColors.Blue,
            onPrimary = Color.White,
            primaryContainer = PuceColors.BlueLight,
            onPrimaryContainer = PuceColors.Blue,
            secondary = PuceColors.Gold,
            onSecondary = PuceColors.Ink,
            secondaryContainer = Color(0xFFFFF0C9),
            background = PuceColors.Background,
            onBackground = PuceColors.Ink,
            surface = PuceColors.Surface,
            onSurface = PuceColors.Ink,
            surfaceVariant = Color(0xFFE8F0F7),
            onSurfaceVariant = Color(0xFF405564),
            error = Color(0xFFB3261E),
        ),
        content = content,
    )
}
