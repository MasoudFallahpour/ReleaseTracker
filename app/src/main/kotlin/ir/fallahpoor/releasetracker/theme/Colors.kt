package ir.fallahpoor.releasetracker.theme

import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

private object ColorPalette {
    val Magenta = Color(0xff6200EE)
    val Blue = Color(0xff3700B3)
    val Teal = Color(0xff03DAC5)
    val DarkRed = Color(0xffF44336)
    val White = Color(0xffEEEEEE)
    val Black = Color(0xff212121)
}

val lightColors: Colors = lightColors(
    primary = ColorPalette.Magenta,
    primaryVariant = ColorPalette.Blue,
    onPrimary = ColorPalette.White,
    secondary = ColorPalette.Teal,
    onSecondary = ColorPalette.Black,
    error = ColorPalette.DarkRed
)

val darkColors: Colors = darkColors(
    primary = ColorPalette.Teal,
    primaryVariant = ColorPalette.Black,
    onPrimary = ColorPalette.Black,
    secondary = ColorPalette.Teal,
    onSecondary = ColorPalette.Black,
    error = ColorPalette.DarkRed
)