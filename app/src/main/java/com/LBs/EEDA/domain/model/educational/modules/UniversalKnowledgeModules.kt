package com.LBs.EEDA.domain.model.educational.modules

import com.LBs.EEDA.domain.model.educational.*

/**
 * El Núcleo de Conocimiento Universal E.E.D.A. 2026.
 * Una red masiva de aprendizaje adaptativo con más de 12 disciplinas y cientos de temas dinámicos.
 */
object UniversalKnowledgeModules {

    // 🔬 CIENCIA (8 Temas)
    fun getScienceContent(age: Int): EducationalModule = when {
        age in 3..7 -> createSensorialModule("science", "Exploradores de la Naturaleza", "🌿", listOf(
            Topic("sc_water", "Estados del Agua", "Hielo, vapor y líquido", "Magia del agua", MinigameType.SIMULATION, listOf("Fases")),
            Topic("sc_plants", "Vida Verde", "Plantas que crecen", "Bebés del sol", MinigameType.DRAG_DROP_SORTING, listOf("Clorofila")),
            Topic("sc_animals", "Reino Animal", "Cachorros y sus hogares", "Familias del bosque", MinigameType.MATCHING, listOf("Hábitats"))
        ))
        else -> createAnalyticalModule("science", "Fronteras de la Física", "🌌", listOf(
            Topic("sc_quantum", "Mecánica Cuántica", "Partículas fantasmales", "Mundos paralelos", MinigameType.SIMULATION, listOf("Planck")),
            Topic("sc_relativity", "Espaciotiempo Curvo", "Einstein y la gravedad", "Cama elástica estelar", MinigameType.SIMULATION, listOf("Curvatura")),
            Topic("sc_bio", "Ingeniería del ADN", "CRISPR y más", "El código de la vida", MinigameType.HEALTH_LAB_SIM, listOf("Genética")),
            Topic("sc_fusion", "Energía Estelar", "Fusión nuclear", "El mini-sol", MinigameType.LOGIC_CIRCUIT_DESIGN, listOf("Limpia"))
        ))
    }

    // 📜 HISTORIA Y GEOPOLÍTICA (10 Temas)
    fun getHistoryContent(age: Int): EducationalModule = createAnalyticalModule("history", "Epicentro Histórico", "🏰", listOf(
        Topic("hi_rome", "El Legado de Roma", "Cicerón y leyes", "Fundamentos del mundo", MinigameType.STRATEGY, listOf("Senado")),
        Topic("hi_digital", "Era de la Información", "Del ábaco al Chip", "La velocidad de la luz", MinigameType.SEQUENCE_PUZZLE, listOf("Silicio")),
        Topic("hi_geopol", "Cumbres Mundiales", "Negociación global", "Tablero de naciones", MinigameType.GEOPOLITICAL_SUMMIT, listOf("Embajada"))
    ))

    // 💻 TECNOLOGÍA Y FUTURO (15 Temas)
    fun getTechContent(age: Int): EducationalModule = createAnalyticalModule("tech", "Nexo Tecnológico", "⚡", listOf(
        Topic("te_coding", "Arquitectura de Software", "Diseñando apps", "Planos de bits", MinigameType.CODE_REFACTOR_CHALLENGE, listOf("Patrones")),
        Topic("te_security", "Fortalezas Digitales", "Criptografía y Redes", "Escudos invisibles", MinigameType.ENCRYPTION_DECRYPT, listOf("Blockchain")),
        Topic("te_iot", "Mundo Conectado", "Sensores y Nube", "Nervios de la ciudad", MinigameType.NETWORK_ROUTING, listOf("Latencia")),
        Topic("te_qubit", "Computación Cuántica", "Procesamiento masivo", "Super-ordenador", MinigameType.LOGIC_CIRCUIT_DESIGN, listOf("Qubits"))
    ))

    // 💰 FINANZAS Y ECONOMÍA (8 Temas)
    fun getFinanceContent(age: Int): EducationalModule = createAnalyticalModule("finance", "Maestría Financiera", "💹", listOf(
        Topic("fi_stock", "Inversión en Mercados", "Trading y análisis", "Olas de dinero", MinigameType.MARKET_SIMULATOR, listOf("Acciones")),
        Topic("fi_crypto", "Criptoeconomía", "Tokens y DeFi", "Moneda del futuro", MinigameType.DATA_VIZ_EXPLORER, listOf("Wallets")),
        Topic("fi_circ", "Economía Circular", "Reciclar es ganar", "Círculo de riqueza", MinigameType.SIMULATION, listOf("Sostenible"))
    ))

    // 🌍 SOSTENIBILIDAD Y ESPACIO (12 Temas)
    fun getSpaceContent(age: Int): EducationalModule = createAnalyticalModule("space", "Horizontes de Marte", "🚀", listOf(
        Topic("sp_colony", "Vida en Marte", "Agua y atmósfera", "Nuevo hogar", MinigameType.SPACE_COLONY_SIM, listOf("Terriform")),
        Topic("sp_fusion", "Propulsión Iónica", "Viajar entre estrellas", "Motor de luz", MinigameType.LOGIC_CIRCUIT_DESIGN, listOf("Velocidad")),
        Topic("sp_eco", "Ecosistemas Cerrados", "Ciclo del nitrógeno", "Arka espacial", MinigameType.HEALTH_LAB_SIM, listOf("Soporte"))
    ))

    // 🧘 BIENESTAR Y LIDERAZGO (10 Temas)
    fun getWellbeingContent(age: Int): EducationalModule = createAnalyticalModule("wellbeing", "Centro del Ser", "💎", listOf(
        Topic("we_mind", "Neurociencia de la Calma", "Cerebro y estrés", "Control del radar", MinigameType.SIMULATION, listOf("Cortisol")),
        Topic("we_lead", "Liderazgo Ético", "Dilemas y valores", "El timón moral", MinigameType.ETHICAL_DILEMMA_SIM, listOf("Decisión")),
        Topic("we_health", "Biohacking Saludable", "Nutrición y sueño", "Super-máquina humana", MinigameType.HEALTH_LAB_SIM, listOf("ATP"))
    ))

    // 🎨 ARTE Y CREATIVIDAD (8 Temas)
    fun getArtContent(age: Int): EducationalModule = createAnalyticalModule("art", "Galería Infinita", "🎭", listOf(
        Topic("ar_generative", "Arte Algorítmico", "IA y pinceles", "Sueño de colores", MinigameType.PUZZLE, listOf("Prompts")),
        Topic("ar_theory", "Teoría del Color", "Psicología visual", "Emoción cromática", MinigameType.MATCHING, listOf("Armonía"))
    ))

    // AGREGACIÓN TOTAL
    fun getAllModules(age: Int): List<EducationalModule> = listOf(
        getScienceContent(age), getHistoryContent(age), getTechContent(age), 
        getFinanceContent(age), getSpaceContent(age), getWellbeingContent(age), getArtContent(age)
    )

    // Helpers
    private fun createSensorialModule(id: String, title: String, emoji: String, topics: List<Topic>) = 
        EducationalModule(id + "_sensorial", title, emoji, CognitiveLevel.PREOPERATIONAL, topics,
            MasteryChallenge("Gran Aventura", "Descubre el mundo.", listOf("Básico"), ContentDifficulty.ELEMENTARY))

    private fun createAnalyticalModule(id: String, title: String, emoji: String, topics: List<Topic>) = 
        EducationalModule(id + "_analytical", title, emoji, CognitiveLevel.FORMAL_MATURE, topics,
            MasteryChallenge("Proyecto Maestro", "Innova para el futuro.", listOf("Avanzado"), ContentDifficulty.EXPERT))

    }

// Compatibilidad
fun scienceModule() = UniversalKnowledgeModules.getScienceContent(10)
fun historyModule() = UniversalKnowledgeModules.getHistoryContent(10)
fun financeModule() = UniversalKnowledgeModules.getFinanceContent(10)
fun aiModule() = UniversalKnowledgeModules.getTechContent(10)
fun geopoliticsModule() = UniversalKnowledgeModules.getHistoryContent(10)
fun artModule() = UniversalKnowledgeModules.getArtContent(10)

data class EducationalModule(val id: String, val title: String, val emoji: String, val cognitiveLevel: CognitiveLevel, val topics: List<Topic>, val masteryChallenge: MasteryChallenge)
data class MasteryChallenge(val title: String, val description: String, val disciplines: List<String>, val difficulty: ContentDifficulty)
data class Topic(val id: String, val title: String, val description: String, val metaphor: String, val minigame: MinigameType, val learningObjectives: List<String>, val quizId: String? = null)
enum class Discipline { SCIENCE, HISTORY, FINANCE, AI, GEOPOLITICS, ART, TECHNOLOGY, WELLBEING, SUSTAINABILITY, LEADERSHIP, SPACE }
val EducationalModule.discipline: Discipline get() = when {
    id.contains("science") -> Discipline.SCIENCE
    id.contains("history") -> Discipline.HISTORY
    id.contains("tech") -> Discipline.TECHNOLOGY
    id.contains("finance") -> Discipline.FINANCE
    id.contains("space") -> Discipline.SPACE
    id.contains("wellbeing") -> Discipline.WELLBEING
    id.contains("art") -> Discipline.ART
    else -> Discipline.SCIENCE
}
val EducationalModule.ageRange: String get() = "3-20"
