package com.LBs.EEDA.domain.model.content

import com.LBs.EEDA.domain.model.DigitalPhase
import com.LBs.EEDA.domain.model.SkillCategory
import kotlinx.serialization.Serializable

/**
 * Sistema de Módulos de Contenido Educativo E.E.D.A
 * Organización completa del currículo por fases y disciplinas
 */
@Serializable
object ContentModules {

    // ═══════════════════════════════════════════════════════════════
    // MÓDULOS POR FASE Y DISCIPLINA
    // ═══════════════════════════════════════════════════════════════

    fun getAllModules(phase: DigitalPhase): List<EducationalModule> {
        return when (phase) {
            DigitalPhase.SENSORIAL -> getSensorialModules()
            DigitalPhase.CREATIVE -> getCreativeModules()
            DigitalPhase.PROFESSIONAL -> getProfessionalModules()
            DigitalPhase.INNOVATOR -> getInnovatorModules()
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // FASE SENSORIAL (3-7 años)
    // ═══════════════════════════════════════════════════════════════
    private fun getSensorialModules(): List<EducationalModule> = listOf(
        // DISPOSITIVOS Y TECNOLOGÍA
        EducationalModule(
            id = "mod_sens_tech_01",
            title = "Mi Tableta Amiga",
            description = "Conoce y cuida tu dispositivo",
            phase = DigitalPhase.SENSORIAL,
            category = SkillCategory.DEVICE_TOOLS,
            lessons = listOf(
                LessonContent(
                    id = "less_sens_01",
                    title = "Encender y Apagar",
                    duration = 5,
                    objectives = listOf("Identificar botón de encendido", "Apagar correctamente"),
                    activities = listOf("Presionar botón", "Contar hasta 5")
                ),
                LessonContent(
                    id = "less_sens_02",
                    title = "Cuidar mi Tableta",
                    duration = 8,
                    objectives = listOf("No comer cerca", "Cargar con ayuda"),
                    activities = listOf("Limpieza", "Cuidado de pantalla")
                )
            ),
            skills = listOf("device_care", "basic_usage"),
            xpReward = 200
        ),

        // SEGURIDAD DIGITAL BÁSICA
        EducationalModule(
            id = "mod_sens_security_01",
            title = "Guardianes de Internet",
            description = "Identificar seguro vs peligroso",
            phase = DigitalPhase.SENSORIAL,
            category = SkillCategory.SECURITY,
            lessons = listOf(
                LessonContent(
                    id = "less_sens_sec_01",
                    title = "El Semáforo de Internet",
                    duration = 10,
                    objectives = listOf("Verde = Seguro", "Rojo = Peligro"),
                    activities = listOf("Juego de colores", "Clasificación")
                ),
                LessonContent(
                    id = "less_sens_sec_02",
                    title = "Preguntar a Adultos",
                    duration = 8,
                    objectives = listOf("Pedir ayuda", "No hacer clic en desconocidos"),
                    activities = listOf("Role-play", "Escenarios")
                )
            ),
            skills = listOf("safe_browsing", "help_seeking"),
            xpReward = 250
        ),

        // GESTOS Y NAVEGACIÓN TÁCTIL
        EducationalModule(
            id = "mod_sens_gestures_01",
            title = "Magia de los Dedos",
            description = "Toca, desliza y explora",
            phase = DigitalPhase.SENSORIAL,
            category = SkillCategory.DEVICE_TOOLS,
            lessons = listOf(
                LessonContent(
                    id = "less_sens_ges_01",
                    title = "El Toque Mágico",
                    duration = 6,
                    objectives = listOf("Tocar iconos", "Abrir aplicaciones"),
                    activities = listOf("Tocar animales", "Sonidos")
                ),
                LessonContent(
                    id = "less_sens_ges_02",
                    title = "Deslizar y Pellizcar",
                    duration = 8,
                    objectives = listOf("Scroll arriba/abajo", "Zoom in/out"),
                    activities = listOf("Mapa del tesoro", "Fotos")
                )
            ),
            skills = listOf("touch_basic", "gestures"),
            xpReward = 180
        ),

        // COMUNICACIÓN DIGITAL
        EducationalModule(
            id = "mod_sens_comm_01",
            title = "Emojis y Caritas",
            description = "Expresiones digitales",
            phase = DigitalPhase.SENSORIAL,
            category = SkillCategory.COMMUNICATION,
            lessons = listOf(
                LessonContent(
                    id = "less_sens_emo_01",
                    title = "Cara Feliz, Cara Triste",
                    duration = 7,
                    objectives = listOf("Identificar emociones", "Usar emojis"),
                    activities = listOf("Memoria de emojis", "Mímica")
                )
            ),
            skills = listOf("emoji_usage", "digital_communication"),
            xpReward = 150
        ),

        // TIEMPO DIGITAL SALUDABLE
        EducationalModule(
            id = "mod_sens_time_01",
            title = "Guardianes del Tiempo",
            description = "Balance pantalla y juego",
            phase = DigitalPhase.SENSORIAL,
            category = SkillCategory.WELLBEING,
            lessons = listOf(
                LessonContent(
                    id = "less_sens_time_01",
                    title = "Temporizador Visual",
                    duration = 10,
                    objectives = listOf("Ver tiempo restante", "Transición suave"),
                    activities = listOf("Reloj de arena digital", "Alertas amigables")
                )
            ),
            skills = listOf("time_awareness", "healthy_habits"),
            xpReward = 200
        )
    )

    // ═══════════════════════════════════════════════════════════════
    // FASE CREATIVA (8-14 años)
    // ═══════════════════════════════════════════════════════════════
    private fun getCreativeModules(): List<EducationalModule> = listOf(
        // CREACIÓN DE CONTENIDO
        EducationalModule(
            id = "mod_creat_content_01",
            title = "Estudio Digital Creativo",
            description = "Crea arte, música y video",
            phase = DigitalPhase.CREATIVE,
            category = SkillCategory.CONTENT_CREATION,
            lessons = listOf(
                LessonContent(
                    id = "less_creat_art_01",
                    title = "Diseño Gráfico Básico",
                    duration = 25,
                    objectives = listOf("Capas", "Colores", "Exportar"),
                    activities = listOf("Poster", "Collage digital")
                ),
                LessonContent(
                    id = "less_creat_music_01",
                    title = "Música Digital",
                    duration = 20,
                    objectives = listOf("Loops", "Beats", "Composición"),
                    activities = listOf("Crear ritmo", "Mix simple")
                ),
                LessonContent(
                    id = "less_creat_video_01",
                    title = "Edición de Video",
                    duration = 30,
                    objectives = listOf("Cortes", "Transiciones", "Audio"),
                    activities = listOf("Editar clip", "Añadir música")
                )
            ),
            skills = listOf("graphic_design", "music_production", "video_editing"),
            xpReward = 600
        ),

        // PENSAMIENTO COMPUTACIONAL
        EducationalModule(
            id = "mod_creat_logic_01",
            title = "Lógica de Programación",
            description = "Algoritmos y resolución de problemas",
            phase = DigitalPhase.CREATIVE,
            category = SkillCategory.LOGIC_PROBLEM_SOLVING,
            lessons = listOf(
                LessonContent(
                    id = "less_creat_algo_01",
                    title = "Secuencias y Orden",
                    duration = 20,
                    objectives = listOf("Algoritmos", "Pasos ordenados"),
                    activities = listOf("Receta", "Laberinto")
                ),
                LessonContent(
                    id = "less_creat_algo_02",
                    title = "Bucles y Repetición",
                    duration = 25,
                    objectives = listOf("for loops", "while loops"),
                    activities = listOf("Patrones", "Animación")
                ),
                LessonContent(
                    id = "less_creat_cond_01",
                    title = "Decisiones (if/else)",
                    duration = 22,
                    objectives = listOf("Condiciones", "Lógica booleana"),
                    activities = listOf("Quiz interactivo", "Juego de decisiones")
                )
            ),
            skills = listOf("algorithms", "loops", "conditionals"),
            xpReward = 700
        ),

        // CIBERSEGURIDAD PARA ADOLESCENTES
        EducationalModule(
            id = "mod_creat_sec_01",
            title = "Defensor Digital",
            description = "Protege tu identidad online",
            phase = DigitalPhase.CREATIVE,
            category = SkillCategory.SECURITY,
            lessons = listOf(
                LessonContent(
                    id = "less_creat_pass_01",
                    title = "Contraseñas Seguras",
                    duration = 18,
                    objectives = listOf("Longitud", "Variación", "Manager"),
                    activities = listOf("Auditoría", "Generador")
                ),
                LessonContent(
                    id = "less_creat_priv_01",
                    title = "Privacidad en Redes",
                    duration = 20,
                    objectives = listOf("Configuración", "Datos personales"),
                    activities = listOf("Revisar perfil", "Ajustes")
                ),
                LessonContent(
                    id = "less_creat_phish_01",
                    title = "Detectar Phishing",
                    duration = 22,
                    objectives = listOf("Señales de alerta", "Verificación"),
                    activities = listOf("Análisis de emails", "Reportar")
                )
            ),
            skills = listOf("password_security", "privacy_settings", "phishing_detection"),
            xpReward = 650
        ),

        // NAVEGACIÓN WEB EFICIENTE
        EducationalModule(
            id = "mod_creat_web_01",
            title = "Navegador Experto",
            description = "Busca, evalúa y organiza información",
            phase = DigitalPhase.CREATIVE,
            category = SkillCategory.WEB_NAVIGATION,
            lessons = listOf(
                LessonContent(
                    id = "less_creat_search_01",
                    title = "Búsqueda Avanzada",
                    duration = 20,
                    objectives = listOf("Operadores", "Filtros", "Bookmarks"),
                    activities = listOf("Tesoro digital", "Organización")
                ),
                LessonContent(
                    id = "less_creat_eval_01",
                    title = "Evaluar Fuentes",
                    duration = 25,
                    objectives = listOf("Credibilidad", "Bias", "Fact-checking"),
                    activities = listOf("Análisis crítico", "Comparación")
                )
            ),
            skills = listOf("search_advanced", "source_evaluation", "bookmarking"),
            xpReward = 550
        ),

        // CIUDADANÍA DIGITAL
        EducationalModule(
            id = "mod_creat_citizen_01",
            title = "Ciudadano Digital",
            description = "Ética, respeto y comunidad online",
            phase = DigitalPhase.CREATIVE,
            category = SkillCategory.COMMUNICATION,
            lessons = listOf(
                LessonContent(
                    id = "less_creat_ethic_01",
                    title = "Ética Digital",
                    duration = 20,
                    objectives = listOf("Respeto", "Integridad", "Responsabilidad"),
                    activities = listOf("Dilemas", "Discusión")
                ),
                LessonContent(
                    id = "less_creat_footprint_01",
                    title = "Huella Digital",
                    duration = 18,
                    objectives = listOf("Rastros digitales", "Permanencia"),
                    activities = listOf("Auditoría", "Limpieza")
                )
            ),
            skills = listOf("digital_ethics", "footprint_awareness"),
            xpReward = 500
        )
    )

    // ═══════════════════════════════════════════════════════════════
    // FASE PROFESIONAL (15-20 años)
    // ═══════════════════════════════════════════════════════════════
    private fun getProfessionalModules(): List<EducationalModule> = listOf(
        // INTELIGENCIA ARTIFICIAL
        EducationalModule(
            id = "mod_prof_ai_01",
            title = "IA y Machine Learning",
            description = "Fundamentos y aplicaciones",
            phase = DigitalPhase.PROFESSIONAL,
            category = SkillCategory.AI_TECH,
            lessons = listOf(
                LessonContent(
                    id = "less_prof_ai_01",
                    title = "Qué es la IA",
                    duration = 35,
                    objectives = listOf("ML vs Programación", "Tipos de IA", "Aplicaciones"),
                    activities = listOf("Clasificación", "Predicción")
                ),
                LessonContent(
                    id = "less_prof_prompt_01",
                    title = "Prompt Engineering",
                    duration = 40,
                    objectives = listOf("Zero-shot", "Few-shot", "Chain-of-thought"),
                    activities = listOf("Optimización", "Experimentos")
                ),
                LessonContent(
                    id = "less_prof_ai_ethic_01",
                    title = "Ética en IA",
                    duration = 30,
                    objectives = listOf("Bias", "Fairness", "Transparencia"),
                    activities = listOf("Análisis de casos", "Debate")
                ),
                LessonContent(
                    id = "less_prof_ai_build_01",
                    title = "Crear tu primer modelo",
                    duration = 50,
                    objectives = listOf("Preparar datos", "Entrenar", "Evaluar"),
                    activities = listOf("Clasificador de imágenes", "Deploy")
                )
            ),
            skills = listOf("ai_fundamentals", "prompt_engineering", "ml_basics", "ai_ethics"),
            xpReward = 1200
        ),

        // FINANZAS DIGITALES
        EducationalModule(
            id = "mod_prof_fin_01",
            title = "Economía Digital",
            description = "Finanzas, cripto y blockchain",
            phase = DigitalPhase.PROFESSIONAL,
            category = SkillCategory.DIGITAL_FINANCE,
            lessons = listOf(
                LessonContent(
                    id = "less_prof_fin_01",
                    title = "Banca Digital",
                    duration = 35,
                    objectives = listOf("Apps bancarias", "Seguridad", "Transferencias"),
                    activities = listOf("Simulación", "Presupuesto")
                ),
                LessonContent(
                    id = "less_prof_crypto_01",
                    title = "Criptomonedas",
                    duration = 40,
                    objectives = listOf("Blockchain", "Wallets", "Trading básico"),
                    activities = listOf("Portfolio virtual", "Análisis")
                ),
                LessonContent(
                    id = "less_prof_nft_01",
                    title = "NFTs y Web3",
                    duration = 35,
                    objectives = listOf("Tokens", "Smart contracts", "Mercados"),
                    activities = listOf("Crear NFT", "Marketplace")
                )
            ),
            skills = listOf("digital_banking", "cryptocurrency", "blockchain", "nft"),
            xpReward = 1000
        ),

        // PROGRAMACIÓN AVANZADA
        EducationalModule(
            id = "mod_prof_code_01",
            title = "Desarrollo de Software",
            description = "De principiante a desarrollador",
            phase = DigitalPhase.PROFESSIONAL,
            category = SkillCategory.AI_TECH,
            lessons = listOf(
                LessonContent(
                    id = "less_prof_python_01",
                    title = "Python Avanzado",
                    duration = 50,
                    objectives = listOf("OOP", "Librerías", "APIs"),
                    activities = listOf("Proyecto", "Automatización")
                ),
                LessonContent(
                    id = "less_prof_web_01",
                    title = "Desarrollo Web",
                    duration = 55,
                    objectives = listOf("HTML/CSS/JS", "Frameworks", "Responsive"),
                    activities = listOf("Portfolio web", "Deploy")
                ),
                LessonContent(
                    id = "less_prof_data_01",
                    title = "Análisis de Datos",
                    duration = 45,
                    objectives = listOf("Pandas", "Visualización", "Insights"),
                    activities = listOf("Dashboard", "Reporte")
                )
            ),
            skills = listOf("python_advanced", "web_dev", "data_analysis", "api_integration"),
            xpReward = 1300
        ),

        // PRODUCTIVIDAD DIGITAL
        EducationalModule(
            id = "mod_prod_01",
            title = "Suite Productiva",
            description = "Herramientas profesionales",
            phase = DigitalPhase.PROFESSIONAL,
            category = SkillCategory.PRODUCTIVITY,
            lessons = listOf(
                LessonContent(
                    id = "less_prof_cloud_01",
                    title = "Cloud Computing",
                    duration = 35,
                    objectives = listOf("Almacenamiento", "Colaboración", "Backups"),
                    activities = listOf("Google Workspace", "Organización")
                ),
                LessonContent(
                    id = "less_prof_proj_01",
                    title = "Gestión de Proyectos",
                    duration = 30,
                    objectives = listOf("Kanban", "Scrum", "Herramientas"),
                    activities = listOf("Planificación", "Sprints")
                ),
                LessonContent(
                    id = "less_prof_pres_01",
                    title = "Presentaciones Efectivas",
                    duration = 28,
                    objectives = listOf("Storytelling", "Diseño", "Delivery"),
                    activities = listOf("Pitch", "Demo")
                )
            ),
            skills = listOf("cloud_tools", "project_management", "presentation_skills"),
            xpReward = 800
        ),

        // CIBERSEGURIDAD PROFESIONAL
        EducationalModule(
            id = "mod_prof_sec_01",
            title = "Seguridad Informática",
            description = "Protección avanzada de sistemas",
            phase = DigitalPhase.PROFESSIONAL,
            category = SkillCategory.SECURITY,
            lessons = listOf(
                LessonContent(
                    id = "less_prof_netsec_01",
                    title = "Seguridad de Redes",
                    duration = 40,
                    objectives = listOf("Firewalls", "VPN", "Protocolos"),
                    activities = listOf("Configuración", "Audit")
                ),
                LessonContent(
                    id = "less_prof_cryp_01",
                    title = "Criptografía",
                    duration = 45,
                    objectives = listOf("Cifrado", "Hashing", "Certificados"),
                    activities = listOf("Implementar AES", "Firmas")
                ),
                LessonContent(
                    id = "less_prof_pentest_01",
                    title = "Ethical Hacking",
                    duration = 50,
                    objectives = listOf("Vulnerabilidades", "Herramientas", "Reportes"),
                    activities = listOf("Scan", "Exploit", "Mitigación")
                )
            ),
            skills = listOf("network_security", "cryptography", "penetration_testing", "compliance"),
            xpReward = 1100
        )
    )

    // ═══════════════════════════════════════════════════════════════
    // FASE INNOVADORA (21+ años)
    // ═══════════════════════════════════════════════════════════════
    private fun getInnovatorModules(): List<EducationalModule> = listOf(
        // ARQUITECTURA DE IA
        EducationalModule(
            id = "mod_inno_ai_arch_01",
            title = "Arquitectura de Sistemas IA",
            description = "Diseña y despliega soluciones ML",
            phase = DigitalPhase.INNOVATOR,
            category = SkillCategory.AI_TECH,
            lessons = listOf(
                LessonContent(
                    id = "less_inno_arch_01",
                    title = "Diseño de Sistemas ML",
                    duration = 65,
                    objectives = listOf("MLOps", "Pipelines", "Monitoreo"),
                    activities = listOf("Arquitectura end-to-end", "CI/CD ML")
                ),
                LessonContent(
                    id = "less_inno_llm_01",
                    title = "Large Language Models",
                    duration = 70,
                    objectives = listOf("Transformers", "Fine-tuning", "RAG"),
                    activities = listOf("Deploy LLM", "Sistema RAG")
                ),
                LessonContent(
                    id = "less_inno_edge_01",
                    title = "Edge AI",
                    duration = 60,
                    objectives = listOf("Optimización", "TinyML", "IoT"),
                    activities = listOf("Modelo embebido", "Raspberry Pi")
                )
            ),
            skills = listOf("mlops", "llm_architecture", "edge_ai", "model_serving"),
            xpReward = 1500
        ),

        // BLOCKCHAIN Y WEB3
        EducationalModule(
            id = "mod_inno_block_01",
            title = "Blockchain Avanzado",
            description = "Desarrollo descentralizado",
            phase = DigitalPhase.INNOVATOR,
            category = SkillCategory.DIGITAL_FINANCE,
            lessons = listOf(
                LessonContent(
                    id = "less_inno_sol_01",
                    title = "Smart Contracts",
                    duration = 60,
                    objectives = listOf("Solidity", "Auditoría", "Deploy"),
                    activities = listOf("Escrow", "Votación", "DeFi")
                ),
                LessonContent(
                    id = "less_inno_dao_01",
                    title = "DAOs y Gobernanza",
                    duration = 55,
                    objectives = listOf("Estructura", "Votación", "Tesorería"),
                    activities = listOf("Crear DAO", "Propuesta")
                ),
                LessonContent(
                    id = "less_inno_zero_01",
                    title = "Zero Knowledge Proofs",
                    duration = 65,
                    objectives = listOf("Privacidad", "zk-SNARKs", "Aplicaciones"),
                    activities = listOf("Implementar", "Verificación")
                )
            ),
            skills = listOf("solidity", "smart_contracts", "dao", "zk_proofs"),
            xpReward = 1400
        ),

        // COMPUTACIÓN CUÁNTICA
        EducationalModule(
            id = "mod_inno_quant_01",
            title = "Computación Cuántica",
            description = "El futuro del procesamiento",
            phase = DigitalPhase.INNOVATOR,
            category = SkillCategory.AI_TECH,
            lessons = listOf(
                LessonContent(
                    id = "less_inno_qubit_01",
                    title = "Qubits y Superposición",
                    duration = 70,
                    objectives = listOf("Matemáticas", "Física", "Estados"),
                    activities = listOf("Simulación", "Qiskit")
                ),
                LessonContent(
                    id = "less_inno_algo_01",
                    title = "Algoritmos Cuánticos",
                    duration = 75,
                    objectives = listOf("Grover", "Shor", "Aplicaciones"),
                    activities = listOf("Implementar", "Benchmark")
                ),
                LessonContent(
                    id = "less_inno_quant_app_01",
                    title = "Aplicaciones Prácticas",
                    duration = 60,
                    objectives = listOf("Optimización", "Criptografía", "ML"),
                    activities = listOf("Problema real", "Comparativa")
                )
            ),
            skills = listOf("quantum_mechanics", "qiskit", "quantum_algorithms", "optimization"),
            xpReward = 1600
        ),

        // LIDERAZGO TECNOLÓGICO
        EducationalModule(
            id = "mod_inno_lead_01",
            title = "Liderazgo Tech",
            description = "Gestión de equipos y proyectos tech",
            phase = DigitalPhase.INNOVATOR,
            category = SkillCategory.LEADERSHIP,
            lessons = listOf(
                LessonContent(
                    id = "less_inno_team_01",
                    title = "Gestión de Equipos",
                    duration = 55,
                    objectives = listOf("Hiring", "1:1s", "Cultura"),
                    activities = listOf("Interview", "Feedback")
                ),
                LessonContent(
                    id = "less_inno_strat_01",
                    title = "Estrategia Tecnológica",
                    duration = 60,
                    objectives = listOf("Roadmap", "Arquitectura", "Innovación"),
                    activities = listOf("Planning", "Stakeholders")
                ),
                LessonContent(
                    id = "less_inno_scale_01",
                    title = "Escalabilidad",
                    duration = 65,
                    objectives = listOf("Microservicios", "Kubernetes", "DevOps"),
                    activities = listOf("Diseño", "Implementar")
                )
            ),
            skills = listOf("team_management", "tech_strategy", "scalability", "devops"),
            xpReward = 1200
        ),

        // ÉTICA Y FUTURO
        EducationalModule(
            id = "mod_inno_ethic_01",
            title = "Ética Tecnológica",
            description = "Responsabilidad y futuro de la humanidad",
            phase = DigitalPhase.INNOVATOR,
            category = SkillCategory.ETHICS,
            lessons = listOf(
                LessonContent(
                    id = "less_inno_ai_ethic_01",
                    title = "AI Alignment",
                    duration = 50,
                    objectives = listOf("Safety", "Control", "Regulación"),
                    activities = listOf("Debate", "Policy")
                ),
                LessonContent(
                    id = "less_inno_future_01",
                    title = "Futuros Posibles",
                    duration = 45,
                    objectives = listOf("Singularity", "Transhumanismo", "Sociedad"),
                    activities = listOf("Scenario planning", "Visión")
                ),
                LessonContent(
                    id = "less_inno_sustain_01",
                    title = "Tech Sostenible",
                    duration = 40,
                    objectives = listOf("Green computing", "Impacto", "SDGs"),
                    activities = listOf("Auditoría", "Mejora")
                )
            ),
            skills = listOf("ai_alignment", "futures_studies", "sustainability", "policy"),
            xpReward = 1000
        )
    )

    // ═══════════════════════════════════════════════════════════════
    // FUNCIONES DE BÚSQUEDA Y FILTRADO
    // ═══════════════════════════════════════════════════════════════

    fun getModuleById(id: String): EducationalModule? {
        return getAllModules(DigitalPhase.SENSORIAL).find { it.id == id }
            ?: getAllModules(DigitalPhase.CREATIVE).find { it.id == id }
            ?: getAllModules(DigitalPhase.PROFESSIONAL).find { it.id == id }
            ?: getAllModules(DigitalPhase.INNOVATOR).find { it.id == id }
    }

    fun getModulesByCategory(category: SkillCategory): List<EducationalModule> {
        return DigitalPhase.entries.flatMap { phase ->
            getAllModules(phase).filter { it.category == category }
        }
    }

    fun getTotalLessonsCount(): Int {
        return DigitalPhase.entries.sumOf { phase ->
            getAllModules(phase).sumOf { it.lessons.size }
        }
    }

    fun getTotalXpAvailable(): Long {
        return DigitalPhase.entries.sumOf { phase ->
            getAllModules(phase).sumOf { it.xpReward }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// DATA CLASSES
// ═══════════════════════════════════════════════════════════════

@Serializable
data class EducationalModule(
    val id: String,
    val title: String,
    val description: String,
    val phase: DigitalPhase,
    val category: SkillCategory,
    val lessons: List<LessonContent>,
    val skills: List<String>,
    val xpReward: Long,
    val prerequisites: List<String> = emptyList(),
    val estimatedHours: Int = lessons.sumOf { it.duration } / 60
)

@Serializable
data class LessonContent(
    val id: String,
    val title: String,
    val duration: Int, // minutos
    val objectives: List<String>,
    val activities: List<String>,
    val resources: List<String> = emptyList(),
    val assessments: List<String> = emptyList(),
    val xpReward: Long = 100L
)

@Serializable
enum class ContentType {
    VIDEO, ARTICLE, INTERACTIVE, QUIZ, PROJECT, SIMULATION
}
