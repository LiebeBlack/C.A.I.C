package com.LBs.EEDA.domain.model.educational.modules

import com.LBs.EEDA.domain.model.educational.*

/**
 * Contenido Educativo Expandido y Dinámico EEDA 2026
 * Sistema de módulos transdisciplinarios con contenido generado dinámicamente
 * Desarrollado por Yoangel Gómez con tecnología de LB's
 */

object ExpandedKnowledgeModules {
    
    /**
     * Genera contenido dinámico basado en edad, perfil cognitivo e intereses
     */
    fun generateDynamicContent(
        age: Int,
        cognitiveLevel: CognitiveLevel,
        interests: List<String>,
        previousPerformance: Map<String, Double>
    ): List<EducationalModule> {
        val baseModules = UniversalKnowledgeModules.getAllModules(age)
        
        return baseModules.map { module ->
            module.copy(
                topics = module.topics.map { topic ->
                    enhanceTopicWithDynamicContent(topic, age, interests, previousPerformance)
                },
                masteryChallenge = enhanceChallenge(module.masteryChallenge, age, cognitiveLevel)
            )
        }
    }
    
    private fun enhanceTopicWithDynamicContent(
        topic: Topic,
        age: Int,
        interests: List<String>,
        performance: Map<String, Double>
    ): Topic {
        // Adaptar metáfora según intereses
        val adaptedMetaphor = adaptMetaphor(topic.metaphor, age, interests)
        
        // Ajustar dificultad según rendimiento previo
        val difficulty = calculateDynamicDifficulty(topic.id, performance)
        
        // Generar objetivos de aprendizaje personalizados
        val personalizedObjectives = generatePersonalizedObjectives(topic, age, performance[topic.id])
        
        return topic.copy(
            metaphor = adaptedMetaphor,
            minigame = adaptMinigameType(topic.minigame, difficulty, age),
            learningObjectives = personalizedObjectives
        )
    }
    
    private fun adaptMetaphor(baseMetaphor: String, age: Int, interests: List<String>): String {
        return when {
            "gaming" in interests -> adaptToGamingMetaphor(baseMetaphor)
            "sports" in interests -> adaptToSportsMetaphor(baseMetaphor)
            "music" in interests -> adaptToMusicMetaphor(baseMetaphor)
            "art" in interests -> adaptToArtMetaphor(baseMetaphor)
            "cooking" in interests -> adaptToCookingMetaphor(baseMetaphor)
            age <= 7 -> adaptToFantasyMetaphor(baseMetaphor)
            else -> baseMetaphor
        }
    }
    
    private fun adaptToGamingMetaphor(base: String): String {
        val gamingAnalogies = mapOf(
            "contraseña" to "Tu contraseña es como tu skin única que nadie más puede usar",
            "red" to "La red es como el mapa del juego donde viaja la información",
            "algoritmo" to "Un algoritmo es la estrategia que usas para ganar cada nivel",
            "cifrado" to "El cifrado es tu poder de invisibilidad contra los hackers",
            "firewall" to "El firewall es tu escudo protector contra ataques enemigos"
        )
        return gamingAnalogies.entries.find { base.contains(it.key, ignoreCase = true) }?.value ?: base
    }
    
    private fun adaptToSportsMetaphor(base: String): String {
        val sportsAnalogies = mapOf(
            "contraseña" to "Tu contraseña es como tu técnica secreta que solo tú dominas",
            "red" to "La red es como el campo donde pasa la pelota (datos) entre jugadores",
            "algoritmo" to "Un algoritmo es la jugada estratégica para marcar gol",
            "cifrado" to "El cifrado es tu defensa impenetrable contra el equipo contrario"
        )
        return sportsAnalogies.entries.find { base.contains(it.key, ignoreCase = true) }?.value ?: base
    }
    
    private fun adaptToMusicMetaphor(base: String): String {
        val musicAnalogies = mapOf(
            "contraseña" to "Tu contraseña es tu melodía secreta que solo tú conoces",
            "red" to "La red es como la orquesta donde cada instrumento (dispositivo) toca junto",
            "algoritmo" to "Un algoritmo es la partitura que guía cada nota en orden perfecto",
            "cifrado" to "El cifrado transforma tu canción en un código solo para ti"
        )
        return musicAnalogies.entries.find { base.contains(it.key, ignoreCase = true) }?.value ?: base
    }
    
    private fun adaptToArtMetaphor(base: String): String {
        val artAnalogies = mapOf(
            "contraseña" to "Tu contraseña es tu firma única que autentifica tu identidad",
            "red" to "La red es la galería donde viajan las obras de arte digitales",
            "algoritmo" to "Un algoritmo es el proceso creativo paso a paso",
            "cifrado" to "El cifrado protege tu obra como un museo con seguridad"
        )
        return artAnalogies.entries.find { base.contains(it.key, ignoreCase = true) }?.value ?: base
    }
    
    private fun adaptToCookingMetaphor(base: String): String {
        val cookingAnalogies = mapOf(
            "contraseña" to "Tu contraseña es tu receta secreta de la abuela",
            "red" to "La red es como la cocina donde se preparan y envían los platillos",
            "algoritmo" to "Un algoritmo es la receta que sigues paso a paso",
            "cifrado" to "El cifrado guarda tu receta en una caja fuerte de sabores"
        )
        return cookingAnalogies.entries.find { base.contains(it.key, ignoreCase = true) }?.value ?: base
    }
    
    private fun adaptToFantasyMetaphor(base: String): String {
        return base.replace(Regex("(?i)contraseña"), "palabra mágica")
            .replace(Regex("(?i)cifrado"), "hechizo de protección")
            .replace(Regex("(?i)red"), "reino mágico")
            .replace(Regex("(?i)algoritmo"), "encantamiento paso a paso")
            .replace(Regex("(?i)firewall"), "escudo mágico")
    }
    
    private fun calculateDynamicDifficulty(conceptId: String, performance: Map<String, Double>): ContentDifficulty {
        val score = performance[conceptId] ?: 0.5
        return when {
            score < 0.3 -> ContentDifficulty.ELEMENTARY
            score < 0.5 -> ContentDifficulty.BEGINNER
            score < 0.7 -> ContentDifficulty.INTERMEDIATE
            score < 0.9 -> ContentDifficulty.ADVANCED
            else -> ContentDifficulty.EXPERT
        }
    }
    
    private fun generatePersonalizedObjectives(topic: Topic, age: Int, performance: Double?): List<String> {
        val baseObjectives = topic.learningObjectives
        
        return if (performance != null && performance < 0.5) {
            // Añadir objetivos de refuerzo para conceptos débiles
            baseObjectives + listOf(
                "Repasar conceptos fundamentales de ${topic.id}",
                "Practicar con ejemplos adicionales",
                "Verificar comprensión con auto-evaluación"
            )
        } else if (age > 12) {
            // Añadir objetivos avanzados para adolescentes
            baseObjectives + listOf(
                "Analizar aplicaciones del concepto en contextos reales",
                "Evaluar críticamente diferentes enfoques"
            )
        } else {
            baseObjectives
        }
    }
    
    private fun adaptMinigameType(
        currentType: MinigameType,
        difficulty: ContentDifficulty,
        age: Int
    ): MinigameType {
        // Para niños pequeños, simplificar tipos complejos
        return if (age <= 6 && currentType in listOf(MinigameType.CODE_COMPLETION, MinigameType.NETWORK_ROUTING)) {
            MinigameType.DRAG_DROP_SORTING
        } else {
            currentType
        }
    }
    
    private fun enhanceChallenge(challenge: MasteryChallenge, age: Int, cognitiveLevel: CognitiveLevel): MasteryChallenge {
        val adjustedTitle = when (cognitiveLevel) {
            CognitiveLevel.PREOPERATIONAL -> "${challenge.title} - Aventura"
            CognitiveLevel.CONCRETE_EARLY -> "${challenge.title} - Exploración"
            CognitiveLevel.CONCRETE_MATURE -> "${challenge.title} - Misión"
            CognitiveLevel.FORMAL_EARLY -> "${challenge.title} - Proyecto"
            CognitiveLevel.FORMAL_MATURE -> "${challenge.title} - Investigación"
            CognitiveLevel.ABSTRACT -> "${challenge.title} - Innovación"
        }
        
        return challenge.copy(
            title = adjustedTitle,
            difficulty = when (cognitiveLevel) {
                CognitiveLevel.PREOPERATIONAL, CognitiveLevel.CONCRETE_EARLY -> ContentDifficulty.BEGINNER
                CognitiveLevel.CONCRETE_MATURE -> ContentDifficulty.INTERMEDIATE
                CognitiveLevel.FORMAL_EARLY -> ContentDifficulty.ADVANCED
                else -> ContentDifficulty.EXPERT
            }
        )
    }
    
    // ==================== NUEVOS MÓDULOS EXPANDIDOS ====================
    
    /**
     * Módulo de Inteligencia Emocional y Habilidades Sociales
     */
    fun getEmotionalIntelligenceModule(age: Int): EducationalModule {
        return when {
            age in 3..7 -> EducationalModule(
                id = "emotional_intelligence_sensorial",
                title = "Mis Emociones Mágicas",
                emoji = "💝",
                cognitiveLevel = CognitiveLevel.PREOPERATIONAL,
                topics = listOf(
                    Topic(
                        id = "recognizing_emotions",
                        title = "Detectives de Sentimientos",
                        description = "Identificar emociones básicas en caritas y situaciones",
                        metaphor = "Las emociones son colores mágicos que pintan nuestro interior",
                        minigame = MinigameType.MATCHING,
                        learningObjectives = listOf(
                            "Identificar felicidad, tristeza, enojo y miedo",
                            "Expresar emociones con palabras"
                        )
                    ),
                    Topic(
                        id = "emotion_regulation",
                        title = "Mi Caja de Calma",
                        description = "Estrategias simples para manejar emociones fuertes",
                        metaphor = "La respiración es como un superpoder que calma la tormenta",
                        minigame = MinigameType.SEQUENCE_PUZZLE,
                        learningObjectives = listOf(
                            "Practicar técnicas de respiración",
                            "Identificar cuando necesito una pausa"
                        )
                    ),
                    Topic(
                        id = "empathy_beginnings",
                        title = "Zapatos de Otros",
                        description = "Comprender que los demás también tienen sentimientos",
                        metaphor = "Imaginar cómo se siente otro es como ponerme sus zapatos mágicos",
                        minigame = MinigameType.DRAG_DROP_SORTING,
                        learningObjectives = listOf(
                            "Reconocer emociones en otros",
                            "Ofrecer ayuda cuando alguien está triste"
                        )
                    )
                ),
                masteryChallenge = MasteryChallenge(
                    title = "Árbol de las Emociones",
                    description = "Crea un árbol familiar donde cada miembro comparta una emoción del día",
                    disciplines = listOf("Inteligencia Emocional", "Comunicación", "Empatía"),
                    difficulty = ContentDifficulty.BEGINNER
                )
            )
            
            age in 8..13 -> EducationalModule(
                id = "emotional_intelligence_logical",
                title = "Laboratorio Emocional",
                emoji = "🧠",
                cognitiveLevel = CognitiveLevel.CONCRETE_MATURE,
                topics = listOf(
                    Topic(
                        id = "emotional_granularity",
                        title = "El Arcoíris de Sentimientos",
                        description = "Diferenciar entre emociones similares (frustración vs decepción)",
                        metaphor = "Las emociones son como música con diferentes notas y ritmos",
                        minigame = MinigameType.MATCHING,
                        learningObjectives = listOf(
                            "Distinguir 12 emociones básicas",
                            "Usar vocabulario emocional preciso"
                        )
                    ),
                    Topic(
                        id = "emotional_triggers",
                        title = "Detectives de Causas",
                        description = "Identificar qué desencadena diferentes emociones",
                        metaphor = "Como detectives, investigamos por qué surgen nuestros sentimientos",
                        minigame = MinigameType.NETWORK_ROUTING,
                        learningObjectives = listOf(
                            "Reconocer desencadenantes emocionales",
                            "Anticipar reacciones emocionales"
                        )
                    ),
                    Topic(
                        id = "advanced_regulation",
                        title = "Caja de Herramientas Emocional",
                        description = "Técnicas avanzadas: reevaluación cognitiva, atención plena",
                        metaphor = "Tener múltiples herramientas para construir puentes sobre aguas turbulentas",
                        minigame = MinigameType.SIMULATION,
                        learningObjectives = listOf(
                            "Aplicar técnicas de regulación cognitiva",
                            "Practicar atención plena (mindfulness)"
                        )
                    ),
                    Topic(
                        id = "social_awareness",
                        title = "Radar Social",
                        description = "Leer señales sociales y dinámicas de grupo",
                        metaphor = "Desarrollar un radar interno que detecta el clima emocional grupal",
                        minigame = MinigameType.MEMORY_CHALLENGE,
                        learningObjectives = listOf(
                            "Interpretar lenguaje no verbal",
                            "Navegar dinámicas grupales"
                        )
                    )
                ),
                masteryChallenge = MasteryChallenge(
                    title = "Meditador Entrenado",
                    description = "Diseña una guía de 5 minutos de atención plena para tu clase",
                    disciplines = listOf("Inteligencia Emocional", "Neurociencia", "Mindfulness"),
                    difficulty = ContentDifficulty.INTERMEDIATE
                )
            )
            
            else -> EducationalModule(
                id = "emotional_intelligence_abstract",
                title = "Arquitectura Emocional",
                emoji = "🎭",
                cognitiveLevel = CognitiveLevel.FORMAL_MATURE,
                topics = listOf(
                    Topic(
                        id = "emotional_architecture",
                        title = "Neurociencia Afectiva",
                        description = "Sistemas neurales de emociones: amígdala, corteza prefrontal",
                        metaphor = "El cerebro emocional es como una orquesta con múltiples secciones",
                        minigame = MinigameType.SIMULATION,
                        learningObjectives = listOf(
                            "Comprender bases neurológicas de emociones",
                            "Analizar interacción cognición-emoción"
                        )
                    ),
                    Topic(
                        id = "emotional_intelligence_frameworks",
                        title = "Modelos de IE",
                        description = "MSCEIT, EQ-i, Goleman: comparación de modelos",
                        metaphor = "Diferentes lentes para observar el mismo paisaje emocional",
                        minigame = MinigameType.CODE_COMPLETION,
                        learningObjectives = listOf(
                            "Evaluar modelos de inteligencia emocional",
                            "Aplicar marcos teóricos a casos prácticos"
                        )
                    ),
                    Topic(
                        id = "emotional_leadership",
                        title = "Liderazgo Emocional",
                        description = "Gestión emocional en equipos y organizaciones",
                        metaphor = "El líder emocionalmente inteligente es como un director de orquesta sensible",
                        minigame = MinigameType.NETWORK_ROUTING,
                        learningObjectives = listOf(
                            "Aplicar IE en contextos de liderazgo",
                            "Gestionar climas emocionales grupales"
                        )
                    ),
                    Topic(
                        id = "meta_emotions",
                        title = "Meta-Emociones",
                        description = "Emociones sobre emociones: vergüenza de sentir ira",
                        metaphor = "Pensar sobre nuestros sentimientos como observar pensamientos desde arriba",
                        minigame = MinigameType.PUZZLE,
                        learningObjectives = listOf(
                            "Identificar meta-emociones",
                            "Interrumpir ciclos emocionales negativos"
                        )
                    )
                ),
                masteryChallenge = MasteryChallenge(
                    title = "Intervención Emocional",
                    description = "Diseña y evalúa una intervención de IE para un contexto real",
                    disciplines = listOf("Psicología", "Neurociencia", "Organizaciones", "Investigación"),
                    difficulty = ContentDifficulty.EXPERT
                )
            )
        }
    }
    
    /**
     * Módulo de Pensamiento Crítico y Resolución de Problemas
     */
    fun getCriticalThinkingModule(age: Int): EducationalModule {
        return when {
            age in 3..7 -> EducationalModule(
                id = "critical_thinking_sensorial",
                title = "Detectives Pequeños",
                emoji = "🔍",
                cognitiveLevel = CognitiveLevel.PREOPERATIONAL,
                topics = listOf(
                    Topic(
                        id = "observation_skills",
                        title = "Ojos de Águila",
                        description = "Desarrollar atención a detalles en objetos cotidianos",
                        metaphor = "Los buenos detectives ven cosas que otros no notan",
                        minigame = MinigameType.MEMORY_CHALLENGE,
                        learningObjectives = listOf(
                            "Describir objetos con 5 detalles",
                            "Identificar qué cambió en una imagen"
                        )
                    ),
                    Topic(
                        id = "simple_predictions",
                        title = "Bola de Cristal",
                        description = "Predecir qué pasará después en historias sencillas",
                        metaphor = "Como adivinos, pensamos qué sucederá después",
                        minigame = MinigameType.SEQUENCE_PUZZLE,
                        learningObjectives = listOf(
                            "Hacer predicciones basadas en pistas",
                            "Verificar si las predicciones se cumplieron"
                        )
                    ),
                    Topic(
                        id = "comparing_sorting",
                        title = "Agrupadores Expertos",
                        description = "Clasificar objetos por diferentes criterios",
                        metaphor = "Organizar tesoros por colores, formas y tamaños",
                        minigame = MinigameType.DRAG_DROP_SORTING,
                        learningObjectives = listOf(
                            "Clasificar por múltiples criterios",
                            "Explicar reglas de clasificación"
                        )
                    )
                ),
                masteryChallenge = MasteryChallenge(
                    title = "Misterio del Objeto Desaparecido",
                    description = "Resuelve quién tomó el objeto usando pistas visuales",
                    disciplines = listOf("Observación", "Lógica", "Deducción"),
                    difficulty = ContentDifficulty.BEGINNER
                )
            )
            
            age in 8..13 -> EducationalModule(
                id = "critical_thinking_logical",
                title = "Laboratorio del Pensamiento",
                emoji = "🧩",
                cognitiveLevel = CognitiveLevel.CONCRETE_MATURE,
                topics = listOf(
                    Topic(
                        id = "logical_fallacies",
                        title = "Trampas del Pensamiento",
                        description = "Identificar falacias comunes: apelación a autoridad, falso dilema",
                        metaphor = "Aprender a ver las trampas que nuestra mente puede caer",
                        minigame = MinigameType.MATCHING,
                        learningObjectives = listOf(
                            "Identificar 8 falacias lógicas básicas",
                            "Construir argumentos sólidos"
                        )
                    ),
                    Topic(
                        id = "evidence_evaluation",
                        title = "Detectives de Evidencias",
                        description = "Evaluar credibilidad de fuentes y evidencias",
                        metaphor = "Como detectives forenses, analizamos qué pruebas son confiables",
                        minigame = MinigameType.SIMULATION,
                        learningObjectives = listOf(
                            "Distinguir hechos de opiniones",
                            "Evaluar credibilidad de fuentes"
                        )
                    ),
                    Topic(
                        id = "problem_decomposition",
                        title = "Divide y Vencerás",
                        description = "Dividir problemas complejos en partes manejables",
                        metaphor = "Un gran rompecabezas se resuelve pieza por pieza",
                        minigame = MinigameType.PUZZLE,
                        learningObjectives = listOf(
                            "Aplicar análisis de problemas",
                            "Priorizar sub-problemas"
                        )
                    ),
                    Topic(
                        id = "creative_solutions",
                        title = "Tormenta de Ideas",
                        description = "Generar múltiples soluciones y seleccionar la óptima",
                        metaphor = "Como inventores, exploramos muchas ideas antes de elegir",
                        minigame = MinigameType.SIMULATION,
                        learningObjectives = listOf(
                            "Generar alternativas creativas",
                            "Evaluar soluciones por criterios"
                        )
                    )
                ),
                masteryChallenge = MasteryChallenge(
                    title = "Comité de Debate",
                    description = "Organiza un debate formal sobre un tema actual con argumentos sólidos",
                    disciplines = listOf("Lógica", "Retórica", "Investigación", "Comunicación"),
                    difficulty = ContentDifficulty.INTERMEDIATE
                )
            )
            
            else -> EducationalModule(
                id = "critical_thinking_abstract",
                title = "Epistemología Práctica",
                emoji = "🎓",
                cognitiveLevel = CognitiveLevel.FORMAL_MATURE,
                topics = listOf(
                    Topic(
                        id = "scientific_method",
                        title = "Método Científico Avanzado",
                        description = "Hipótesis, diseño experimental, análisis estadístico, publicación",
                        metaphor = "La ciencia es el arte de preguntar correctamente",
                        minigame = MinigameType.CODE_COMPLETION,
                        learningObjectives = listOf(
                            "Diseñar experimentos controlados",
                            "Analizar datos estadísticamente"
                        )
                    ),
                    Topic(
                        id = "cognitive_biases",
                        title = "Sesgos Cognitivos",
                        description = "Confirmation bias, anchoring, availability heuristic",
                        metaphor = "Nuestras mentes tienen atajos que a veces nos llevan por mal camino",
                        minigame = MinigameType.SIMULATION,
                        learningObjectives = listOf(
                            "Identificar 20+ sesgos cognitivos",
                            "Mitigar sesgos en toma de decisiones"
                        )
                    ),
                    Topic(
                        id = "systems_thinking",
                        title = "Pensamiento Sistémico",
                        description = "Retroalimentación, stocks y flows, leverage points",
                        metaphor = "Ver el bosque completo, no solo árboles individuales",
                        minigame = MinigameType.NETWORK_ROUTING,
                        learningObjectives = listOf(
                            "Mapear sistemas complejos",
                            "Identificar puntos de influencia"
                        )
                    ),
                    Topic(
                        id = "ethical_reasoning",
                        title = "Razonamiento Ético",
                        description = "Consecuencialismo, deontología, ética de virtudes",
                        metaphor = "Marcos para navegar dilemas morales complejos",
                        minigame = MinigameType.PUZZLE,
                        learningObjectives = listOf(
                            "Aplicar marcos éticos a casos",
                            "Defender posiciones éticas razonadas"
                        )
                    )
                ),
                masteryChallenge = MasteryChallenge(
                    title = "Investigación Original",
                    description = "Diseña, ejecuta y comunica un estudio sobre sesgos cognitivos",
                    disciplines = listOf("Psicología", "Estadística", "Epistemología", "Comunicación"),
                    difficulty = ContentDifficulty.EXPERT
                )
            )
        }
    }
    
    /**
     * Módulo de Creatividad e Innovación
     */
    fun getCreativityModule(age: Int): EducationalModule {
        return when {
            age in 3..7 -> EducationalModule(
                id = "creativity_sensorial",
                title = "Taller de Inventores",
                emoji = "✨",
                cognitiveLevel = CognitiveLevel.PREOPERATIONAL,
                topics = listOf(
                    Topic(
                        id = "divergent_thinking",
                        title = "Lluvia de Ideas",
                        description = "Generar múltiples usos para objetos cotidianos",
                        metaphor = "Nuestra imaginación es un pozo sin fondo de ideas",
                        minigame = MinigameType.SIMULATION,
                        learningObjectives = listOf(
                            "Listar 10+ usos alternativos para objetos",
                            "Valorar ideas inusuales"
                        )
                    ),
                    Topic(
                        id = "story_creation",
                        title = "Cuentacuentos",
                        description = "Crear historias con inicio, nudo y desenlace",
                        metaphor = "Tejemos historias como alfombras mágicas de palabras",
                        minigame = MinigameType.SEQUENCE_PUZZLE,
                        learningObjectives = listOf(
                            "Estructurar narrativas coherentes",
                            "Desarrollar personajes"
                        )
                    ),
                    Topic(
                        id = "artistic_expression",
                        title = "Artistas Libres",
                        description = "Expresar emociones y ideas mediante arte",
                        metaphor = "El arte es el lenguaje de las almas",
                        minigame = MinigameType.DRAG_DROP_SORTING,
                        learningObjectives = listOf(
                            "Expresar emociones mediante colores",
                            "Combinar materiales creativamente"
                        )
                    )
                ),
                masteryChallenge = MasteryChallenge(
                    title = "Mi Invento Útil",
                    description = "Crea un invento que solucione un problema de tu vida diaria",
                    disciplines = listOf("Creatividad", "Diseño", "Empatía"),
                    difficulty = ContentDifficulty.BEGINNER
                )
            )
            
            else -> EducationalModule(
                id = "creativity_abstract",
                title = "Ingeniería de la Innovación",
                emoji = "🚀",
                cognitiveLevel = CognitiveLevel.FORMAL_MATURE,
                topics = listOf(
                    Topic(
                        id = "design_thinking",
                        title = "Design Thinking",
                        description = "Empatizar, definir, idear, prototipar, testear",
                        metaphor = "El diseño es la intersección de deseabilidad, viabilidad y factibilidad",
                        minigame = MinigameType.SIMULATION,
                        learningObjectives = listOf(
                            "Aplicar metodología design thinking",
                            "Iterar basado en feedback"
                        )
                    ),
                    Topic(
                        id = "innovation_frameworks",
                        title = "SCAMPER, TRIZ, Blue Ocean",
                        description = "Marcos sistemáticos para innovación",
                        metaphor = "Herramientas profesionales para inventar sistemáticamente",
                        minigame = MinigameType.CODE_COMPLETION,
                        learningObjectives = listOf(
                            "Aplicar técnicas SCAMPER",
                            "Analizar estrategias Blue Ocean"
                        )
                    ),
                    Topic(
                        id = "creative_leadership",
                        title = "Liderazgo Creativo",
                        description = "Fomentar creatividad en equipos e organizaciones",
                        metaphor = "El líder creativo cultiva jardines donde florecen ideas",
                        minigame = MinigameType.NETWORK_ROUTING,
                        learningObjectives = listOf(
                            "Diseñar espacios para innovación",
                            "Gestionar equipos creativos"
                        )
                    )
                ),
                masteryChallenge = MasteryChallenge(
                    title = "Startup Educativa",
                    description = "Desarrolla un MVP de solución educativa innovadora",
                    disciplines = listOf("Emprendimiento", "Design Thinking", "Tecnología", "Negocios"),
                    difficulty = ContentDifficulty.EXPERT
                )
            )
        }
    }
    
    /**
     * Obtiene todos los módulos expandidos para una edad específica
     */
    fun getAllExpandedModules(age: Int): List<EducationalModule> {
        return listOf(
            getEmotionalIntelligenceModule(age),
            getCriticalThinkingModule(age),
            getCreativityModule(age),
            UniversalKnowledgeModules.getScienceContent(age),
            UniversalKnowledgeModules.getHistoryContent(age),
            UniversalKnowledgeModules.getTechContent(age),
            UniversalKnowledgeModules.getWellbeingContent(age)
        )
    }
}
