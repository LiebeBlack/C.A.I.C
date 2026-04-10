package com.LBs.EEDA.domain.model

import kotlinx.serialization.Serializable

/**
 * Configuración del Modo Acompañante (Padres/Tutores).
 * Versión 2.0: Soporta controles de tiempo, filtros de contenido y bitácora de actividad.
 */
@Serializable
data class ParentConfig(
    val isEnabled: Boolean = false,
    val parentPin: String = "1234",
    val dailyTimeLimitMinutes: Int = 60,
    val weeklyReportEnabled: Boolean = true,
    val contentFilterLevel: ContentFilterLevel = ContentFilterLevel.MODERATE,
    val progressNotifications: Boolean = true,
    val activityLog: List<ActivityEntry> = emptyList(),
    val restrictedAppIds: List<String> = emptyList(),
    val lastReportDate: String? = null
) {
    fun isAccessible(pin: String): Boolean = parentPin == pin
}

@Serializable
enum class ContentFilterLevel(val displayName: String, val description: String) {
    STRICT("Estricto", "Solo contenido educativo básico y esencial."),
    MODERATE("Moderado", "Contenido educativo con herramientas creativas."),
    OPEN("Abierto", "Acceso total a todas las funcionalidades de la fase.")
}

@Serializable
data class ActivityEntry(
    val timestamp: String,
    val action: String,
    val category: String,
    val durationMinutes: Int = 0
)
