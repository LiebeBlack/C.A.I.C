package com.LBs.EEDA.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Lesson(
    val id: String,
    val title: String,
    val description: String,
    val content: String,
    val category: SkillCategory,
    val phase: DigitalPhase,
    val estimatedMinutes: Int,
    val xpReward: Long
)

object LessonData {
    fun getLessons(): List<Lesson> = listOf(
        // ═══════════════════════════════════════════════════════════════
        // FASE SENSORIAL (3-7 años) - Fundamentos Digitales Básicos
        // ═══════════════════════════════════════════════════════════════
        Lesson(
            id = "lesson_sens_01",
            title = "¡Hola, Tableta!",
            description = "Tus primeros pasos con el dispositivo.",
            content = "Aprende a encender, apagar y usar los dedos para navegar de forma segura. Conoce los botones básicos y cómo cuidar tu dispositivo.",
            category = SkillCategory.DEVICE_TOOLS,
            phase = DigitalPhase.SENSORIAL,
            estimatedMinutes = 5,
            xpReward = 100
        ),
        Lesson(
            id = "lesson_sens_02",
            title = "El Semáforo de Internet",
            description = "Identifica sitios seguros.",
            content = "Verde para juegos conocidos, Amarillo para dudas, Rojo para extraños. Aprende a reconocer sitios web seguros y cuándo pedir ayuda a un adulto.",
            category = SkillCategory.SECURITY,
            phase = DigitalPhase.SENSORIAL,
            estimatedMinutes = 10,
            xpReward = 150
        ),
        Lesson(
            id = "lesson_sens_03",
            title = "Los Colores del Tacto",
            description = "Descubre tu pantalla.",
            content = "Toca, desliza, pellizca. Aprende los gestos básicos para interactuar con aplicaciones de manera natural y divertida.",
            category = SkillCategory.DEVICE_TOOLS,
            phase = DigitalPhase.SENSORIAL,
            estimatedMinutes = 8,
            xpReward = 120
        ),
        Lesson(
            id = "lesson_sens_04",
            title = "Mi Primera Contraseña Secreta",
            description = "Protege tu espacio digital.",
            content = "Crea una contraseña fácil de recordar pero difícil de adivinar usando dibujos o patrones que solo tú conozcas.",
            category = SkillCategory.SECURITY,
            phase = DigitalPhase.SENSORIAL,
            estimatedMinutes = 12,
            xpReward = 180
        ),
        Lesson(
            id = "lesson_sens_05",
            title = "El Mundo de los Emoji",
            description = "Expresiones digitales.",
            content = "Descubre cómo las caritas y símbolos pueden expresar emociones en mensajes digitales de forma divertida.",
            category = SkillCategory.COMMUNICATION,
            phase = DigitalPhase.SENSORIAL,
            estimatedMinutes = 7,
            xpReward = 130
        ),
        Lesson(
            id = "lesson_sens_06",
            title = "Guardianes del Tiempo",
            description = "Balance digital saludable.",
            content = "Aprende a usar temporizadores visuales para equilibrar tiempo de pantalla con juegos al aire libre.",
            category = SkillCategory.DEVICE_TOOLS,
            phase = DigitalPhase.SENSORIAL,
            estimatedMinutes = 10,
            xpReward = 160
        ),

        // ═══════════════════════════════════════════════════════════════
        // FASE CREATIVA (8-14 años) - Expresión y Navegación Digital
        // ═══════════════════════════════════════════════════════════════
        Lesson(
            id = "lesson_creat_01",
            title = "Tu Primera Obra de Arte Digital",
            description = "Usa herramientas de dibujo.",
            content = "Explora capas, pinceles y colores para expresar tu creatividad. Crea composiciones digitales profesionales desde cero.",
            category = SkillCategory.CONTENT_CREATION,
            phase = DigitalPhase.CREATIVE,
            estimatedMinutes = 20,
            xpReward = 300
        ),
        Lesson(
            id = "lesson_creat_02",
            title = "Detective de Noticias Falsas",
            description = "No todo lo que brilla es oro.",
            content = "Cómo verificar información y no caer en engaños en redes sociales. Desarrolla pensamiento crítico digital.",
            category = SkillCategory.WEB_NAVIGATION,
            phase = DigitalPhase.CREATIVE,
            estimatedMinutes = 15,
            xpReward = 250
        ),
        Lesson(
            id = "lesson_creat_03",
            title = "Edición de Video para Jóvenes",
            description = "Corta, une, añade música.",
            content = "Fundamentos de edición de video: cortes, transiciones, efectos básicos y exportación para redes sociales.",
            category = SkillCategory.CONTENT_CREATION,
            phase = DigitalPhase.CREATIVE,
            estimatedMinutes = 25,
            xpReward = 350
        ),
        Lesson(
            id = "lesson_creat_04",
            title = "Redes Sociales con Responsabilidad",
            description = "Tu huella digital.",
            content = "Entiende cómo tus acciones online te representan. Configura privacidad y piensa antes de publicar.",
            category = SkillCategory.SECURITY,
            phase = DigitalPhase.CREATIVE,
            estimatedMinutes = 18,
            xpReward = 280
        ),
        Lesson(
            id = "lesson_creat_05",
            title = "Música Digital Básica",
            description = "Crea tus primeros beats.",
            content = "Introducción a DAWs simples, loops, ritmos básicos y composición musical digital para principiantes.",
            category = SkillCategory.CONTENT_CREATION,
            phase = DigitalPhase.CREATIVE,
            estimatedMinutes = 22,
            xpReward = 320
        ),
        Lesson(
            id = "lesson_creat_06",
            title = "Navegación Web Eficiente",
            description = "Busca como un experto.",
            content = "Operadores de búsqueda avanzada, evaluación de fuentes, bookmarking inteligente y organización de información.",
            category = SkillCategory.WEB_NAVIGATION,
            phase = DigitalPhase.CREATIVE,
            estimatedMinutes = 16,
            xpReward = 260
        ),
        Lesson(
            id = "lesson_creat_07",
            title = "Diseño Gráfico Fundamental",
            description = "Color, forma y composición.",
            content = "Principios básicos de diseño: teoría del color, tipografía, jerarquía visual y herramientas Canva/Figma.",
            category = SkillCategory.CONTENT_CREATION,
            phase = DigitalPhase.CREATIVE,
            estimatedMinutes = 28,
            xpReward = 380
        ),
        Lesson(
            id = "lesson_creat_08",
            title = "Ciberseguridad para Adolescentes",
            description = "Protege tu identidad digital.",
            content = "Contraseñas seguras, autenticación de dos factores, reconocimiento de phishing y privacidad online.",
            category = SkillCategory.SECURITY,
            phase = DigitalPhase.CREATIVE,
            estimatedMinutes = 20,
            xpReward = 300
        ),

        // ═══════════════════════════════════════════════════════════════
        // FASE PROFESIONAL (15-20 años) - Habilidades Avanzadas
        // ═══════════════════════════════════════════════════════════════
        Lesson(
            id = "lesson_prof_01",
            title = "El Arte del Prompt Engineering",
            description = "Hablando con la IA efectivamente.",
            content = "Técnicas avanzadas de prompting: zero-shot, few-shot, chain-of-thought, y optimización de resultados con LLMs.",
            category = SkillCategory.AI_TECH,
            phase = DigitalPhase.PROFESSIONAL,
            estimatedMinutes = 35,
            xpReward = 550
        ),
        Lesson(
            id = "lesson_prof_02",
            title = "Finanzas Personales Digitales",
            description = "Gestiona tu dinero en la era digital.",
            content = "Presupuestos, inversión básica, criptomonedas, NFTs, y seguridad en transacciones online y banca digital.",
            category = SkillCategory.DIGITAL_FINANCE,
            phase = DigitalPhase.PROFESSIONAL,
            estimatedMinutes = 40,
            xpReward = 650
        ),
        Lesson(
            id = "lesson_prof_03",
            title = "Automatización con Python",
            description = "Código que trabaja por ti.",
            content = "Fundamentos de Python: variables, loops, funciones, y automatización de tareas repetitivas del día a día.",
            category = SkillCategory.AI_TECH,
            phase = DigitalPhase.PROFESSIONAL,
            estimatedMinutes = 45,
            xpReward = 700
        ),
        Lesson(
            id = "lesson_prof_04",
            title = "Marketing Digital y Redes Sociales",
            description = "Construye tu marca personal.",
            content = "Estrategias de contenido, SEO básico, analítica de redes, engagement y monetización de plataformas.",
            category = SkillCategory.WEB_NAVIGATION,
            phase = DigitalPhase.PROFESSIONAL,
            estimatedMinutes = 38,
            xpReward = 600
        ),
        Lesson(
            id = "lesson_prof_05",
            title = "Cloud Computing Fundamentals",
            description = "La nube en tus manos.",
            content = "Conceptos de cloud, almacenamiento, colaboración en tiempo real, y herramientas Google/AWS básicas.",
            category = SkillCategory.DEVICE_TOOLS,
            phase = DigitalPhase.PROFESSIONAL,
            estimatedMinutes = 32,
            xpReward = 520
        ),
        Lesson(
            id = "lesson_prof_06",
            title = "Data Science para Principiantes",
            description = "Entiende tus datos.",
            content = "Estadística básica, visualización de datos con Python (matplotlib), interpretación de gráficos y dashboards.",
            category = SkillCategory.AI_TECH,
            phase = DigitalPhase.PROFESSIONAL,
            estimatedMinutes = 50,
            xpReward = 750
        ),
        Lesson(
            id = "lesson_prof_07",
            title = "Ciberseguridad Profesional",
            description = "Protección empresarial.",
            content = "Análisis de vulnerabilidades, ethical hacking básico, compliance GDPR/LGPD, y gestión de incidentes.",
            category = SkillCategory.SECURITY,
            phase = DigitalPhase.PROFESSIONAL,
            estimatedMinutes = 42,
            xpReward = 680
        ),
        Lesson(
            id = "lesson_prof_08",
            title = "Desarrollo Web Frontend",
            description = "Crea tu primera web.",
            content = "HTML5 semántico, CSS3 moderno, JavaScript básico, responsive design, y despliegue en hosting gratuito.",
            category = SkillCategory.CONTENT_CREATION,
            phase = DigitalPhase.PROFESSIONAL,
            estimatedMinutes = 55,
            xpReward = 800
        ),

        // ═══════════════════════════════════════════════════════════════
        // FASE INNOVADORA (21+ años) - Tecnología de Vanguardia
        // ═══════════════════════════════════════════════════════════════
        Lesson(
            id = "lesson_innov_01",
            title = "Arquitectura de IA Avanzada",
            description = "Diseña sistemas inteligentes.",
            content = "Transformers, arquitecturas LLM, fine-tuning, RAG (Retrieval Augmented Generation), y despliegue de modelos.",
            category = SkillCategory.AI_TECH,
            phase = DigitalPhase.INNOVATOR,
            estimatedMinutes = 60,
            xpReward = 1000
        ),
        Lesson(
            id = "lesson_innov_02",
            title = "Blockchain y Smart Contracts",
            description = "Descentralización profunda.",
            content = "Fundamentos de blockchain, Solidity básico, DeFi, DAOs, y casos de uso empresarial de Web3.",
            category = SkillCategory.DIGITAL_FINANCE,
            phase = DigitalPhase.INNOVATOR,
            estimatedMinutes = 55,
            xpReward = 950
        ),
        Lesson(
            id = "lesson_innov_03",
            title = "Computación Cuántica Introducción",
            description = "El futuro del procesamiento.",
            content = "Qubits, superposición, entrelazamiento, algoritmos cuánticos básicos, y plataformas IBM Q/Amazon Braket.",
            category = SkillCategory.AI_TECH,
            phase = DigitalPhase.INNOVATOR,
            estimatedMinutes = 65,
            xpReward = 1100
        ),
        Lesson(
            id = "lesson_innov_04",
            title = "Machine Learning Engineering",
            description = "ML en producción.",
            content = "Pipelines ML, MLOps, model serving, monitoreo de drift, y escalado de sistemas de ML en producción.",
            category = SkillCategory.AI_TECH,
            phase = DigitalPhase.INNOVATOR,
            estimatedMinutes = 70,
            xpReward = 1200
        ),
        Lesson(
            id = "lesson_innov_05",
            title = "Ciberseguridad Ofensiva",
            description = "Red Team avanzado.",
            content = "Penetration testing, exploit development, reverse engineering, malware analysis, y técnicas de post-explotación.",
            category = SkillCategory.SECURITY,
            phase = DigitalPhase.INNOVATOR,
            estimatedMinutes = 75,
            xpReward = 1250
        ),
        Lesson(
            id = "lesson_innov_06",
            title = "Arquitectura de Sistemas Distribuidos",
            description = "Escala global.",
            content = "Microservicios, Kubernetes, event-driven architecture, CQRS, eventual consistency, y diseño resilient.",
            category = SkillCategory.DEVICE_TOOLS,
            phase = DigitalPhase.INNOVATOR,
            estimatedMinutes = 68,
            xpReward = 1150
        ),
        Lesson(
            id = "lesson_innov_07",
            title = "Ética en IA y Tecnología",
            description = "Responsabilidad digital.",
            content = "Bias algorítmico, fairness, explainability, regulación AI Act, privacidad diferencial, y gobernanza de IA.",
            category = SkillCategory.COMMUNICATION,
            phase = DigitalPhase.INNOVATOR,
            estimatedMinutes = 50,
            xpReward = 900
        ),
        Lesson(
            id = "lesson_innov_08",
            title = "Biohacking y Transhumanismo",
            description = "Fusionando humano y máquina.",
            content = "Wearables avanzados, neurotecnología, enhancement cognitivo, ética del transhumanismo, y el futuro de la humanidad.",
            category = SkillCategory.CONTENT_CREATION,
            phase = DigitalPhase.INNOVATOR,
            estimatedMinutes = 58,
            xpReward = 1050
        ),
        Lesson(
            id = "lesson_innov_09",
            title = "Realidad Extendida (XR) Development",
            description = "Mundos inmersivos.",
            content = "VR/AR/MR development, Unity/Unreal para XR, spatial computing, y aplicaciones empresariales de metaversos.",
            category = SkillCategory.CONTENT_CREATION,
            phase = DigitalPhase.INNOVATOR,
            estimatedMinutes = 62,
            xpReward = 1080
        ),
        Lesson(
            id = "lesson_innov_10",
            title = "Edge Computing e IoT Avanzado",
            description = "Inteligencia en el perímetro.",
            content = "Arquitecturas edge, TinyML, IoT industrial, 5G applications, y procesamiento de datos en tiempo real.",
            category = SkillCategory.DEVICE_TOOLS,
            phase = DigitalPhase.INNOVATOR,
            estimatedMinutes = 56,
            xpReward = 1020
        )
    )

    /**
     * Obtiene lecciones filtradas por fase digital
     */
    fun getLessonsByPhase(phase: DigitalPhase): List<Lesson> {
        return getLessons().filter { it.phase == phase }
    }

    /**
     * Obtiene lecciones filtradas por categoría de habilidad
     */
    fun getLessonsByCategory(category: SkillCategory): List<Lesson> {
        return getLessons().filter { it.category == category }
    }

    /**
     * Obtiene lecciones recomendadas basadas en XP total (nivel del usuario)
     */
    fun getRecommendedLessons(totalXp: Long): List<Lesson> {
        val userLevel = when (totalXp) {
            in 0..999 -> DigitalPhase.SENSORIAL
            in 1000..4999 -> DigitalPhase.CREATIVE
            in 5000..14999 -> DigitalPhase.PROFESSIONAL
            else -> DigitalPhase.INNOVATOR
        }
        return getLessonsByPhase(userLevel).take(3)
    }

    /**
     * Calcula XP total disponible en todas las lecciones
     */
    fun getTotalAvailableXp(): Long {
        return getLessons().sumOf { it.xpReward }
    }
}
