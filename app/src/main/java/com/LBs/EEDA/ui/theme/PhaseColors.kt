package com.LBs.EEDA.ui.theme

import androidx.compose.ui.graphics.Color
import com.LBs.EEDA.domain.model.DigitalPhase

/**
 * Sistema de paletas cromáticas por fase evolutiva.
 * Diseñado para denotar madurez progresiva visual.
 *
 * - SENSORIAL: Vibrante, cálido, amigable (niños 3-7)
 * - CREATIVA: Moderno, fresco, energético (adolescentes 8-14)
 * - PROFESIONAL: Ejecutivo, modo oscuro, sofisticado (jóvenes 15-20)
 */
data class PhaseColorPalette(
    val primary: Color,
    val primaryVariant: Color,
    val secondary: Color,
    val secondaryVariant: Color,
    val accent: Color,
    val background: Color,
    val backgroundGradientStart: Color,
    val backgroundGradientEnd: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val onPrimary: Color,
    val onSecondary: Color,
    val onBackground: Color,
    val onSurface: Color,
    val error: Color,
    val success: Color,
    val warning: Color,
    val info: Color,
    val cardBorder: Color,
    val divider: Color,
    val shimmer: Color,
    val glassOverlay: Color,
    val glassBorder: Color,
    val navBarBackground: Color,
    val navBarSelected: Color,
    val navBarUnselected: Color,
    val progressTrack: Color,
    val progressFill: Color,
    val badgeGlow: Color,
    val isDark: Boolean
)

object PhaseColors {

    val Sensorial = PhaseColorPalette(
        primary = Color(0xFFFF6B6B),           // Coral Brillante
        primaryVariant = Color(0xFFEE5253),     // Coral Profundo
        secondary = Color(0xFFFFD93D),          // Amarillo Sol
        secondaryVariant = Color(0xFFFFC107),   // Ámbar
        accent = Color(0xFF6C5CE7),             // Lavanda Juguetoña
        background = Color(0xFFFFF9F0),         // Crema Cálido
        backgroundGradientStart = Color(0xFFFFF3E0),
        backgroundGradientEnd = Color(0xFFFFECB3),
        surface = Color(0xFFFFFFFF),            // Blanco Puro
        surfaceVariant = Color(0xFFFFF0E1),     // Melocotón Suave
        onPrimary = Color.White,
        onSecondary = Color(0xFF2D1B69),
        onBackground = Color(0xFF2D1B69),       // Púrpura Oscuro
        onSurface = Color(0xFF2D1B69),
        error = Color(0xFFFF5252),
        success = Color(0xFF00C853),
        warning = Color(0xFFFFAB00),
        info = Color(0xFF448AFF),
        cardBorder = Color(0xFFFFE0B2),
        divider = Color(0xFFFFCC80),
        shimmer = Color(0xFFFFD700),
        glassOverlay = Color.White.copy(alpha = 0.85f),
        glassBorder = Color.White.copy(alpha = 0.9f),
        navBarBackground = Color(0xFFFFFFFF),
        navBarSelected = Color(0xFFFF6B6B),
        navBarUnselected = Color(0xFFBDBDBD),
        progressTrack = Color(0xFFFFE0B2),
        progressFill = Color(0xFFFF6B6B),
        badgeGlow = Color(0xFFFFD700),
        isDark = false
    )

    val Creative = PhaseColorPalette(
        primary = Color(0xFF7C4DFF),            // Púrpura Eléctrico
        primaryVariant = Color(0xFF651FFF),      // Púrpura Intenso
        secondary = Color(0xFF00BCD4),           // Cian Moderno
        secondaryVariant = Color(0xFF0097A7),    // Teal
        accent = Color(0xFFFF6E40),              // Naranja Coral
        background = Color(0xFFF0F4FF),          // Gris-Azul Claro
        backgroundGradientStart = Color(0xFFE8EAF6),
        backgroundGradientEnd = Color(0xFFE0F7FA),
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFEDE7F6),      // Lavanda Claro
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = Color(0xFF1A1A2E),        // Azul Noche
        onSurface = Color(0xFF1A1A2E),
        error = Color(0xFFFF5252),
        success = Color(0xFF00E676),
        warning = Color(0xFFFFAB00),
        info = Color(0xFF448AFF),
        cardBorder = Color(0xFFD1C4E9),
        divider = Color(0xFFB39DDB),
        shimmer = Color(0xFF7C4DFF),
        glassOverlay = Color.White.copy(alpha = 0.78f),
        glassBorder = Color.White.copy(alpha = 0.7f),
        navBarBackground = Color(0xFFFFFFFF),
        navBarSelected = Color(0xFF7C4DFF),
        navBarUnselected = Color(0xFF9E9E9E),
        progressTrack = Color(0xFFD1C4E9),
        progressFill = Color(0xFF7C4DFF),
        badgeGlow = Color(0xFF00BCD4),
        isDark = false
    )

    val Professional = PhaseColorPalette(
        primary = Color(0xFF00BFA5),             // Teal Ejecutivo
        primaryVariant = Color(0xFF00897B),       // Teal Profundo
        secondary = Color(0xFF7C4DFF),            // Púrpura Sofisticado
        secondaryVariant = Color(0xFF651FFF),
        accent = Color(0xFFFFD740),               // Oro Ejecutivo
        background = Color(0xFF0F172A),            // Charcoal Profundo
        backgroundGradientStart = Color(0xFF0F172A),
        backgroundGradientEnd = Color(0xFF1E293B),
        surface = Color(0xFF1E293B),               // Slate Oscuro
        surfaceVariant = Color(0xFF334155),         // Slate Medio
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = Color(0xFFE2E8F0),          // Platino
        onSurface = Color(0xFFE2E8F0),
        error = Color(0xFFEF5350),
        success = Color(0xFF66BB6A),
        warning = Color(0xFFFFA726),
        info = Color(0xFF42A5F5),
        cardBorder = Color(0xFF334155),
        divider = Color(0xFF475569),
        shimmer = Color(0xFF00BFA5),
        glassOverlay = Color(0xFF1E293B).copy(alpha = 0.85f),
        glassBorder = Color(0xFF475569).copy(alpha = 0.5f),
        navBarBackground = Color(0xFF1E293B),
        navBarSelected = Color(0xFF00BFA5),
        navBarUnselected = Color(0xFF64748B),
        progressTrack = Color(0xFF334155),
        progressFill = Color(0xFF00BFA5),
        badgeGlow = Color(0xFFFFD740),
        isDark = true
    )

    val Innovator = PhaseColorPalette(
        primary = Color(0xFFFF8C00),           // Atardecer Naranja
        primaryVariant = Color(0xFFE67E22),     // Calabaza
        secondary = Color(0xFF9B59B6),          // Amatista
        secondaryVariant = Color(0xFF8E44AD),
        accent = Color(0xFFF1C40F),             // Girasol
        background = Color(0xFF17202A),         // Azul Ebano
        backgroundGradientStart = Color(0xFF1C2833),
        backgroundGradientEnd = Color(0xFF212F3C),
        surface = Color(0xFF1C2833),            // Gunmetal
        surfaceVariant = Color(0xFF283747),
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = Color(0xFFFDFEFE),
        onSurface = Color(0xFFFDFEFE),
        error = Color(0xFFE74C3C),
        success = Color(0xFF2ECC71),
        warning = Color(0xFFF39C12),
        info = Color(0xFF3498DB),
        cardBorder = Color(0xFF2C3E50),
        divider = Color(0xFF34495E),
        shimmer = Color(0xFFFF8C00),
        glassOverlay = Color(0xFF1C2833).copy(alpha = 0.9f),
        glassBorder = Color(0xFF34495E).copy(alpha = 0.6f),
        navBarBackground = Color(0xFF17202A),
        navBarSelected = Color(0xFFFF8C00),
        navBarUnselected = Color(0xFF95A5A6),
        progressTrack = Color(0xFF2C3E50),
        progressFill = Color(0xFFFF8C00),
        badgeGlow = Color(0xFFF1C40F),
        isDark = true
    )

    /**
     * Obtiene la paleta de colores correspondiente a la fase.
     */
    fun forPhase(phase: DigitalPhase): PhaseColorPalette = when (phase) {
        DigitalPhase.SENSORIAL -> Sensorial
        DigitalPhase.CREATIVE -> Creative
        DigitalPhase.PROFESSIONAL -> Professional
        DigitalPhase.INNOVATOR -> Innovator
    }
}
