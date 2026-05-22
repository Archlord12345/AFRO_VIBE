package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = AfroPrimaryGold,
    onPrimary = AfroBackground,
    secondary = AfroSecondaryPurple,
    onSecondary = AfroTextLight,
    tertiary = AfroAccentPink,
    background = AfroBackground,
    onBackground = AfroTextLight,
    surface = AfroSurfaceDark,
    onSurface = AfroTextLight,
    outline = AfroPurpleBorder
)

// Since AfroVibe is a short-video social network based on immersive local culture (like TikTok), 
// it strictly uses a premium dark theme across all views as shown in the mockup design specs.
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force dark theme for that immersive "AfroVibe" cinema/neon vibe
    dynamicColor: Boolean = false, // Disable dynamic colors to enforce the strict tribal brand color styling
    content: @Composable () -> Unit,
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            
            val windowInsetsController = WindowCompat.getInsetsController(window, view)
            // Since it's a dark background, we want light status/navigation bar icons
            windowInsetsController.isAppearanceLightStatusBars = false
            windowInsetsController.isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
