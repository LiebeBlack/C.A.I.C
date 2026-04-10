package com.LBs.EEDA.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.LBs.EEDA.domain.model.DigitalPhase

/**
 * Sistema de tipografía adaptativa por fase.
 *
 * - SENSORIAL: Grande, redondeada, cálida (niños 3-7)
 * - CREATIVA: Media, geométrica, moderna (adolescentes 8-14)
 * - PROFESIONAL: Compacta, profesional, eficiente (jóvenes 15-20)
 */
data class PhaseTypographyConfig(
    val typography: Typography,
    val borderRadius: Float,     // dp
    val elevation: Float,        // dp
    val iconSize: Float,         // dp
    val buttonHeight: Float,     // dp
    val cardPadding: Float       // dp
)

object PhaseTypography {

    /**
     * Tipografía para la fase Sensorial (3-7 años).
     * Nunito/Similar: Redondeada, amigable, grande para desarrollar lectura.
     */
    val Sensorial = PhaseTypographyConfig(
        typography = Typography(
            displayLarge = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Black,
                fontSize = 52.sp,
                lineHeight = 60.sp,
                letterSpacing = (-1.5).sp
            ),
            displayMedium = TextStyle(
                fontWeight = FontWeight.Black,
                fontSize = 40.sp,
                lineHeight = 48.sp,
                letterSpacing = (-0.5).sp
            ),
            displaySmall = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 34.sp,
                lineHeight = 42.sp
            ),
            headlineLarge = TextStyle(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp,
                lineHeight = 40.sp
            ),
            headlineMedium = TextStyle(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp,
                lineHeight = 36.sp
            ),
            headlineSmall = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                lineHeight = 32.sp
            ),
            titleLarge = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                lineHeight = 28.sp
            ),
            titleMedium = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                lineHeight = 26.sp
            ),
            titleSmall = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                lineHeight = 24.sp
            ),
            bodyLarge = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                lineHeight = 28.sp
            ),
            bodyMedium = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                lineHeight = 26.sp
            ),
            bodySmall = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 22.sp
            ),
            labelLarge = TextStyle(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                lineHeight = 24.sp,
                letterSpacing = 1.1.sp
            ),
            labelMedium = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.5.sp
            ),
            labelSmall = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                lineHeight = 18.sp
            )
        ),
        borderRadius = 28f,
        elevation = 8f,
        iconSize = 32f,
        buttonHeight = 72f,
        cardPadding = 20f
    )

    /**
     * Tipografía para la fase Creativa (8-14 años).
     * Outfit/Similar: Geométrica, moderna, equilibrada.
     */
    val Creative = PhaseTypographyConfig(
        typography = Typography(
            displayLarge = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold,
                fontSize = 44.sp,
                lineHeight = 52.sp,
                letterSpacing = (-1.5).sp
            ),
            displayMedium = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp,
                lineHeight = 44.sp,
                letterSpacing = (-0.5).sp
            ),
            displaySmall = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 30.sp,
                lineHeight = 38.sp
            ),
            headlineLarge = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                lineHeight = 36.sp
            ),
            headlineMedium = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                lineHeight = 32.sp
            ),
            headlineSmall = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                lineHeight = 28.sp
            ),
            titleLarge = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                lineHeight = 26.sp
            ),
            titleMedium = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                lineHeight = 24.sp
            ),
            titleSmall = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                lineHeight = 22.sp
            ),
            bodyLarge = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp
            ),
            bodyMedium = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp
            ),
            bodySmall = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                lineHeight = 18.sp
            ),
            labelLarge = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                letterSpacing = 0.8.sp
            ),
            labelMedium = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                letterSpacing = 0.4.sp
            ),
            labelSmall = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        ),
        borderRadius = 16f,
        elevation = 4f,
        iconSize = 24f,
        buttonHeight = 56f,
        cardPadding = 16f
    )

    /**
     * Tipografía para la fase Profesional (15-20 años).
     * Inter/Similar: Limpia, compacta, profesional.
     */
    val Professional = PhaseTypographyConfig(
        typography = Typography(
            displayLarge = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.SemiBold,
                fontSize = 36.sp,
                lineHeight = 44.sp,
                letterSpacing = (-1).sp
            ),
            displayMedium = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 30.sp,
                lineHeight = 38.sp,
                letterSpacing = (-0.5).sp
            ),
            displaySmall = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 26.sp,
                lineHeight = 34.sp
            ),
            headlineLarge = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                lineHeight = 32.sp
            ),
            headlineMedium = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                lineHeight = 28.sp
            ),
            headlineSmall = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                lineHeight = 24.sp
            ),
            titleLarge = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                lineHeight = 24.sp
            ),
            titleMedium = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                lineHeight = 22.sp
            ),
            titleSmall = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp
            ),
            bodyLarge = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 22.sp
            ),
            bodyMedium = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                lineHeight = 20.sp
            ),
            bodySmall = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 18.sp
            ),
            labelLarge = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.5.sp
            ),
            labelMedium = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.3.sp
            ),
            labelSmall = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                lineHeight = 14.sp
            )
        ),
        borderRadius = 8f,
        elevation = 2f,
        iconSize = 20f,
        buttonHeight = 48f,
        cardPadding = 12f
    )
    
    /**
     * Tipografía para la fase Innovador (21+ años).
     * Enfoque: Liderazgo, claridad y arquitectura.
     */
    val Innovator = Professional.copy(
        borderRadius = 4f,
        buttonHeight = 52f
    )

    fun forPhase(phase: DigitalPhase): PhaseTypographyConfig = when (phase) {
        DigitalPhase.SENSORIAL -> Sensorial
        DigitalPhase.CREATIVE -> Creative
        DigitalPhase.PROFESSIONAL -> Professional
        DigitalPhase.INNOVATOR -> Innovator
    }
}
