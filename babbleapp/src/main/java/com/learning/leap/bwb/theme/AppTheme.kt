package com.learning.leap.bwb.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val LightColorPalette = lightColors(
    primary = PrimaryColor,
    secondary = DarkBlue,
    primaryVariant = DarkGreen,
    onSecondary = DarkBlue,
    secondaryVariant = DarkBlue,
    surface = DarkGreen,

)

@Composable
fun BabbleTheme(
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colors = LightColorPalette,
        typography = typography,
        content = content
    )
}