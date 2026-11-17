package com.example.ecopilot.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = EcoMossGreen,         // Botones y elementos principales
    secondary = EcoEarthBrown,      // Acentos secundarios y detalles de tierra
    tertiary = EcoForestGreen,      // Contenedores y elementos de apoyo
    background = EcoDeepBark,       // Fondo oscuro Principal de las pantallas
    surface = EcoSurfaceDark,       // Superficie de tarjetas y barras
    onPrimary = EcoTextLight,       // Color del texto dentro de los botones principales
    onSecondary = EcoTextLight,     // Color del texto sobre elementos secundarios
    onBackground = EcoTextLight,    // Texto principal sobre el fondo
    onSurface = EcoTextLight        // Texto sobre tarjetas y superficies
)

private val LightColorScheme = lightColorScheme(
    primary = EcoMossGreen,
    secondary = EcoEarthBrown,
    tertiary = EcoForestGreen,
    background = EcoTextLight,
    surface = EcoTextLight,
    onPrimary = EcoDeepBark,
    onSecondary = EcoDeepBark,
    onBackground = EcoDeepBark,
    onSurface = EcoDeepBark
)

@Composable
fun EcoPilotTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}