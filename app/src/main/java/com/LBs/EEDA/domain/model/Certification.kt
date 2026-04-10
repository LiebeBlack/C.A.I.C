package com.LBs.EEDA.domain.model

import kotlinx.serialization.Serializable

/**
 * Certificaciones internas que validan competencias adquiridas.
 * Cada certificación se desbloquea al completar un conjunto de habilidades.
 */
@Serializable
enum class CertificationType(
    val displayName: String,
    val description: String,
    val requiredPhase: DigitalPhase,
    val requiredSkillIds: List<String>,
    val badgeColor: Long, // Color en formato ARGB
    val tier: Int,
    val emoji: String
) {
    // === SENSORIAL ===
    SAFE_USER(
        displayName = "Pequeño Guardián",
        description = "Has aprendido a proteger tus secretos y pedir ayuda a los adultos",
        requiredPhase = DigitalPhase.SENSORIAL,
        requiredSkillIds = listOf("sec_01", "sec_02", "sec_03"),
        badgeColor = 0xFF00C853, // Verde
        tier = 1,
        emoji = "🛡️"
    ),
    DEVICE_MASTER_JR(
        displayName = "Explorador Táctil",
        description = "Dominas los gestos, botones y el uso saludable del dispositivo",
        requiredPhase = DigitalPhase.SENSORIAL,
        requiredSkillIds = listOf("dev_01", "dev_02", "well_01"),
        badgeColor = 0xFF1E90FF, // Azul
        tier = 1,
        emoji = "📱"
    ),
    INTERNET_VOYAGER_JR(
        displayName = "Primer Navegante",
        description = "Entiendes qué es internet y reconoces los símbolos de conexión",
        requiredPhase = DigitalPhase.SENSORIAL,
        requiredSkillIds = listOf("web_01", "web_02"),
        badgeColor = 0xFFFFD600, // Amarillo
        tier = 1,
        emoji = "🌐"
    ),

    // === CREATIVE ===
    DIGITAL_CITIZEN(
        displayName = "Ciudadano Digital",
        description = "Comprendes la responsabilidad en línea, la huella digital y el respeto",
        requiredPhase = DigitalPhase.CREATIVE,
        requiredSkillIds = listOf("cont_03", "sec_05"),
        badgeColor = 0xFF7C4DFF, // Púrpura
        tier = 2,
        emoji = "🤝"
    ),
    CREATIVE_PRODUCER(
        displayName = "Productor Creativo",
        description = "Sabes crear y editar contenido multimedia con intención",
        requiredPhase = DigitalPhase.CREATIVE,
        requiredSkillIds = listOf("cont_01", "cont_02"),
        badgeColor = 0xFFFF4081, // Rosa
        tier = 2,
        emoji = "🎨"
    ),
    LOGIC_ARCHITECT(
        displayName = "Arquitecto Lógico",
        description = "Dominas el pensamiento computacional, bucles y variables",
        requiredPhase = DigitalPhase.CREATIVE,
        requiredSkillIds = listOf("prog_01", "prog_02", "prog_03"),
        badgeColor = 0xFF00BCD4, // Cian
        tier = 2,
        emoji = "🧩"
    ),
    CYBER_DEFENDER_JR(
        displayName = "Defensor Cibernético",
        description = "Sabes protegerte del phishing y gestionar tu seguridad avanzada",
        requiredPhase = DigitalPhase.CREATIVE,
        requiredSkillIds = listOf("sec_04", "sec_06"),
        badgeColor = 0xFF43A047, // Verde bosque
        tier = 2,
        emoji = "⚔️"
    ),
    CLOUD_EXPERT(
        displayName = "Experto en la Nube",
        description = "Dominas la búsqueda eficiente y el almacenamiento en la nube",
        requiredPhase = DigitalPhase.CREATIVE,
        requiredSkillIds = listOf("web_03", "web_04"),
        badgeColor = 0xFF03A9F4, // Azul cielo
        tier = 2,
        emoji = "☁️"
    ),

    // === PROFESSIONAL ===
    AI_PRACTITIONER(
        displayName = "Practicante de IA",
        description = "Entiendes la IA, su ética y sabes aplicar ingeniería de prompts",
        requiredPhase = DigitalPhase.PROFESSIONAL,
        requiredSkillIds = listOf("ai_01", "ai_02", "ai_03", "ai_04"),
        badgeColor = 0xFFE040FB, // Magenta
        tier = 3,
        emoji = "🤖"
    ),
    DIGITAL_ECONOMIST(
        displayName = "Economista Digital",
        description = "Dominas la banca digital, la seguridad financiera y el blockchain",
        requiredPhase = DigitalPhase.PROFESSIONAL,
        requiredSkillIds = listOf("fin_01", "fin_03"),
        badgeColor = 0xFF4CAF50, // Verde dinero
        tier = 3,
        emoji = "📊"
    ),
    PROFESSIONAL_LEADER(
        displayName = "Líder Profesional",
        description = "Dominas la suite ofimática, marca personal y gestión de proyectos",
        requiredPhase = DigitalPhase.PROFESSIONAL,
        requiredSkillIds = listOf("prod_01", "prod_02", "prod_03", "prod_04"),
        badgeColor = 0xFFFFD700, // Dorado
        tier = 3,
        emoji = "🌟"
    ),
    DATA_STRATEGIST(
        displayName = "Estratega de Datos",
        description = "Capacidad para analizar y visualizar información para la toma de decisiones",
        requiredPhase = DigitalPhase.PROFESSIONAL,
        requiredSkillIds = listOf("dat_01", "dat_02"),
        badgeColor = 0xFFFF9800, // Naranja
        tier = 3,
        emoji = "📈"
    ),
    DIGITAL_RIGHTS_EXPERT(
        displayName = "Experto en Derechos",
        description = "Dominas la privacidad de datos, GDPR y derechos digitales",
        requiredPhase = DigitalPhase.PROFESSIONAL,
        requiredSkillIds = listOf("sec_08"),
        badgeColor = 0xFFFF5252, // Rojo
        tier = 3,
        emoji = "⚖️"
    )
}

/**
 * Registro de una certificación obtenida por el usuario.
 */
@Serializable
data class EarnedCertification(
    val type: CertificationType,
    val earnedAt: String,
    val scorePercent: Int = 100
)
