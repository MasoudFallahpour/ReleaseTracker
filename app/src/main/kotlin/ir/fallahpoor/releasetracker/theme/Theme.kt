package ir.fallahpoor.releasetracker.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private object ColorPalette {
    val Magenta = Color(0xff6200EE)
    val Blue = Color(0xff3700B3)
    val Teal = Color(0xff03DAC5)
    val DarkRed = Color(0xffF44336)
    val White = Color(0xffEEEEEE)
    val Black = Color(0xff212121)
}

private val lightColors: Colors = lightColors(
    primary = ColorPalette.Magenta,
    primaryVariant = ColorPalette.Blue,
    onPrimary = ColorPalette.White,
    secondary = ColorPalette.Teal,
    onSecondary = ColorPalette.Black,
    error = ColorPalette.DarkRed
)

private val darkColors: Colors = darkColors(
    primary = ColorPalette.Teal,
    primaryVariant = ColorPalette.Black,
    onPrimary = ColorPalette.Black,
    secondary = ColorPalette.Teal,
    onSecondary = ColorPalette.Black,
    error = ColorPalette.DarkRed
)

data class Spacing(
    val default: Dp = 0.dp,
    val small: Dp = 8.dp,
    val normal: Dp = 16.dp
)

private val LocalSpacing = compositionLocalOf { Spacing() }

val MaterialTheme.spacing: Spacing
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacing.current

@Composable
fun ReleaseTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalSpacing provides Spacing()) {
        MaterialTheme(
            colors = if (darkTheme) darkColors else lightColors,
            content = content
        )
    }
}