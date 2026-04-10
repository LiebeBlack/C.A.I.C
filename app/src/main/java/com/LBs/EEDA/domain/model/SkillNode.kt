package com.LBs.EEDA.domain.model

import kotlinx.serialization.Serializable

/**
 * Categorías del Árbol de Habilidades Digitales.
 */
@Serializable
enum class SkillCategory(val displayName: String, val icon: String) {
    SECURITY("Seguridad Digital", "shield"),
    DEVICE_TOOLS("Herramientas del Dispositivo", "phone_android"),
    WEB_NAVIGATION("Navegación y Web", "language"),
    PROGRAMMING("Programación y Lógica", "code"),
    CONTENT_CREATION("Creación de Contenido", "palette"),
    AI_TECH("IA y Tecnologías Emergentes", "smart_toy"),
    DIGITAL_FINANCE("Finanzas Digitales", "account_balance"),
    PRODUCTIVITY("Productividad", "dashboard"),
    COLLABORATION("Colaboración Digital", "groups"),
    COMMUNICATION("Comunicación Digital", "chat"),
    DATA_LITERACY("Cultura de Datos", "bar_chart"),
    WELLBEING("Bienestar Digital", "self_improvement"),
    INNOVATION("Innovación y Futuro", "rocket_launch"),
    ETHICS("Ética y Filosofía Digital", "balance")
}

/**
 * Un nodo individual dentro del Árbol de Habilidades Digitales.
 * Representa una competencia específica que el usuario puede desbloquear.
 */
@Serializable
data class SkillNode(
    val id: String,
    val name: String,
    val description: String,
    val category: SkillCategory,
    val requiredPhase: DigitalPhase,
    val xpRequired: Int = 100,
    val xpEarned: Int = 0,
    val isUnlocked: Boolean = false,
    val isCompleted: Boolean = false,
    val prerequisiteIds: List<String> = emptyList(),
    val tier: Int = 1 // 1-5, nivel de profundidad dentro de la categoría
) {
    val progressPercent: Float
        get() = if (xpRequired > 0) (xpEarned.toFloat() / xpRequired).coerceIn(0f, 1f) else 0f
}

/**
 * Árbol completo de habilidades con datos predefinidos por fase.
 */
object SkillTreeData {

    fun getDefaultSkillTree(): List<SkillNode> = buildList {
        // === FASE SENSORIAL (3-7 años) - Enfoque: Exploración e Intuición ===
        // Nivel 3-5 años: Motor y Sensorial
        add(SkillNode("dev_01", "Toque y Gesto", "Domina los gestos básicos: tocar, deslizar, pellizcar", SkillCategory.DEVICE_TOOLS, DigitalPhase.SENSORIAL, 40, tier = 1))
        add(SkillNode("dev_02", "Mis Botones", "Identifica los botones de inicio, volumen y bloqueo", SkillCategory.DEVICE_TOOLS, DigitalPhase.SENSORIAL, 50, tier = 1))
        add(SkillNode("well_01", "La Pausa Mágica", "Aprende cuándo soltar la tableta para jugar fuera", SkillCategory.WELLBEING, DigitalPhase.SENSORIAL, 40, tier = 1))
        
        // Nivel 6-7 años: Reglas y Seguridad Básica
        add(SkillNode("sec_01", "Contraseña Mágica", "Aprende qué es una contraseña y por qué es secreta", SkillCategory.SECURITY, DigitalPhase.SENSORIAL, 50, tier = 1))
        add(SkillNode("sec_02", "Extraños en la Pantalla", "Identifica cuándo alguien desconocido intenta hablar contigo", SkillCategory.SECURITY, DigitalPhase.SENSORIAL, 60, tier = 1, prerequisiteIds = listOf("sec_01")))
        add(SkillNode("sec_03", "Pedir Ayuda", "Sabe cuándo pedirle ayuda a un adulto de confianza", SkillCategory.SECURITY, DigitalPhase.SENSORIAL, 50, tier = 1, prerequisiteIds = listOf("sec_02")))
        add(SkillNode("web_01", "¿Qué es Internet?", "Comprende Internet como una red de información", SkillCategory.WEB_NAVIGATION, DigitalPhase.SENSORIAL, 60, tier = 1))
        add(SkillNode("web_02", "Iconos Mágicos", "Reconoce iconos comunes: WiFi, batería, cámara", SkillCategory.WEB_NAVIGATION, DigitalPhase.SENSORIAL, 40, tier = 1))

        // === FASE CREATIVA (8-14 años) - Enfoque: Creación y Pensamiento Crítico ===
        // Nivel 8-10 años: Herramientas Creativas y Lógica
        add(SkillNode("cont_01", "Foto y Edición", "Toma fotos y aplica ediciones básicas", SkillCategory.CONTENT_CREATION, DigitalPhase.CREATIVE, 80, tier = 2))
        add(SkillNode("prog_01", "Pensamiento Lógico", "Descomponer problemas en pasos simples", SkillCategory.PROGRAMMING, DigitalPhase.CREATIVE, 80, tier = 2))
        add(SkillNode("prog_02", "Secuencias y Bucles", "Instrucciones paso a paso y repeticiones", SkillCategory.PROGRAMMING, DigitalPhase.CREATIVE, 100, tier = 2, prerequisiteIds = listOf("prog_01")))
        add(SkillNode("sec_04", "Ciberseguridad Personal", "Phishing, malware y protección de identidad", SkillCategory.SECURITY, DigitalPhase.CREATIVE, 100, tier = 2, prerequisiteIds = listOf("sec_03")))
        
        // Nivel 11-12 años: Ciudadanía y Colaboración
        add(SkillNode("cont_03", "Ciudadanía Digital", "Respeto, empatía y responsabilidad en línea", SkillCategory.CONTENT_CREATION, DigitalPhase.CREATIVE, 80, tier = 2))
        add(SkillNode("col_01", "Trabajo en Equipo Online", "Usa herramientas para crear cosas con amigos", SkillCategory.COLLABORATION, DigitalPhase.CREATIVE, 90, tier = 2))
        add(SkillNode("sec_05", "Huella Digital", "Entiende que todo lo que publicas deja rastro", SkillCategory.SECURITY, DigitalPhase.CREATIVE, 80, tier = 2))
        add(SkillNode("web_04", "Búsqueda Eficiente", "Usa motores de búsqueda de forma efectiva y crítica", SkillCategory.WEB_NAVIGATION, DigitalPhase.CREATIVE, 80, tier = 2))
        
        // Nivel 13-14 años: Producción Avanzada
        add(SkillNode("cont_02", "Video Creativo", "Graba y edita videos cortos con mensaje", SkillCategory.CONTENT_CREATION, DigitalPhase.CREATIVE, 100, tier = 2, prerequisiteIds = listOf("cont_01")))
        add(SkillNode("prog_03", "Variables y Datos", "Almacenar y transformar información", SkillCategory.PROGRAMMING, DigitalPhase.CREATIVE, 120, tier = 3, prerequisiteIds = listOf("prog_02")))
        add(SkillNode("web_03", "La Nube", "Almacenar, sincronizar y compartir archivos en la nube", SkillCategory.WEB_NAVIGATION, DigitalPhase.CREATIVE, 100, tier = 2, prerequisiteIds = listOf("web_01", "web_04")))
        add(SkillNode("sec_06", "Gestión de Contraseñas", "Uso de gestores y autenticación de doble factor", SkillCategory.SECURITY, DigitalPhase.CREATIVE, 100, tier = 3, prerequisiteIds = listOf("sec_04")))

        // === FASE PROFESIONAL (15-20 años) - Enfoque: Productividad y Tecnologías Emergentes ===
        // Nivel 15-16 años: IA y Productividad
        add(SkillNode("ai_01", "¿Qué es la IA?", "Fundamentos y conceptos básicos de IA", SkillCategory.AI_TECH, DigitalPhase.PROFESSIONAL, 120, tier = 3))
        add(SkillNode("prod_01", "Suite Ofimática", "Documentos, hojas de cálculo y presentaciones pro", SkillCategory.PRODUCTIVITY, DigitalPhase.PROFESSIONAL, 100, tier = 3))
        add(SkillNode("fin_01", "Banca Digital", "Cuentas, transferencias y seguridad financiera", SkillCategory.DIGITAL_FINANCE, DigitalPhase.PROFESSIONAL, 120, tier = 3))
        add(SkillNode("dat_01", "Análisis de Datos", "Interpreta gráficos e información compleja", SkillCategory.DATA_LITERACY, DigitalPhase.PROFESSIONAL, 130, tier = 4))
        
        // Nivel 17-18 años: Identidad y Ética
        add(SkillNode("prod_03", "Identidad Profesional", "LinkedIn, portafolio digital y marca personal", SkillCategory.PRODUCTIVITY, DigitalPhase.PROFESSIONAL, 120, tier = 4))
        add(SkillNode("ai_02", "IA Aplicada", "Herramientas de IA para productividad real", SkillCategory.AI_TECH, DigitalPhase.PROFESSIONAL, 150, tier = 4, prerequisiteIds = listOf("ai_01")))
        add(SkillNode("ai_03", "Ética de Datos", "Privacidad, sesgo algorítmico y responsabilidad", SkillCategory.AI_TECH, DigitalPhase.PROFESSIONAL, 100, tier = 4, prerequisiteIds = listOf("ai_01")))
        add(SkillNode("sec_08", "Derechos Digitales", "GDPR, privacidad y protección de información", SkillCategory.SECURITY, DigitalPhase.PROFESSIONAL, 100, tier = 4, prerequisiteIds = listOf("sec_06")))
        
        // Nivel 19-20 años: Especialización y Automatización
        add(SkillNode("fin_03", "Criptomonedas y Blockchain", "Blockchain y monedas digitales", SkillCategory.DIGITAL_FINANCE, DigitalPhase.PROFESSIONAL, 150, tier = 4, prerequisiteIds = listOf("fin_01")))
        add(SkillNode("prod_02", "Gestión de Proyectos", "Metodologías ágiles y herramientas de equipo", SkillCategory.PRODUCTIVITY, DigitalPhase.PROFESSIONAL, 120, tier = 4, prerequisiteIds = listOf("prod_01")))
        add(SkillNode("prod_04", "Automatización", "No-code y automatización de procesos", SkillCategory.PRODUCTIVITY, DigitalPhase.PROFESSIONAL, 150, tier = 5, prerequisiteIds = listOf("prod_02")))
        add(SkillNode("ai_04", "Prompt Engineering", "Optimización de consultas para modelos de lenguaje", SkillCategory.AI_TECH, DigitalPhase.PROFESSIONAL, 130, tier = 5, prerequisiteIds = listOf("ai_02")))
        add(SkillNode("dat_02", "Visualización Avanzada", "Storytelling con datos", SkillCategory.DATA_LITERACY, DigitalPhase.PROFESSIONAL, 140, tier = 5, prerequisiteIds = listOf("dat_01")))

        // === FASE INNOVADOR (21+ años) - Enfoque: Liderazgo y Arquitectura ===
        add(SkillNode("inn_01", "Ideación y Prototipado", "Creación de productos digitales desde cero", SkillCategory.INNOVATION, DigitalPhase.INNOVATOR, 160, tier = 1))
        add(SkillNode("inn_02", "Arquitectura de Sistemas", "Diseño de soluciones escalables", SkillCategory.INNOVATION, DigitalPhase.INNOVATOR, 180, tier = 2, prerequisiteIds = listOf("inn_01")))
        add(SkillNode("eth_01", "Filosofía del Algoritmo", "Impacto social de la IA", SkillCategory.ETHICS, DigitalPhase.INNOVATOR, 150, tier = 1))
        add(SkillNode("inn_03", "Liderazgo Distribuido", "Gestión de equipos remotos y globales", SkillCategory.COLLABORATION, DigitalPhase.INNOVATOR, 170, tier = 3))
        add(SkillNode("inn_04", "Economía de la Innovación", "Modelos de negocio disruptivos", SkillCategory.DIGITAL_FINANCE, DigitalPhase.INNOVATOR, 190, tier = 4, prerequisiteIds = listOf("inn_02")))
    }
}
