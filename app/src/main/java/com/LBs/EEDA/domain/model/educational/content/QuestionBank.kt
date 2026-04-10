package com.LBs.EEDA.domain.model.educational.content

import com.LBs.EEDA.domain.model.educational.analytics.*
import com.LBs.EEDA.domain.model.educational.ContentDifficulty

/**
 * Banco de Preguntas Extenso EEDA 2026
 * Más de 500 preguntas organizadas por categorías y niveles
 */

object QuestionBank {
    
    fun getQuestionsForConcept(conceptId: String, difficulty: ContentDifficulty, count: Int): List<AdaptiveAssessmentQuestion> {
        val allQuestions = getAllQuestionsForConcept(conceptId)
        val filteredByDifficulty = filterByDifficulty(allQuestions, difficulty)
        return filteredByDifficulty.shuffled().take(count)
    }
    
    private fun getAllQuestionsForConcept(conceptId: String): List<AdaptiveAssessmentQuestion> {
        return when (conceptId) {
            "password_security", "security" -> passwordSecurityQuestions
            "encryption", "cryptography" -> encryptionQuestions
            "algorithm", "programming_logic" -> algorithmQuestions
            "network", "networking" -> networkQuestions
            "ai_ml", "artificial_intelligence" -> aiQuestions
            "cybersecurity", "hacking" -> cybersecurityQuestions
            "data_privacy", "privacy" -> privacyQuestions
            "emotional_intelligence" -> emotionalIntelligenceQuestions
            "critical_thinking" -> criticalThinkingQuestions
            "creativity" -> creativityQuestions
            "science", "biology", "physics", "chemistry" -> scienceQuestions
            "history", "historical" -> historyQuestions
            "finance", "economics", "money" -> financeQuestions
            else -> generateQuestions.getOrDefault(conceptId, generalQuestions)
        }
    }
    
    // ==================== PREGUNTAS DE SEGURIDAD DE CONTRASEÑAS (100+) ====================
    val passwordSecurityQuestions = listOf(
        // NIVEL BÁSICO
        AdaptiveAssessmentQuestion(
            id = "pwd_001",
            conceptId = "password_security",
            questionType = QuestionType.MULTIPLE_CHOICE,
            difficulty = ContentDifficulty.BEGINNER,
            content = QuestionContent(
                stem = "¿Cuál de estas contraseñas es la más segura?",
                options = listOf(
                    QuestionOption("a", "password123", false, DistractorType.COMMON_MISCONCEPTION, "sequential_pattern"),
                    QuestionOption("b", "MiPerroLuna2024!", true, null, null),
                    QuestionOption("c", "12345678", false, DistractorType.TOO_EXTREME, "common_pin"),
                    QuestionOption("d", "qwerty", false, DistractorType.PLAUSIBLE_ALTERNATIVE, "keyboard_pattern")
                ),
                correctAnswers = listOf("b"),
                explanation = "Una contraseña segura combina letras mayúsculas, minúsculas, números y símbolos. 'MiPerroLuna2024!' es fácil de recordar pero difícil de adivinar.",
                misconceptionExplanations = mapOf(
                    "sequential_pattern" to "Las contraseñas secuenciales son las primeras que prueban los hackers",
                    "common_pin" to "123456 es la contraseña más usada y la más insegura",
                    "keyboard_pattern" to "Los patrones de teclado son fáciles de detectar"
                )
            ),
            hints = listOf(
                AdaptiveHint("h1", HintLevel.SUBTLE, "Piensa en algo personal que combines con números y símbolos", null, listOf()),
                AdaptiveHint("h2", HintLevel.GUIDING, "Busca la que tenga mayúscula, minúscula, número y símbolo", null, listOf()),
                AdaptiveHint("h3", HintLevel.DIRECT, "MiPerroLuna2024! cumple todos los requisitos", null, listOf())
            ),
            timeLimitSeconds = 60,
            cognitiveLoadEstimate = CognitiveLoad.LOW,
            prerequisites = emptyList(),
            learningObjective = "Identificar características de contraseñas seguras",
            bloomsLevel = BloomsTaxonomyLevel.UNDERSTAND,
            estimatedDiscrimination = 0.7,
            estimatedDifficulty = 0.3
        ),
        
        AdaptiveAssessmentQuestion(
            id = "pwd_002",
            conceptId = "password_security",
            questionType = QuestionType.MULTIPLE_SELECT,
            difficulty = ContentDifficulty.ELEMENTARY,
            content = QuestionContent(
                stem = "Selecciona TODAS las prácticas que hacen una contraseña más segura:",
                options = listOf(
                    QuestionOption("a", "Usar al menos 12 caracteres", true, null, null),
                    QuestionOption("b", "Incluir números y símbolos", true, null, null),
                    QuestionOption("c", "Usar tu fecha de nacimiento", false, DistractorType.COMMON_MISCONCEPTION, "personal_info"),
                    QuestionOption("d", "Cambiarla regularmente", true, null, null),
                    QuestionOption("e", "Usar la misma en todos lados", false, DistractorType.OPOSITE_CORRECT, "password_reuse"),
                    QuestionOption("f", "Incluir palabras del diccionario", false, DistractorType.PLAUSIBLE_ALTERNATIVE, "dictionary_words")
                ),
                correctAnswers = listOf("a", "b", "d"),
                explanation = "Las contraseñas seguras son largas, complejas y únicas. Nunca uses información personal ni la misma contraseña en múltiples sitios.",
                misconceptionExplanations = mapOf(
                    "personal_info" to "La información personal es fácil de descubrir en redes sociales",
                    "password_reuse" to "Si una cuenta es hackeada, todas las demás quedan expuestas",
                    "dictionary_words" to "Los ataques de diccionario prueban palabras comunes primero"
                )
            ),
            hints = defaultHints("Contraseña segura"),
            timeLimitSeconds = 90,
            cognitiveLoadEstimate = CognitiveLoad.MEDIUM,
            prerequisites = listOf("pwd_001"),
            learningObjective = "Comprender elementos de contraseñas fuertes",
            bloomsLevel = BloomsTaxonomyLevel.UNDERSTAND,
            estimatedDiscrimination = 0.6,
            estimatedDifficulty = 0.5
        ),
        
        AdaptiveAssessmentQuestion(
            id = "pwd_003",
            conceptId = "password_security",
            questionType = QuestionType.TRUE_FALSE,
            difficulty = ContentDifficulty.BEGINNER,
            content = QuestionContent(
                stem = "Guardar contraseñas en un archivo de texto en tu computadora es seguro si el archivo está oculto.",
                options = null,
                correctAnswers = listOf("false"),
                explanation = "Los archivos ocultos no están encriptados. Cualquiera con acceso físico o malware puede leerlos. Usa un gestor de contraseñas en su lugar.",
                misconceptionExplanations = mapOf(
                    "hidden_security" to "Ocultar ≠ Proteger. Los archivos ocultos son fáciles de mostrar"
                )
            ),
            hints = listOf(
                AdaptiveHint("h1", HintLevel.SUBTLE, "¿Qué significa realmente 'oculto' en una computadora?", null, listOf())
            ),
            timeLimitSeconds = 45,
            cognitiveLoadEstimate = CognitiveLoad.LOW,
            prerequisites = emptyList(),
            learningObjective = "Reconocer prácticas inseguras de almacenamiento",
            bloomsLevel = BloomsTaxonomyLevel.REMEMBER,
            estimatedDiscrimination = 0.5,
            estimatedDifficulty = 0.4
        ),
        
        // NIVEL INTERMEDIO
        AdaptiveAssessmentQuestion(
            id = "pwd_004",
            conceptId = "password_security",
            questionType = QuestionType.CODE_COMPLETION,
            difficulty = ContentDifficulty.INTERMEDIATE,
            content = QuestionContent(
                stem = "Completa la función para verificar la fortaleza de una contraseña:",
                options = listOf(
                    QuestionOption("a", "return len(password) > 8", false, DistractorType.PARTIALLY_CORRECT, "length_only"),
                    QuestionOption("b", "return any(c.isdigit() for c in password)", false, DistractorType.PARTIALLY_CORRECT, "digits_only"),
                    QuestionOption("c", "return (len(password) >= 12 and any(c.isupper() for c in password) and any(c.islower() for c in password) and any(c.isdigit() for c in password) and any(c in '!@#$%^&*' for c in password))", true, null, null),
                    QuestionOption("d", "return password.isalnum()", false, DistractorType.OPOSITE_CORRECT, "alphanumeric_weak")
                ),
                correctAnswers = listOf("c"),
                explanation = "Una verificación completa incluye longitud mínima (12+), mayúsculas, minúsculas, números y símbolos especiales.",
                misconceptionExplanations = mapOf(
                    "length_only" to "La longitud sola no garantiza seguridad",
                    "digits_only" to "Solo verificar números es insuficiente",
                    "alphanumeric_weak" to "isalnum() rechaza símbolos, debilitando la contraseña"
                )
            ),
            hints = listOf(
                AdaptiveHint("h1", HintLevel.SUBTLE, "¿Cuántos criterios diferentes debe cumplir una contraseña fuerte?", null, listOf()),
                AdaptiveHint("h2", HintLevel.GUIDING, "Busca la opción que verifique: longitud, mayúsculas, minúsculas, números Y símbolos", null, listOf())
            ),
            timeLimitSeconds = 120,
            cognitiveLoadEstimate = CognitiveLoad.HIGH,
            prerequisites = listOf("pwd_001", "pwd_002"),
            learningObjective = "Implementar validación de fortaleza de contraseñas",
            bloomsLevel = BloomsTaxonomyLevel.APPLY,
            estimatedDiscrimination = 0.8,
            estimatedDifficulty = 0.6
        ),
        
        // NIVEL AVANZADO
        AdaptiveAssessmentQuestion(
            id = "pwd_005",
            conceptId = "password_security",
            questionType = QuestionType.MATCHING,
            difficulty = ContentDifficulty.ADVANCED,
            content = QuestionContent(
                stem = "Relaciona cada técnica de ataque con su método de protección:",
                options = listOf(
                    QuestionOption("a", "Fuerza bruta → Rate limiting + CAPTCHA", true, null, null),
                    QuestionOption("b", "Ataque de diccionario → Bloqueo después de intentos fallidos", true, null, null),
                    QuestionOption("c", "Ingeniería social → Educación y 2FA", true, null, null),
                    QuestionOption("d", "Keylogger → Antivirus + verificación biométrica", true, null, null),
                    QuestionOption("e", "Phishing → Filtros de email + verificación de URLs", true, null, null)
                ),
                correctAnswers = listOf("a", "b", "c", "d", "e"),
                explanation = "Cada vector de ataque requiere defensas específicas: técnicas para ataques automatizados, educación para manipulación humana, y herramientas para malware.",
                misconceptionExplanations = emptyMap()
            ),
            hints = defaultHints("Protección contra ataques"),
            timeLimitSeconds = 150,
            cognitiveLoadEstimate = CognitiveLoad.VERY_HIGH,
            prerequisites = listOf("pwd_001", "pwd_002", "pwd_004"),
            learningObjective = "Analizar vectores de ataque y contramedidas",
            bloomsLevel = BloomsTaxonomyLevel.ANALYZE,
            estimatedDiscrimination = 0.9,
            estimatedDifficulty = 0.75
        ),
        
        // MÁS PREGUNTAS DE CONTRASEÑAS...
        AdaptiveAssessmentQuestion(
            id = "pwd_006",
            conceptId = "password_security",
            questionType = QuestionType.SHORT_ANSWER,
            difficulty = ContentDifficulty.INTERMEDIATE,
            content = QuestionContent(
                stem = "Explica por qué 'CorrectHorseBatteryStaple' es más segura que 'Tr0ub4dor&3' aunque la segunda parezca más 'compleja'.",
                options = null,
                correctAnswers = listOf("entropía", "longitud", "frase", "cuatro palabras", "diceware"),
                explanation = "La contraseña de cuatro palabras comunes tiene ~44 bits de entropía vs ~28 bits de la 'compleja'. La entropía (aleatoriedad real) es más importante que la apariencia de complejidad.",
                misconceptionExplanations = mapOf(
                    "symbols_security" to "Los símbolos no garantizan seguridad si el patrón es predecible"
                )
            ),
            hints = listOf(
                AdaptiveHint("h1", HintLevel.SUBTLE, "¿Qué es más difícil de adivinar: 4 palabras aleatorias o 1 palabra con sustituciones?", null, listOf()),
                AdaptiveHint("h2", HintLevel.GUIDING, "Considera cuántas combinaciones posibles existen en cada caso", null, listOf())
            ),
            timeLimitSeconds = 180,
            cognitiveLoadEstimate = CognitiveLoad.HIGH,
            prerequisites = listOf("pwd_001"),
            learningObjective = "Comprender el concepto de entropía en contraseñas",
            bloomsLevel = BloomsTaxonomyLevel.EVALUATE,
            estimatedDiscrimination = 0.85,
            estimatedDifficulty = 0.7
        ),
        
        AdaptiveAssessmentQuestion(
            id = "pwd_007",
            conceptId = "password_security",
            questionType = QuestionType.SEQUENCING,
            difficulty = ContentDifficulty.ADVANCED,
            content = QuestionContent(
                stem = "Ordena los pasos para implementar autenticación segura en una aplicación web:",
                options = listOf(
                    QuestionOption("1", "Hash de contraseña con bcrypt/scrypt", false, null, null),
                    QuestionOption("2", "Validación de fortaleza de contraseña", false, null, null),
                    QuestionOption("3", "Implementar rate limiting", false, null, null),
                    QuestionOption("4", "Añadir 2FA opcional", false, null, null),
                    QuestionOption("5", "Logging de intentos de login", false, null, null)
                ),
                correctAnswers = listOf("2", "1", "3", "5", "4"),
                explanation = "Secuencia: 1) Validar entrada, 2) Proteger almacenamiento, 3) Prevenir ataques de fuerza bruta, 4) Monitorear, 5) Añadir capa adicional de seguridad.",
                misconceptionExplanations = emptyMap()
            ),
            hints = listOf(
                AdaptiveHint("h1", HintLevel.SUBTLE, "¿Qué debe pasar primero antes de almacenar una contraseña?", null, listOf())
            ),
            timeLimitSeconds = 120,
            cognitiveLoadEstimate = CognitiveLoad.HIGH,
            prerequisites = listOf("pwd_004"),
            learningObjective = "Diseñar arquitectura de autenticación segura",
            bloomsLevel = BloomsTaxonomyLevel.CREATE,
            estimatedDiscrimination = 0.8,
            estimatedDifficulty = 0.8
        ),
        
        AdaptiveAssessmentQuestion(
            id = "pwd_008",
            conceptId = "password_security",
            questionType = QuestionType.HOTSPOT,
            difficulty = ContentDifficulty.EXPERT,
            content = QuestionContent(
                stem = "En este código de autenticación, identifica las 3 vulnerabilidades críticas:",
                options = listOf(
                    QuestionOption("line_3", "Hash MD5 en lugar de bcrypt", true, null, "weak_hash"),
                    QuestionOption("line_7", "Contraseña en texto plano en logs", true, null, "plain_text_log"),
                    QuestionOption("line_12", "Sin rate limiting", true, null, "no_rate_limit"),
                    QuestionOption("line_15", "Uso de HTTPS", false, DistractorType.PLAUSIBLE_ALTERNATIVE, "actually_secure")
                ),
                correctAnswers = listOf("line_3", "line_7", "line_12"),
                explanation = "MD5 está roto desde 2004, nunca loguees contraseñas, y siempre implementa rate limiting para prevenir fuerza bruta.",
                misconceptionExplanations = mapOf(
                    "actually_secure" to "HTTPS es correcto, no es una vulnerabilidad"
                )
            ),
            hints = defaultHints("Vulnerabilidades"),
            timeLimitSeconds = 200,
            cognitiveLoadEstimate = CognitiveLoad.VERY_HIGH,
            prerequisites = listOf("pwd_005", "pwd_007"),
            learningObjective = "Identificar vulnerabilidades en implementaciones reales",
            bloomsLevel = BloomsTaxonomyLevel.ANALYZE,
            estimatedDiscrimination = 0.9,
            estimatedDifficulty = 0.85
        ),
        
        // PREGUNTAS DE DRAG AND DROP
        AdaptiveAssessmentQuestion(
            id = "pwd_009",
            conceptId = "password_security",
            questionType = QuestionType.DRAG_DROP,
            difficulty = ContentDifficulty.ELEMENTARY,
            content = QuestionContent(
                stem = "Arrastra cada elemento para construir una contraseña fuerte en el orden correcto:",
                options = listOf(
                    QuestionOption("step1", "Palabra base memorable (ej: Casa)", true, null, null),
                    QuestionOption("step2", "Añadir número personal (ej: 1995)", true, null, null),
                    QuestionOption("step3", "Símbolo especial (ej: !@#)", true, null, null),
                    QuestionOption("step4", "Mayúsculas variadas (ej: CaSa)", true, null, null)
                ),
                correctAnswers = listOf("step1", "step4", "step2", "step3"),
                explanation = "Estructura: base → variaciones de mayúsculas → números → símbolos. Ejemplo: CaSa1995!",
                misconceptionExplanations = emptyMap()
            ),
            hints = listOf(
                AdaptiveHint("h1", HintLevel.SUBTLE, "Empieza con algo memorable, luego añade complejidad gradualmente", null, listOf())
            ),
            timeLimitSeconds = 90,
            cognitiveLoadEstimate = CognitiveLoad.MEDIUM,
            prerequisites = emptyList(),
            learningObjective = "Construir contraseñas memorables y seguras",
            bloomsLevel = BloomsTaxonomyLevel.APPLY,
            estimatedDiscrimination = 0.6,
            estimatedDifficulty = 0.4
        ),
        
        // SIMULACIÓN INTERACTIVA
        AdaptiveAssessmentQuestion(
            id = "pwd_010",
            conceptId = "password_security",
            questionType = QuestionType.SIMULATION,
            difficulty = ContentDifficulty.INTERMEDIATE,
            content = QuestionContent(
                stem = "Simulación: Eres administrador de sistemas. Configura la política de contraseñas óptima para 1000 empleados.",
                options = listOf(
                    QuestionOption("min_length", "12 caracteres mínimo", true, null, null),
                    QuestionOption("complexity", "Requerir 3 de 4 grupos (may/min/num/sym)", true, null, null),
                    QuestionOption("rotation", "Cambio obligatorio cada 30 días", false, DistractorType.COMMON_MISCONCEPTION, "freq_rotation_bad"),
                    QuestionOption("reuse", "Bloquear últimas 10 contraseñas", true, null, null),
                    QuestionOption("2fa", "Mandatorio para acceso remoto", true, null, null)
                ),
                correctAnswers = listOf("min_length", "complexity", "reuse", "2fa"),
                explanation = "Rotación frecuente induce contraseñas peores. NIST recomienda: 8+ chars mínimo, bloqueo comunes, 2FA, y cambio solo si comprometida.",
                misconceptionExplanations = mapOf(
                    "freq_rotation_bad" to "Rotación forzada frecuente lleva a contraseñas predecibles (Password1, Password2...)"
                )
            ),
            hints = listOf(
                AdaptiveHint("h1", HintLevel.SUBTLE, "¿Qué recomienda NIST actualmente sobre rotación de contraseñas?", null, listOf())
            ),
            timeLimitSeconds = 180,
            cognitiveLoadEstimate = CognitiveLoad.VERY_HIGH,
            prerequisites = listOf("pwd_002", "pwd_005"),
            learningObjective = "Aplicar políticas de seguridad basadas en estándares actuales",
            bloomsLevel = BloomsTaxonomyLevel.EVALUATE,
            estimatedDiscrimination = 0.85,
            estimatedDifficulty = 0.7
        )
    ) + generateMorePasswordQuestions(90) // Generar 90 más para total 100
    
    // ==================== PREGUNTAS DE ENCRIPTACIÓN (100+) ====================
    val encryptionQuestions = listOf(
        AdaptiveAssessmentQuestion(
            id = "enc_001",
            conceptId = "encryption",
            questionType = QuestionType.MULTIPLE_CHOICE,
            difficulty = ContentDifficulty.BEGINNER,
            content = QuestionContent(
                stem = "¿Qué es el cifrado César?",
                options = listOf(
                    QuestionOption("a", "Un método de cifrado moderno usado en bancos", false, DistractorType.COMMON_MISCONCEPTION, "modern_encryption"),
                    QuestionOption("b", "Un técnica antigua que desplaza letras del alfabeto", true, null, null),
                    QuestionOption("c", "Un virus informático", false, DistractorType.IRRELEVANT, "confusion_malware"),
                    QuestionOption("d", "Un programa para hackear WiFi", false, DistractorType.IRRELEVANT, "hacking_tool")
                ),
                correctAnswers = listOf("b"),
                explanation = "El cifrado César, usado por Julio César hacia el 100 a.C., desplaza cada letra un número fijo de posiciones en el alfabeto.",
                misconceptionExplanations = mapOf(
                    "modern_encryption" to "El César es histórico, no se usa en sistemas modernos",
                    "confusion_malware" to "Cifrado ≠ Malware. El cifrado protege, el malware daña",
                    "hacking_tool" to "Conocer cifrado no es hackear"
                )
            ),
            hints = defaultHints("Cifrado César"),
            timeLimitSeconds = 60,
            cognitiveLoadEstimate = CognitiveLoad.LOW,
            prerequisites = emptyList(),
            learningObjective = "Comprender conceptos básicos de criptografía histórica",
            bloomsLevel = BloomsTaxonomyLevel.REMEMBER,
            estimatedDiscrimination = 0.5,
            estimatedDifficulty = 0.3
        ),
        
        AdaptiveAssessmentQuestion(
            id = "enc_002",
            conceptId = "encryption",
            questionType = QuestionType.CODE_COMPLETION,
            difficulty = ContentDifficulty.INTERMEDIATE,
            content = QuestionContent(
                stem = "Completa la función para descifrar un mensaje César con desplazamiento 3:",
                options = listOf(
                    QuestionOption("a", "return ''.join(chr(ord(c) + 3) for c in text)", false, DistractorType.OPOSITE_CORRECT, "encrypt_instead"),
                    QuestionOption("b", "return ''.join(chr(ord(c) - 3) for c in text)", true, null, null),
                    QuestionOption("c", "return text[::-1]", false, DistractorType.PLAUSIBLE_ALTERNATIVE, "reverse_only"),
                    QuestionOption("d", "return text.lower()", false, DistractorType.IRRELEVANT, "irrelevant_op")
                ),
                correctAnswers = listOf("b"),
                explanation = "Para descifrar, restamos el desplazamiento. Si ciframos sumando 3 (D=E), desciframos restando 3 (E=D).",
                misconceptionExplanations = mapOf(
                    "encrypt_instead" to "Eso cifraría, no descifraría",
                    "reverse_only" to "Revertir no deshace el desplazamiento",
                    "irrelevant_op" to "Minúsculas no afectan el desplazamiento"
                )
            ),
            hints = listOf(
                AdaptiveHint("h1", HintLevel.SUBTLE, "Si cifrar sumó, descifrar debe...", null, listOf())
            ),
            timeLimitSeconds = 90,
            cognitiveLoadEstimate = CognitiveLoad.MEDIUM,
            prerequisites = listOf("enc_001"),
            learningObjective = "Implementar algoritmo de cifrado César",
            bloomsLevel = BloomsTaxonomyLevel.APPLY,
            estimatedDiscrimination = 0.7,
            estimatedDifficulty = 0.5
        ),
        
        AdaptiveAssessmentQuestion(
            id = "enc_003",
            conceptId = "encryption",
            questionType = QuestionType.MATCHING,
            difficulty = ContentDifficulty.INTERMEDIATE,
            content = QuestionContent(
                stem = "Relaciona cada tipo de cifrado con su característica principal:",
                options = listOf(
                    QuestionOption("a", "Cifrado simétrico → Misma clave para cifrar y descifrar", true, null, null),
                    QuestionOption("b", "Cifrado asimétrico → Par de claves pública/privada", true, null, null),
                    QuestionOption("c", "Hash → Función unidireccional sin reversión", true, null, null),
                    QuestionOption("d", "Esteganografía → Ocultar mensaje dentro de otro archivo", true, null, null)
                ),
                correctAnswers = listOf("a", "b", "c", "d"),
                explanation = "Cada técnica criptográfica tiene propósitos distintos: confidencialidad (simétrico/asimétrico), integridad (hash), u ocultación (esteganografía).",
                misconceptionExplanations = emptyMap()
            ),
            hints = defaultHints("Tipos de cifrado"),
            timeLimitSeconds = 120,
            cognitiveLoadEstimate = CognitiveLoad.HIGH,
            prerequisites = listOf("enc_001"),
            learningObjective = "Diferenciar técnicas criptográficas",
            bloomsLevel = BloomsTaxonomyLevel.UNDERSTAND,
            estimatedDiscrimination = 0.75,
            estimatedDifficulty = 0.6
        ),
        
        AdaptiveAssessmentQuestion(
            id = "enc_004",
            conceptId = "encryption",
            questionType = QuestionType.TRUE_FALSE,
            difficulty = ContentDifficulty.ADVANCED,
            content = QuestionContent(
                stem = "Si tengo la clave pública de alguien, puedo descifrar mensajes que esa persona cifró con su clave privada.",
                options = null,
                correctAnswers = listOf("true"),
                explanation = "En cifrado asimétrico, lo cifrado con privada se descifra con pública (firma digital). Lo cifrado con pública solo se descifra con privada.",
                misconceptionExplanations = mapOf(
                    "public_decrypt" to "La clave pública descifra firmas, no mensajes privados"
                )
            ),
            hints = listOf(
                AdaptiveHint("h1", HintLevel.SUBTLE, "¿Para qué sirve cifrar con la clave privada?", null, listOf())
            ),
            timeLimitSeconds = 75,
            cognitiveLoadEstimate = CognitiveLoad.HIGH,
            prerequisites = listOf("enc_003"),
            learningObjective = "Comprender operaciones de cifrado asimétrico",
            bloomsLevel = BloomsTaxonomyLevel.UNDERSTAND,
            estimatedDiscrimination = 0.8,
            estimatedDifficulty = 0.7
        ),
        
        AdaptiveAssessmentQuestion(
            id = "enc_005",
            conceptId = "encryption",
            questionType = QuestionType.SHORT_ANSWER,
            difficulty = ContentDifficulty.EXPERT,
            content = QuestionContent(
                stem = "Explica por qué SHA-256 se considera seguro aunque existe el 'problema de las cumpleaños'.",
                options = null,
                correctAnswers = listOf("2^128", "colisión", "computacionalmente", "impracticable", "años"),
                explanation = "El ataque de cumpleaños requiere 2^128 operaciones para SHA-256. Incluso con computación cuántica, esto tomaría millones de años, haciéndolo computacionalmente seguro.",
                misconceptionExplanations = mapOf(
                    "theoretical_only" to "Aunque teóricamente posible, la probabilidad práctica es infinitesimal"
                )
            ),
            hints = listOf(
                AdaptiveHint("h1", HintLevel.SUBTLE, "¿Cuántas operaciones necesitas para encontrar una colisión SHA-256?", null, listOf())
            ),
            timeLimitSeconds = 240,
            cognitiveLoadEstimate = CognitiveLoad.VERY_HIGH,
            prerequisites = listOf("enc_003", "enc_004"),
            learningObjective = "Analizar seguridad de funciones hash criptográficas",
            bloomsLevel = BloomsTaxonomyLevel.EVALUATE,
            estimatedDiscrimination = 0.9,
            estimatedDifficulty = 0.85
        ),
        
        AdaptiveAssessmentQuestion(
            id = "enc_006",
            conceptId = "encryption",
            questionType = QuestionType.VISUAL_RECOGNITION,
            difficulty = ContentDifficulty.INTERMEDIATE,
            content = QuestionContent(
                stem = "Identifica en el diagrama: ¿Qué representa la 'caja negra' en el cifrado de bloques?",
                stimulus = StimulusType.DIAGRAM,
                options = listOf(
                    QuestionOption("a", "La función de cifrado/red SPN", true, null, null),
                    QuestionOption("b", "La clave privada", false, DistractorType.PLAUSIBLE_ALTERNATIVE, "key_confusion"),
                    QuestionOption("c", "El vector de inicialización", false, DistractorType.COMMON_MISCONCEPTION, "iv_confusion")
                ),
                correctAnswers = listOf("a"),
                explanation = "La 'caja negra' representa la red de permutación-sustitución (SPN) que transforma el texto plano usando la clave en múltiples rondas.",
                misconceptionExplanations = mapOf(
                    "key_confusion" to "La clave entra a la caja, pero no es la caja",
                    "iv_confusion" to "El IV se usa en modo CBC, no en la caja básica"
                )
            ),
            hints = defaultHints("Diagrama de cifrado"),
            timeLimitSeconds = 90,
            cognitiveLoadEstimate = CognitiveLoad.MEDIUM,
            prerequisites = listOf("enc_003"),
            learningObjective = "Interpretar diagramas de criptografía de bloques",
            bloomsLevel = BloomsTaxonomyLevel.UNDERSTAND,
            estimatedDiscrimination = 0.7,
            estimatedDifficulty = 0.6
        )
    ) + generateMoreEncryptionQuestions(94) // 94 más para total 100
    
    // ==================== PREGUNTAS DE ALGORITMOS (100+) ====================
    val algorithmQuestions = listOf(
        AdaptiveAssessmentQuestion(
            id = "alg_001",
            conceptId = "algorithm",
            questionType = QuestionType.SEQUENCING,
            difficulty = ContentDifficulty.BEGINNER,
            content = QuestionContent(
                stem = "Ordena los pasos para preparar un sándwich:",
                options = listOf(
                    QuestionOption("1", "Tomar dos rebanadas de pan", false, null, null),
                    QuestionOption("2", "Agregar ingredientes", false, null, null),
                    QuestionOption("3", "Untar mayonesa", false, null, null),
                    QuestionOption("4", "Cerrar con la otra rebanada", false, null, null),
                    QuestionOption("5", "Servir", false, null, null)
                ),
                correctAnswers = listOf("1", "3", "2", "4", "5"),
                explanation = "Algoritmo: 1) Pan base, 2) Base untable, 3) Relleno, 4) Cierre, 5) Output. Los algoritmos son secuencias precisas.",
                misconceptionExplanations = emptyMap()
            ),
            hints = listOf(
                AdaptiveHint("h1", HintLevel.SUBTLE, "¿Qué necesitas tener antes de poder untar nada?", null, listOf())
            ),
            timeLimitSeconds = 60,
            cognitiveLoadEstimate = CognitiveLoad.LOW,
            prerequisites = emptyList(),
            learningObjective = "Comprender concepto de algoritmo como secuencia",
            bloomsLevel = BloomsTaxonomyLevel.UNDERSTAND,
            estimatedDiscrimination = 0.4,
            estimatedDifficulty = 0.2
        ),
        
        AdaptiveAssessmentQuestion(
            id = "alg_002",
            conceptId = "algorithm",
            questionType = QuestionType.CODE_COMPLETION,
            difficulty = ContentDifficulty.ELEMENTARY,
            content = QuestionContent(
                stem = "Completa el algoritmo para encontrar el número más grande:",
                options = listOf(
                    QuestionOption("a", "return min(numbers)", false, DistractorType.OPOSITE_CORRECT, "opposite_func"),
                    QuestionOption("b", "return max(numbers)", true, null, null),
                    QuestionOption("c", "return sum(numbers)", false, DistractorType.PLAUSIBLE_ALTERNATIVE, "wrong_func"),
                    QuestionOption("d", "return len(numbers)", false, DistractorType.IRRELEVANT, "irrelevant_func")
                ),
                correctAnswers = listOf("b"),
                explanation = "max() devuelve el valor más alto. min() el más bajo, sum() la suma total, len() la cantidad de elementos.",
                misconceptionExplanations = mapOf(
                    "opposite_func" to "Eso devolvería el más pequeño",
                    "wrong_func" to "Eso sumaría todos los números",
                    "irrelevant_func" to "Eso cuenta cuántos hay, no cuál es más grande"
                )
            ),
            hints = listOf(
                AdaptiveHint("h1", HintLevel.DIRECT, "La función se llama 'max'", null, listOf())
            ),
            timeLimitSeconds = 45,
            cognitiveLoadEstimate = CognitiveLoad.LOW,
            prerequisites = emptyList(),
            learningObjective = "Usar funciones básicas de algoritmos",
            bloomsLevel = BloomsTaxonomyLevel.APPLY,
            estimatedDiscrimination = 0.5,
            estimatedDifficulty = 0.25
        ),
        
        AdaptiveAssessmentQuestion(
            id = "alg_003",
            conceptId = "algorithm",
            questionType = QuestionType.MULTIPLE_CHOICE,
            difficulty = ContentDifficulty.INTERMEDIATE,
            content = QuestionContent(
                stem = "¿Cuál es la complejidad temporal de buscar un elemento en una lista ordenada usando búsqueda binaria?",
                options = listOf(
                    QuestionOption("a", "O(1)", false, DistractorType.PARTIALLY_CORRECT, "constant_time_wrong"),
                    QuestionOption("b", "O(log n)", true, null, null),
                    QuestionOption("c", "O(n)", false, DistractorType.PLAUSIBLE_ALTERNATIVE, "linear_search_confusion"),
                    QuestionOption("d", "O(n²)", false, DistractorType.TOO_EXTREME, "quadratic_wrong")
                ),
                correctAnswers = listOf("b"),
                explanation = "Búsqueda binaria divide el espacio a la mitad cada vez: O(log n). Búsqueda lineal es O(n).",
                misconceptionExplanations = mapOf(
                    "constant_time_wrong" to "Solo hash tables tienen O(1) promedio",
                    "linear_search_confusion" to "Esa es búsqueda lineal, no binaria",
                    "quadratic_wrong" to "Burbuja y selección son O(n²), no búsqueda"
                )
            ),
            hints = listOf(
                AdaptiveHint("h1", HintLevel.SUBTLE, "Si divides el problema a la mitad cada vez, ¿cuántas veces puedes dividir n?", null, listOf()),
                AdaptiveHint("h2", HintLevel.GUIDING, "Recuerda: log₂(n) es cuántas veces divides n hasta llegar a 1", null, listOf())
            ),
            timeLimitSeconds = 90,
            cognitiveLoadEstimate = CognitiveLoad.HIGH,
            prerequisites = listOf("alg_001", "alg_002"),
            learningObjective = "Analizar complejidad de algoritmos de búsqueda",
            bloomsLevel = BloomsTaxonomyLevel.ANALYZE,
            estimatedDiscrimination = 0.8,
            estimatedDifficulty = 0.65
        ),
        
        AdaptiveAssessmentQuestion(
            id = "alg_004",
            conceptId = "algorithm",
            questionType = QuestionType.DRAG_DROP,
            difficulty = ContentDifficulty.INTERMEDIATE,
            content = QuestionContent(
                stem = "Construye el algoritmo Bubble Sort arrastrando los pasos en orden:",
                options = listOf(
                    QuestionOption("1", "Para cada elemento i en lista", false, null, null),
                    QuestionOption("2", "Para cada elemento j desde 0 hasta longitud-i-1", false, null, null),
                    QuestionOption("3", "Si lista[j] > lista[j+1]", false, null, null),
                    QuestionOption("4", "Intercambiar lista[j] y lista[j+1]", false, null, null),
                    QuestionOption("5", "Retornar lista ordenada", false, null, null)
                ),
                correctAnswers = listOf("1", "2", "3", "4", "5"),
                explanation = "Bubble sort: iterar sobre lista, comparar pares adyacentes, intercambiar si desordenados, repetir hasta ordenar.",
                misconceptionExplanations = emptyMap()
            ),
            hints = listOf(
                AdaptiveHint("h1", HintLevel.SUBTLE, "Primero los loops externos, luego la comparación, luego el intercambio", null, listOf())
            ),
            timeLimitSeconds = 120,
            cognitiveLoadEstimate = CognitiveLoad.HIGH,
            prerequisites = listOf("alg_002"),
            learningObjective = "Construir algoritmos de ordenamiento",
            bloomsLevel = BloomsTaxonomyLevel.APPLY,
            estimatedDiscrimination = 0.7,
            estimatedDifficulty = 0.6
        ),
        
        AdaptiveAssessmentQuestion(
            id = "alg_005",
            conceptId = "algorithm",
            questionType = QuestionType.SHORT_ANSWER,
            difficulty = ContentDifficulty.ADVANCED,
            content = QuestionContent(
                stem = "Compara Quick Sort y Merge Sort. ¿En qué situación preferirías cada uno y por qué?",
                options = null,
                correctAnswers = listOf("memoria", "in-place", "estable", "peor caso", "O(n log n)"),
                explanation = "Quick Sort: O(log n) espacio (in-place), no estable, O(n²) peor caso. Merge Sort: O(n) espacio, estable, garantizado O(n log n). Preferir Quick para memoria, Merge para estabilidad.",
                misconceptionExplanations = mapOf(
                    "quick_always_better" to "Quick sort tiene peor caso cuadrático"
                )
            ),
            hints = listOf(
                AdaptiveHint("h1", HintLevel.SUBTLE, "Considera: uso de memoria, estabilidad, y garantía de peor caso", null, listOf())
            ),
            timeLimitSeconds = 180,
            cognitiveLoadEstimate = CognitiveLoad.VERY_HIGH,
            prerequisites = listOf("alg_003", "alg_004"),
            learningObjective = "Evaluar y seleccionar algoritmos según contexto",
            bloomsLevel = BloomsTaxonomyLevel.EVALUATE,
            estimatedDiscrimination = 0.85,
            estimatedDifficulty = 0.75
        )
    ) + generateMoreAlgorithmQuestions(95) // 95 más para total 100
    
    // ==================== PREGUNTAS DE REDES (100+) ====================
    val networkQuestions = listOf(
        AdaptiveAssessmentQuestion(
            id = "net_001",
            conceptId = "network",
            questionType = QuestionType.MULTIPLE_CHOICE,
            difficulty = ContentDifficulty.BEGINNER,
            content = QuestionContent(
                stem = "¿Qué dispositivo conecta tu computadora a Internet en casa?",
                options = listOf(
                    QuestionOption("a", "Router/Modem", true, null, null),
                    QuestionOption("b", "Switch", false, DistractorType.COMMON_MISCONCEPTION, "switch_vs_router"),
                    QuestionOption("c", "Firewall", false, DistractorType.PLAUSIBLE_ALTERNATIVE, "security_confusion"),
                    QuestionOption("d", "Hub", false, DistractorType.PARTIALLY_CORRECT, "outdated_tech")
                ),
                correctAnswers = listOf("a"),
                explanation = "El router conecta tu red local a Internet. El switch conecta dispositivos locales. Firewall protege. Hub es obsoleto.",
                misconceptionExplanations = mapOf(
                    "switch_vs_router" to "Switch = local, Router = Internet",
                    "security_confusion" to "Firewall es software/protección, no conexión",
                    "outdated_tech" to "Hubs comparten ancho de banda, ya no se usan"
                )
            ),
            hints = defaultHints("Dispositivos de red"),
            timeLimitSeconds = 45,
            cognitiveLoadEstimate = CognitiveLoad.LOW,
            prerequisites = emptyList(),
            learningObjective = "Identificar componentes básicos de redes",
            bloomsLevel = BloomsTaxonomyLevel.REMEMBER,
            estimatedDiscrimination = 0.5,
            estimatedDifficulty = 0.3
        ),
        
        AdaptiveAssessmentQuestion(
            id = "net_002",
            conceptId = "network",
            questionType = QuestionType.MATCHING,
            difficulty = ContentDifficulty.ELEMENTARY,
            content = QuestionContent(
                stem = "Relaciona cada protocolo con su función:",
                options = listOf(
                    QuestionOption("a", "HTTP → Transferir páginas web", true, null, null),
                    QuestionOption("b", "FTP → Transferir archivos", true, null, null),
                    QuestionOption("c", "SMTP → Enviar correos", true, null, null),
                    QuestionOption("d", "DNS → Traducir nombres a IPs", true, null, null),
                    QuestionOption("e", "TCP → Transmisión confiable", true, null, null)
                ),
                correctAnswers = listOf("a", "b", "c", "d", "e"),
                explanation = "HTTP: web. FTP: archivos. SMTP: email saliente. DNS: resolución de nombres. TCP: transporte confiable.",
                misconceptionExplanations = emptyMap()
            ),
            hints = defaultHints("Protocolos"),
            timeLimitSeconds = 90,
            cognitiveLoadEstimate = CognitiveLoad.MEDIUM,
            prerequisites = emptyList(),
            learningObjective = "Comprender protocolos de red comunes",
            bloomsLevel = BloomsTaxonomyLevel.REMEMBER,
            estimatedDiscrimination = 0.6,
            estimatedDifficulty = 0.4
        ),
        
        AdaptiveAssessmentQuestion(
            id = "net_003",
            conceptId = "network",
            questionType = QuestionType.SIMULATION,
            difficulty = ContentDifficulty.INTERMEDIATE,
            content = QuestionContent(
                stem = "Simulación: Configura una subred para 500 dispositivos. ¿Qué máscara de subred eliges?",
                options = listOf(
                    QuestionOption("a", "/22 (255.255.252.0) - 1022 hosts", true, null, null),
                    QuestionOption("b", "/24 (255.255.255.0) - 254 hosts", false, DistractorType.TOO_EXTREME, "insufficient_hosts"),
                    QuestionOption("c", "/16 (255.255.0.0) - 65534 hosts", false, DistractorType.TOO_EXTREME, "wasteful"),
                    QuestionOption("d", "/30 (255.255.255.252) - 2 hosts", false, DistractorType.TOO_EXTREME, "point_to_point")
                ),
                correctAnswers = listOf("a"),
                explanation = "/22 ofrece 1022 hosts (2^10 - 2), suficiente para 500 con crecimiento. /24 solo da 254. /16 desperdicia IPs.",
                misconceptionExplanations = mapOf(
                    "insufficient_hosts" to "254 < 500, no alcanza",
                    "wasteful" to "65k IPs para 500 dispositivos es desperdicio",
                    "point_to_point" to "/30 es solo para conexiones entre 2 routers"
                )
            ),
            hints = listOf(
                AdaptiveHint("h1", HintLevel.SUBTLE, "¿Cuántos hosts necesitas? 2^(32-máscara) - 2", null, listOf())
            ),
            timeLimitSeconds = 120,
            cognitiveLoadEstimate = CognitiveLoad.HIGH,
            prerequisites = listOf("net_001"),
            learningObjective = "Calcular subnetting para requerimientos dados",
            bloomsLevel = BloomsTaxonomyLevel.APPLY,
            estimatedDiscrimination = 0.8,
            estimatedDifficulty = 0.65
        ),
        
        AdaptiveAssessmentQuestion(
            id = "net_004",
            conceptId = "network",
            questionType = QuestionType.HOTSPOT,
            difficulty = ContentDifficulty.ADVANCED,
            content = QuestionContent(
                stem = "En este diagrama de red, identifica el punto donde implementarías un firewall para máxima seguridad:",
                stimulus = StimulusType.DIAGRAM,
                options = listOf(
                    QuestionOption("point_a", "Entre ISP y Router perimetral", true, null, "optimal_placement"),
                    QuestionOption("point_b", "Dentro de la LAN", false, DistractorType.PARTIALLY_CORRECT, "internal_only"),
                    QuestionOption("point_c", "En cada estación de trabajo", false, DistractorType.TOO_EXTREME, "host_firewalls")
                ),
                correctAnswers = listOf("point_a"),
                explanation = "El firewall perimetral (entre ISP y red interna) filtra todo tráfico entrante/saliente antes de que llegue a la LAN.",
                misconceptionExplanations = mapOf(
                    "internal_only" to "Protege interno pero deja expuesta la red",
                    "host_firewalls" to "Host firewalls complementan, no reemplazan perimetral"
                )
            ),
            hints = listOf(
                AdaptiveHint("h1", HintLevel.GUIDING, "¿Dónde está el límite entre 'afuera' (Internet) y 'adentro' (tu red)?", null, listOf())
            ),
            timeLimitSeconds = 90,
            cognitiveLoadEstimate = CognitiveLoad.MEDIUM,
            prerequisites = listOf("net_001", "net_002"),
            learningObjective = "Diseñar arquitectura de seguridad de red",
            bloomsLevel = BloomsTaxonomyLevel.APPLY,
            estimatedDiscrimination = 0.7,
            estimatedDifficulty = 0.55
        ),
        
        AdaptiveAssessmentQuestion(
            id = "net_005",
            conceptId = "network",
            questionType = QuestionType.CODE_COMPLETION,
            difficulty = ContentDifficulty.EXPERT,
            content = QuestionContent(
                stem = "Completa la expresión regular para validar una IPv4:",
                options = listOf(
                    QuestionOption("a", "^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$", false, DistractorType.PARTIALLY_CORRECT, "allows_invalid"),
                    QuestionOption("b", "^(25[0-5]|2[0-4]\\d|1\\d{1,2}|[1-9]?\\d)(\\.(?!$)|$){4}$", true, null, null),
                    QuestionOption("c", "^\\d+\\.\\d+\\.\\d+\\.\\d+$", false, DistractorType.TOO_EXTREME, "too_permissive")
                ),
                correctAnswers = listOf("b"),
                explanation = "La opción b valida rangos correctos: 0-255 por octeto, exactamente 4 octetos, sin puntos al final. La 'a' acepta 999.999.999.999.",
                misconceptionExplanations = mapOf(
                    "allows_invalid" to "\\d{1,3} acepta 999, no es válido en IPv4",
                    "too_permissive" to "Cualquier número de dígitos es inválido"
                )
            ),
            hints = listOf(
                AdaptiveHint("h1", HintLevel.SUBTLE, "Cada octeto debe estar entre 0-255, no cualquier número de 1-3 dígitos", null, listOf())
            ),
            timeLimitSeconds = 180,
            cognitiveLoadEstimate = CognitiveLoad.VERY_HIGH,
            prerequisites = listOf("net_003"),
            learningObjective = "Implementar validación de direcciones IP",
            bloomsLevel = BloomsTaxonomyLevel.CREATE,
            estimatedDiscrimination = 0.9,
            estimatedDifficulty = 0.85
        )
    ) + generateMoreNetworkQuestions(95) // 95 más para total 100
    
    // ==================== PREGUNTAS DE INTELIGENCIA ARTIFICIAL (100+) ====================
    val aiQuestions = listOf(
        AdaptiveAssessmentQuestion(
            id = "ai_001",
            conceptId = "ai_ml",
            questionType = QuestionType.MULTIPLE_CHOICE,
            difficulty = ContentDifficulty.BEGINNER,
            content = QuestionContent(
                stem = "¿Qué es el 'machine learning'?",
                options = listOf(
                    QuestionOption("a", "Robots que aprenden solos como en las películas", false, DistractorType.COMMON_MISCONCEPTION, "sci_fi_confusion"),
                    QuestionOption("b", "Programas que mejoran su rendimiento con datos sin ser reprogramados", true, null, null),
                    QuestionOption("c", "Computadoras que piensan como humanos", false, DistractorType.COMMON_MISCONCEPTION, "agi_confusion"),
                    QuestionOption("d", "Un tipo de motor de búsqueda avanzado", false, DistractorType.PLAUSIBLE_ALTERNATIVE, "search_confusion")
                ),
                correctAnswers = listOf("b"),
                explanation = "Machine learning permite a programas aprender patrones de datos y mejorar sin intervención humana directa. No es IA general ni ciencia ficción.",
                misconceptionExplanations = mapOf(
                    "sci_fi_confusion" to "El ML es estadística avanzada, no conciencia artificial",
                    "agi_confusion" to "La IA general (pensar como humano) aún no existe",
                    "search_confusion" to "Los motores de búsqueda pueden usar ML, pero no son ML"
                )
            ),
            hints = defaultHints("Machine Learning"),
            timeLimitSeconds = 60,
            cognitiveLoadEstimate = CognitiveLoad.LOW,
            prerequisites = emptyList(),
            learningObjective = "Definir concepto básico de machine learning",
            bloomsLevel = BloomsTaxonomyLevel.REMEMBER,
            estimatedDiscrimination = 0.5,
            estimatedDifficulty = 0.3
        ),
        
        AdaptiveAssessmentQuestion(
            id = "ai_002",
            conceptId = "ai_ml",
            questionType = QuestionType.MATCHING,
            difficulty = ContentDifficulty.ELEMENTARY,
            content = QuestionContent(
                stem = "Relaciona cada tipo de ML con su ejemplo:",
                options = listOf(
                    QuestionOption("a", "Supervisado → Clasificar emails spam/no-spam", true, null, null),
                    QuestionOption("b", "No supervisado → Agrupar clientes por comportamiento", true, null, null),
                    QuestionOption("c", "Reforzamiento → AlphaGo aprendiendo Go", true, null, null),
                    QuestionOption("d", "Deep Learning → Reconocimiento facial", true, null, null)
                ),
                correctAnswers = listOf("a", "b", "c", "d"),
                explanation = "Supervisado: datos etiquetados. No supervisado: descubre patrones. Reforzamiento: prueba-error con recompensas. Deep Learning: redes neuronales profundas.",
                misconceptionExplanations = emptyMap()
            ),
            hints = defaultHints("Tipos de ML"),
            timeLimitSeconds = 90,
            cognitiveLoadEstimate = CognitiveLoad.MEDIUM,
            prerequisites = listOf("ai_001"),
            learningObjective = "Diferenciar tipos de aprendizaje automático",
            bloomsLevel = BloomsTaxonomyLevel.UNDERSTAND,
            estimatedDiscrimination = 0.6,
            estimatedDifficulty = 0.45
        ),
        
        AdaptiveAssessmentQuestion(
            id = "ai_003",
            conceptId = "ai_ml",
            questionType = QuestionType.TRUE_FALSE,
            difficulty = ContentDifficulty.INTERMEDIATE,
            content = QuestionContent(
                stem = "Un modelo de ML con 99% de precisión en datos de entrenamiento pero 60% en datos nuevos está 'sobreajustado' (overfitting).",
                options = null,
                correctAnswers = listOf("true"),
                explanation = "Overfitting: memoriza el entrenamiento pero no generaliza. La gran diferencia entre train/test accuracy es la señal clásica.",
                misconceptionExplanations = mapOf(
                    "accuracy_good" to "99% suena bien, pero la generalización es lo que importa"
                )
            ),
            hints = listOf(
                AdaptiveHint("h1", HintLevel.SUBTLE, "¿Qué significa que funcione bien en entrenamiento pero mal en datos nuevos?", null, listOf())
            ),
            timeLimitSeconds = 60,
            cognitiveLoadEstimate = CognitiveLoad.MEDIUM,
            prerequisites = listOf("ai_001", "ai_002"),
            learningObjective = "Identificar sobreajuste en modelos de ML",
            bloomsLevel = BloomsTaxonomyLevel.ANALYZE,
            estimatedDiscrimination = 0.7,
            estimatedDifficulty = 0.5
        ),
        
        AdaptiveAssessmentQuestion(
            id = "ai_004",
            conceptId = "ai_ml",
            questionType = QuestionType.CODE_COMPLETION,
            difficulty = ContentDifficulty.INTERMEDIATE,
            content = QuestionContent(
                stem = "Completa el código para prevenir overfitting usando regularización L2:",
                options = listOf(
                    QuestionOption("a", "model = LinearRegression()", false, DistractorType.COMMON_MISCONCEPTION, "no_regularization"),
                    QuestionOption("b", "model = Ridge(alpha=1.0)", true, null, null),
                    QuestionOption("c", "model = Lasso(alpha=1.0)", false, DistractorType.PARTIALLY_CORRECT, "l1_not_l2"),
                    QuestionOption("d", "model = LogisticRegression()", false, DistractorType.PLAUSIBLE_ALTERNATIVE, "wrong_model")
                ),
                correctAnswers = listOf("b"),
                explanation = "Ridge usa regularización L2 (penaliza cuadrados de coeficientes). Lasso es L1. LinearRegression sin regularización.",
                misconceptionExplanations = mapOf(
                    "no_regularization" to "LinearRegression no tiene regularización",
                    "l1_not_l2" to "Lasso es L1 (valor absoluto), Ridge es L2 (cuadrados)",
                    "wrong_model" to "LogisticRegression es para clasificación, no el objetivo"
                )
            ),
            hints = listOf(
                AdaptiveHint("h1", HintLevel.GUIDING, "Ridge Regression implementa regularización L2 (penalización cuadrática)", null, listOf())
            ),
            timeLimitSeconds = 90,
            cognitiveLoadEstimate = CognitiveLoad.MEDIUM,
            prerequisites = listOf("ai_003"),
            learningObjective = "Aplicar técnicas de regularización",
            bloomsLevel = BloomsTaxonomyLevel.APPLY,
            estimatedDiscrimination = 0.75,
            estimatedDifficulty = 0.6
        ),
        
        AdaptiveAssessmentQuestion(
            id = "ai_005",
            conceptId = "ai_ml",
            questionType = QuestionType.SHORT_ANSWER,
            difficulty = ContentDifficulty.ADVANCED,
            content = QuestionContent(
                stem = "Explica el dilema sesgo-varianza (bias-variance tradeoff) y cómo afecta la selección de modelos.",
                options = null,
                correctAnswers = listOf("subajuste", "sobreajuste", "complejidad", "generalización", "equilibrio"),
                explanation = "Alto sesgo = subajuste (modelo muy simple). Alta varianza = sobreajuste (modelo muy complejo). El objetivo es encontrar el equilibrio óptimo que minimice el error total.",
                misconceptionExplanations = mapOf(
                    "complexity_always_good" to "Más complejidad reduce sesgo pero aumenta varianza"
                )
            ),
            hints = listOf(
                AdaptiveHint("h1", HintLevel.SUBTLE, "Modelos muy simples vs modelos muy complejos: ¿qué error comete cada uno?", null, listOf())
            ),
            timeLimitSeconds = 180,
            cognitiveLoadEstimate = CognitiveLoad.VERY_HIGH,
            prerequisites = listOf("ai_003", "ai_004"),
            learningObjective = "Analizar compensación entre sesgo y varianza",
            bloomsLevel = BloomsTaxonomyLevel.ANALYZE,
            estimatedDiscrimination = 0.85,
            estimatedDifficulty = 0.75
        ),
        
        AdaptiveAssessmentQuestion(
            id = "ai_006",
            conceptId = "ai_ml",
            questionType = QuestionType.VISUAL_RECOGNITION,
            difficulty = ContentDifficulty.INTERMEDIATE,
            content = QuestionContent(
                stem = "En esta curva de aprendizaje, ¿qué indica la separación entre líneas de entrenamiento y validación?",
                stimulus = StimulusType.DIAGRAM,
                options = listOf(
                    QuestionOption("a", "Overfitting - el modelo memorizó el entrenamiento", true, null, null),
                    QuestionOption("b", "Underfitting - el modelo es muy simple", false, DistractorType.OPOSITE_CORRECT, "opposite_diagnosis"),
                    QuestionOption("c", "Datos insuficientes", false, DistractorType.PLAUSIBLE_ALTERNATIVE, "data_quantity")
                ),
                correctAnswers = listOf("a"),
                explanation = "Cuando train accuracy es mucho mayor que validation accuracy, hay overfitting. El modelo no generaliza.",
                misconceptionExplanations = mapOf(
                    "opposite_diagnosis" to "Underfitting muestra ambas líneas bajas y cercanas",
                    "data_quantity" to "Puede contribuir, pero la brecha indica overfitting específicamente"
                )
            ),
            hints = defaultHints("Curvas de aprendizaje"),
            timeLimitSeconds = 90,
            cognitiveLoadEstimate = CognitiveLoad.MEDIUM,
            prerequisites = listOf("ai_003"),
            learningObjective = "Interpretar gráficas de rendimiento de ML",
            bloomsLevel = BloomsTaxonomyLevel.ANALYZE,
            estimatedDiscrimination = 0.7,
            estimatedDifficulty = 0.6
        ),
        
        AdaptiveAssessmentQuestion(
            id = "ai_007",
            conceptId = "ai_ml",
            questionType = QuestionType.SIMULATION,
            difficulty = ContentDifficulty.EXPERT,
            content = QuestionContent(
                stem = "Simulación: Diseña un pipeline de ML para predecir abandono de clientes (churn). Elige los componentes:",
                options = listOf(
                    QuestionOption("a", "One-hot encoding para variables categóricas", true, null, null),
                    QuestionOption("b", "SMOTE para balancear clases", true, null, null),
                    QuestionOption("c", "Random Forest para clasificación", true, null, null),
                    QuestionOption("d", "Cross-validation estratificada", true, null, null),
                    QuestionOption("e", "Usar solo precisión como métrica", false, DistractorType.COMMON_MISCONCEPTION, "accuracy_only_bad")
                ),
                correctAnswers = listOf("a", "b", "c", "d"),
                explanation = "Churn requiere: encoding, balanceo de clases (desbalanceado), modelo robusto, validación apropiada. Precisión sola es engañosa en datos desbalanceados.",
                misconceptionExplanations = mapOf(
                    "accuracy_only_bad" to "Con churn del 5%, predecir 'no churn' siempre da 95% precisión"
                )
            ),
            hints = listOf(
                AdaptiveHint("h1", HintLevel.SUBTLE, "Churn es típicamente desbalanceado. ¿Qué métricas necesitas además de precisión?", null, listOf())
            ),
            timeLimitSeconds = 200,
            cognitiveLoadEstimate = CognitiveLoad.VERY_HIGH,
            prerequisites = listOf("ai_002", "ai_004", "ai_005"),
            learningObjective = "Diseñar pipelines de ML para problemas reales",
            bloomsLevel = BloomsTaxonomyLevel.CREATE,
            estimatedDiscrimination = 0.9,
            estimatedDifficulty = 0.85
        )
    ) + generateMoreAIQuestions(93) // 93 más para total 100
    
    // ==================== PREGUNTAS DE CIBERSEGURIDAD (100+) ====================
    val cybersecurityQuestions = listOf(
        AdaptiveAssessmentQuestion(
            id = "cyber_001",
            conceptId = "cybersecurity",
            questionType = QuestionType.MULTIPLE_CHOICE,
            difficulty = ContentDifficulty.BEGINNER,
            content = QuestionContent(
                stem = "¿Qué es el phishing?",
                options = listOf(
                    QuestionOption("a", "Un virus que destruye archivos", false, DistractorType.IRRELEVANT, "malware_confusion"),
                    QuestionOption("b", "Engaño para robar información personal", true, null, null),
                    QuestionOption("c", "Un ataque que bloquea la red", false, DistractorType.PLAUSIBLE_ALTERNATIVE, "ddos_confusion"),
                    QuestionOption("d", "Robo físico de computadoras", false, DistractorType.IRRELEVANT, "physical_theft")
                ),
                correctAnswers = listOf("b"),
                explanation = "Phishing: correos/mensajes falsos que engañan para revelar contraseñas, datos bancarios, etc. Es ingeniería social, no malware técnico.",
                misconceptionExplanations = mapOf(
                    "malware_confusion" to "Phishing es engaño humano, no software dañino",
                    "ddos_confusion" to "DDoS ataca disponibilidad, phishing ataca confidencialidad",
                    "physical_theft" to "Phishing es cibernético, no físico"
                )
            ),
            hints = defaultHints("Phishing"),
            timeLimitSeconds = 45,
            cognitiveLoadEstimate = CognitiveLoad.LOW,
            prerequisites = emptyList(),
            learningObjective = "Identificar definición de phishing",
            bloomsLevel = BloomsTaxonomyLevel.REMEMBER,
            estimatedDiscrimination = 0.5,
            estimatedDifficulty = 0.25
        ),
        
        AdaptiveAssessmentQuestion(
            id = "cyber_002",
            conceptId = "cybersecurity",
            questionType = QuestionType.MATCHING,
            difficulty = ContentDifficulty.ELEMENTARY,
            content = QuestionContent(
                stem = "Relaciona cada ataque con su descripción:",
                options = listOf(
                    QuestionOption("a", "Malware → Software diseñado para dañar o infiltrarse", true, null, null),
                    QuestionOption("b", "Ransomware → Cifra archivos y exige rescate", true, null, null),
                    QuestionOption("c", "Spyware → Espía actividad del usuario", true, null, null),
                    QuestionOption("d", "Troyano → Parece legítimo pero es dañino", true, null, null),
                    QuestionOption("e", "Gusano → Se propaga solo por la red", true, null, null)
                ),
                correctAnswers = listOf("a", "b", "c", "d", "e"),
                explanation = "Malware: categoría general. Ransomware: extorsión. Spyware: vigilancia. Troyano: engaño de apariencia. Gusano: auto-propagación.",
                misconceptionExplanations = emptyMap()
            ),
            hints = defaultHints("Tipos de malware"),
            timeLimitSeconds = 90,
            cognitiveLoadEstimate = CognitiveLoad.MEDIUM,
            prerequisites = listOf("cyber_001"),
            learningObjective = "Diferenciar tipos de malware",
            bloomsLevel = BloomsTaxonomyLevel.REMEMBER,
            estimatedDiscrimination = 0.6,
            estimatedDifficulty = 0.4
        ),
        
        AdaptiveAssessmentQuestion(
            id = "cyber_003",
            conceptId = "cybersecurity",
            questionType = QuestionType.SIMULATION,
            difficulty = ContentDifficulty.INTERMEDIATE,
            content = QuestionContent(
                stem = "Simulación: Recibes un email de 'tu banco' pidiendo verificar tu cuenta. ¿Qué haces?",
                options = listOf(
                    QuestionOption("a", "Verificar el remitente: banco-real@banco.com vs banco@banco-secure-update.com", true, null, null),
                    QuestionOption("b", "No hacer clic en links, ir directo al sitio web oficial", true, null, null),
                    QuestionOption("c", "Verificar que el email use tu nombre completo, no 'Estimado cliente'", true, null, null),
                    QuestionOption("d", "Llamar al número de teléfono oficial del banco para confirmar", true, null, null),
                    QuestionOption("e", "Hacer clic para verificar rápido antes de que cierren la cuenta", false, DistractorType.COMMON_MISCONCEPTION, "urgency_fallacy")
                ),
                correctAnswers = listOf("a", "b", "c", "d"),
                explanation = "Verificar remitente, evitar links (ir directo), personalización, y confirmar por canal oficial. La urgencia es señal de phishing.",
                misconceptionExplanations = mapOf(
                    "urgency_fallacy" to "La urgencia artificial es táctica clásica de phishing"
                )
            ),
            hints = listOf(
                AdaptiveHint("h1", HintLevel.SUBTLE, "¿Cómo verificarías si un email es real sin arriesgar tu información?", null, listOf())
            ),
            timeLimitSeconds = 120,
            cognitiveLoadEstimate = CognitiveLoad.MEDIUM,
            prerequisites = listOf("cyber_001"),
            learningObjective = "Aplicar técnicas de detección de phishing",
            bloomsLevel = BloomsTaxonomyLevel.APPLY,
            estimatedDiscrimination = 0.7,
            estimatedDifficulty = 0.5
        ),
        
        AdaptiveAssessmentQuestion(
            id = "cyber_004",
            conceptId = "cybersecurity",
            questionType = QuestionType.CODE_COMPLETION,
            difficulty = ContentDifficulty.INTERMEDIATE,
            content = QuestionContent(
                stem = "Completa el código para prevenir SQL Injection usando prepared statements:",
                options = listOf(
                    QuestionOption("a", "query = f\"SELECT * FROM users WHERE id = {user_id}\"", false, DistractorType.COMMON_MISCONCEPTION, "f_string_vulnerable"),
                    QuestionOption("b", "cursor.execute(\"SELECT * FROM users WHERE id = ?\", (user_id,))", true, null, null),
                    QuestionOption("c", "query = \"SELECT * FROM users WHERE id = \" + user_id", false, DistractorType.OPOSITE_CORRECT, "concatenation_vulnerable")
                ),
                correctAnswers = listOf("b"),
                explanation = "Prepared statements con parámetros (?) separan código de datos, previniendo inyección. La concatenación es vulnerable.",
                misconceptionExplanations = mapOf(
                    "f_string_vulnerable" to "F-strings son concatenación con formato, igual de vulnerables",
                    "concatenation_vulnerable" to "Concatenación directa permite inyección SQL"
                )
            ),
            hints = listOf(
                AdaptiveHint("h1", HintLevel.GUIDING, "Los '?' son placeholders que el motor de base de datos trata como datos, no como código", null, listOf())
            ),
            timeLimitSeconds = 90,
            cognitiveLoadEstimate = CognitiveLoad.HIGH,
            prerequisites = listOf("cyber_002"),
            learningObjective = "Implementar defensa contra SQL Injection",
            bloomsLevel = BloomsTaxonomyLevel.APPLY,
            estimatedDiscrimination = 0.8,
            estimatedDifficulty = 0.65
        ),
        
        AdaptiveAssessmentQuestion(
            id = "cyber_005",
            conceptId = "cybersecurity",
            questionType = QuestionType.HOTSPOT,
            difficulty = ContentDifficulty.ADVANCED,
            content = QuestionContent(
                stem = "En este código, identifica la vulnerabilidad de XSS:",
                stimulus = StimulusType.DIAGRAM,
                options = listOf(
                    QuestionOption("line_2", "document.write(userInput)", true, null, "dom_xss"),
                    QuestionOption("line_5", "innerHTML sin sanitización", true, null, "innerhtml_xss"),
                    QuestionOption("line_8", "eval(userData)", true, null, "eval_dangerous"),
                    QuestionOption("line_12", "textContent = userInput", false, DistractorType.PLAUSIBLE_ALTERNATIVE, "textcontent_safe")
                ),
                correctAnswers = listOf("line_2", "line_5", "line_8"),
                explanation = "XSS ocurre cuando input del usuario se ejecuta como código. document.write, innerHTML, y eval son peligrosos. textContent escapa HTML automáticamente.",
                misconceptionExplanations = mapOf(
                    "textcontent_safe" to "textContent es seguro - trata todo como texto plano"
                )
            ),
            hints = listOf(
                AdaptiveHint("h1", HintLevel.SUBTLE, "Busca donde el input del usuario puede ejecutarse como código JavaScript", null, listOf())
            ),
            timeLimitSeconds = 120,
            cognitiveLoadEstimate = CognitiveLoad.HIGH,
            prerequisites = listOf("cyber_004"),
            learningObjective = "Identificar vulnerabilidades XSS en código",
            bloomsLevel = BloomsTaxonomyLevel.ANALYZE,
            estimatedDiscrimination = 0.85,
            estimatedDifficulty = 0.7
        ),
        
        AdaptiveAssessmentQuestion(
            id = "cyber_006",
            conceptId = "cybersecurity",
            questionType = QuestionType.SHORT_ANSWER,
            difficulty = ContentDifficulty.EXPERT,
            content = QuestionContent(
                stem = "Diseña una estrategia de defensa en profundidad (defense in depth) para proteger datos médicos sensibles (PHI).",
                options = null,
                correctAnswers = listOf("capas", "encriptación", "acceso", "monitoreo", "auditoría", "2fa", "segmentación"),
                explanation = "Defense in depth: múltiples capas - encriptación en reposo y tránsito, controles de acceso estrictos (RBAC), monitoreo continuo, auditorías, 2FA, segmentación de red, y políticas de seguridad.",
                misconceptionExplanations = mapOf(
                    "firewall_only" to "Un solo firewall no es suficiente para datos médicos"
                )
            ),
            hints = listOf(
                AdaptiveHint("h1", HintLevel.SUBTLE, "Si un atacante supera una defensa, ¿qué otras capas lo detienen?", null, listOf())
            ),
            timeLimitSeconds = 240,
            cognitiveLoadEstimate = CognitiveLoad.VERY_HIGH,
            prerequisites = listOf("cyber_003", "cyber_004", "cyber_005"),
            learningObjective = "Diseñar arquitectura de seguridad multicapa",
            bloomsLevel = BloomsTaxonomyLevel.CREATE,
            estimatedDiscrimination = 0.9,
            estimatedDifficulty = 0.85
        )
    ) + generateMoreCybersecurityQuestions(94) // 94 más para total 100
    
    // ==================== PREGUNTAS DE PRIVACIDAD DE DATOS (100+) ====================
    val privacyQuestions = listOf(
        AdaptiveAssessmentQuestion(
            id = "priv_001",
            conceptId = "data_privacy",
            questionType = QuestionType.MULTIPLE_CHOICE,
            difficulty = ContentDifficulty.BEGINNER,
            content = QuestionContent(
                stem = "¿Qué son los 'cookies' en un sitio web?",
                options = listOf(
                    QuestionOption("a", "Viruses que infectan tu computadora", false, DistractorType.COMMON_MISCONCEPTION, "malware_confusion"),
                    QuestionOption("b", "Pequeños archivos que guardan información de tu visita", true, null, null),
                    QuestionOption("c", "Programas para bloquear publicidad", false, DistractorType.PLAUSIBLE_ALTERNATIVE, "adblocker_confusion"),
                    QuestionOption("d", "Contraseñas automáticas", false, DistractorType.PARTIALLY_CORRECT, "partial_confusion")
                ),
                correctAnswers = listOf("b"),
                explanation = "Cookies son datos que sitios web guardan en tu navegador para recordar preferencias, sesiones, carritos de compra, etc.",
                misconceptionExplanations = mapOf(
                    "malware_confusion" to "Las cookies son datos, no programas ejecutables",
                    "adblocker_confusion" to "Los bloqueadores pueden eliminar cookies, pero no son cookies",
                    "partial_confusion" to "Las cookies pueden recordar sesiones, pero no son contraseñas"
                )
            ),
            hints = defaultHints("Cookies"),
            timeLimitSeconds = 45,
            cognitiveLoadEstimate = CognitiveLoad.LOW,
            prerequisites = emptyList(),
            learningObjective = "Definir qué son las cookies web",
            bloomsLevel = BloomsTaxonomyLevel.REMEMBER,
            estimatedDiscrimination = 0.5,
            estimatedDifficulty = 0.3
        ),
        
        AdaptiveAssessmentQuestion(
            id = "priv_002",
            conceptId = "data_privacy",
            questionType = QuestionType.TRUE_FALSE,
            difficulty = ContentDifficulty.ELEMENTARY,
            content = QuestionContent(
                stem = "Borrar el historial del navegador elimina todos los rastros de tu actividad online.",
                options = null,
                correctAnswers = listOf("false"),
                explanation = "Tu ISP, sitios web, servidores DNS, y compañías de publicidad aún tienen registros. Borrar historial solo afecta tu dispositivo local.",
                misconceptionExplanations = mapOf(
                    "local_only" to "El historial local es solo una copia de muchos registros"
                )
            ),
            hints = listOf(
                AdaptiveHint("h1", HintLevel.SUBTLE, "¿Quién más podría tener registro de los sitios que visitas?", null, listOf())
            ),
            timeLimitSeconds = 60,
            cognitiveLoadEstimate = CognitiveLoad.MEDIUM,
            prerequisites = listOf("priv_001"),
            learningObjective = "Comprender alcance real de privacidad del historial",
            bloomsLevel = BloomsTaxonomyLevel.UNDERSTAND,
            estimatedDiscrimination = 0.6,
            estimatedDifficulty = 0.45
        ),
        
        AdaptiveAssessmentQuestion(
            id = "priv_003",
            conceptId = "data_privacy",
            questionType = QuestionType.MATCHING,
            difficulty = ContentDifficulty.INTERMEDIATE,
            content = QuestionContent(
                stem = "Relaciona cada tecnología de privacidad con su función:",
                options = listOf(
                    QuestionOption("a", "VPN → Encriptar conexión y ocultar IP", true, null, null),
                    QuestionOption("b", "Tor → Anonimato mediante enrutamiento en cebolla", true, null, null),
                    QuestionOption("c", "Do Not Track → Solicitar no ser rastreado (no garantizado)", true, null, null),
                    QuestionOption("d", "HTTPS → Encriptar datos en tránsito", true, null, null),
                    QuestionOption("e", "Ad blocker → Bloquear scripts de rastreo", true, null, null)
                ),
                correctAnswers = listOf("a", "b", "c", "d", "e"),
                explanation = "Cada tecnología aborda diferentes aspectos: VPN (privacidad de ISP), Tor (anonimato), DNT (petición), HTTPS (seguridad), Ad blocker (prevención de rastreo).",
                misconceptionExplanations = emptyMap()
            ),
            hints = defaultHints("Tecnologías de privacidad"),
            timeLimitSeconds = 90,
            cognitiveLoadEstimate = CognitiveLoad.MEDIUM,
            prerequisites = listOf("priv_001", "priv_002"),
            learningObjective = "Diferenciar tecnologías de privacidad",
            bloomsLevel = BloomsTaxonomyLevel.UNDERSTAND,
            estimatedDiscrimination = 0.65,
            estimatedDifficulty = 0.5
        ),
        
        AdaptiveAssessmentQuestion(
            id = "priv_004",
            conceptId = "data_privacy",
            questionType = QuestionType.SIMULATION,
            difficulty = ContentDifficulty.INTERMEDIATE,
            content = QuestionContent(
                stem = "Simulación: Configura las opciones de privacidad de una app de redes sociales. ¿Qué activas/desactivas?",
                options = listOf(
                    QuestionOption("a", "Desactivar ubicación precisa", true, null, null),
                    QuestionOption("b", "Desactivar acceso a contactos", true, null, null),
                    QuestionOption("c", "Limitar quién ve tus publicaciones", true, null, null),
                    QuestionOption("d", "Desactivar publicidad personalizada", true, null, null),
                    QuestionOption("e", "Permitir que todos encuentren tu perfil por email", false, DistractorType.COMMON_MISCONCEPTION, "discoverability_risk")
                ),
                correctAnswers = listOf("a", "b", "c", "d"),
                explanation = "Configuración privacidad-respetuosa: limitar datos sensibles (ubicación, contactos), controlar audiencia, rechazar tracking para publicidad.",
                misconceptionExplanations = mapOf(
                    "discoverability_risk" to "Permitir búsqueda por email facilita stalking y doxxing"
                )
            ),
            hints = listOf(
                AdaptiveHint("h1", HintLevel.SUBTLE, "¿Qué información personal no es necesaria para el funcionamiento básico de la app?", null, listOf())
            ),
            timeLimitSeconds = 120,
            cognitiveLoadEstimate = CognitiveLoad.MEDIUM,
            prerequisites = listOf("priv_003"),
            learningObjective = "Configurar ajustes de privacidad en aplicaciones",
            bloomsLevel = BloomsTaxonomyLevel.APPLY,
            estimatedDiscrimination = 0.7,
            estimatedDifficulty = 0.55
        ),
        
        AdaptiveAssessmentQuestion(
            id = "priv_005",
            conceptId = "data_privacy",
            questionType = QuestionType.SHORT_ANSWER,
            difficulty = ContentDifficulty.ADVANCED,
            content = QuestionContent(
                stem = "Explica el concepto de 'privacy by design' y da un ejemplo en el desarrollo de software.",
                options = null,
                correctAnswers = listOf("proactivo", "predeterminado", "embebido", "positivo", "minimización", "GDPR"),
                explanation = "Privacy by Design: 1) Proactivo, no reactivo. 2) Privacidad como predeterminado. 3) Embebido en diseño. 4) Positivo-sumatorio. 5) Seguridad end-to-end. 6) Minimización de datos. 7) Respeto al usuario.",
                misconceptionExplanations = mapOf(
                    "bolt_on" to "Privacidad no es un 'add-on', debe ser fundamental"
                )
            ),
            hints = listOf(
                AdaptiveHint("h1", HintLevel.SUBTLE, "¿Cómo sería diferente una app si la privacidad fuera su principio fundamental de diseño?", null, listOf())
            ),
            timeLimitSeconds = 180,
            cognitiveLoadEstimate = CognitiveLoad.HIGH,
            prerequisites = listOf("priv_004"),
            learningObjective = "Aplicar principios de privacy by design",
            bloomsLevel = BloomsTaxonomyLevel.APPLY,
            estimatedDiscrimination = 0.8,
            estimatedDifficulty = 0.7
        )
    ) + generateMorePrivacyQuestions(95) // 95 más para total 100
    
    // Preguntas de inteligencia emocional
    val emotionalIntelligenceQuestions = listOf(
        AdaptiveAssessmentQuestion(
            id = "ei_001",
            conceptId = "emotional_intelligence",
            questionType = QuestionType.MULTIPLE_CHOICE,
            difficulty = ContentDifficulty.BEGINNER,
            content = QuestionContent(
                stem = "¿Qué significa 'empatía'?",
                options = listOf(
                    QuestionOption("a", "Sentir lástima por alguien", false, DistractorType.COMMON_MISCONCEPTION, "sympathy_confusion"),
                    QuestionOption("b", "Ponerse en el lugar de otro y comprender sus sentimientos", true, null, null),
                    QuestionOption("c", "Dar consejos a los demás", false, DistractorType.PLAUSIBLE_ALTERNATIVE, "advice_giving"),
                    QuestionOption("d", "Evitar conflictos a toda costa", false, DistractorType.IRRELEVANT, "conflict_avoidance")
                ),
                correctAnswers = listOf("b"),
                explanation = "Empatía es comprender/compartir sentimientos de otro. Diferente de simpatía (lástima), consejos (resolver), o evasión.",
                misconceptionExplanations = mapOf(
                    "sympathy_confusion" to "Simpatía = 'te compadezco'. Empatía = 'comprendo lo que sientes'",
                    "advice_giving" to "Dar consejos sin comprender primero es prescripción sin diagnóstico"
                )
            ),
            hints = defaultHints("Empatía"),
            timeLimitSeconds = 45,
            cognitiveLoadEstimate = CognitiveLoad.LOW,
            prerequisites = emptyList(),
            learningObjective = "Definir empatía correctamente",
            bloomsLevel = BloomsTaxonomyLevel.REMEMBER,
            estimatedDiscrimination = 0.5,
            estimatedDifficulty = 0.3
        )
    ) + generateMoreEmotionalIntelligenceQuestions(99)
    
    // Preguntas de pensamiento crítico
    val criticalThinkingQuestions = listOf(
        AdaptiveAssessmentQuestion(
            id = "ct_001",
            conceptId = "critical_thinking",
            questionType = QuestionType.MULTIPLE_CHOICE,
            difficulty = ContentDifficulty.ELEMENTARY,
            content = QuestionContent(
                stem = "¿Cuál es un ejemplo de 'sesgo de confirmación'?",
                options = listOf(
                    QuestionOption("a", "Buscar información que confirma lo que ya crees", true, null, null),
                    QuestionOption("b", "Cambiar de opinión al ver nueva evidencia", false, DistractorType.OPOSITE_CORRECT, "open_minded"),
                    QuestionOption("c", "Pedir opinión de expertos", false, DistractorType.PLAUSIBLE_ALTERNATIVE, "expert_opinion"),
                    QuestionOption("d", "Analizar pros y contras", false, DistractorType.PLAUSIBLE_ALTERNATIVE, "balanced_analysis")
                ),
                correctAnswers = listOf("a"),
                explanation = "Sesgo de confirmación: buscar, interpretar y recordar información que confirma creencias preexistentes, ignorando la contradictoria.",
                misconceptionExplanations = mapOf(
                    "open_minded" to "Eso es pensamiento crítico sano, no un sesgo",
                    "expert_opinion" to "Consultar expertos puede ser sesgo de autoridad, no confirmación"
                )
            ),
            hints = defaultHints("Sesgos cognitivos"),
            timeLimitSeconds = 60,
            cognitiveLoadEstimate = CognitiveLoad.MEDIUM,
            prerequisites = emptyList(),
            learningObjective = "Identificar sesgo de confirmación",
            bloomsLevel = BloomsTaxonomyLevel.UNDERSTAND,
            estimatedDiscrimination = 0.6,
            estimatedDifficulty = 0.45
        )
    ) + generateMoreCriticalThinkingQuestions(99)
    
    // Preguntas de creatividad
    val creativityQuestions = listOf(
        AdaptiveAssessmentQuestion(
            id = "cre_001",
            conceptId = "creativity",
            questionType = QuestionType.MULTIPLE_CHOICE,
            difficulty = ContentDifficulty.BEGINNER,
            content = QuestionContent(
                stem = "¿Qué técnica de creatividad consiste en listar muchas ideas sin juzgarlas?",
                options = listOf(
                    QuestionOption("a", "Tormenta de ideas (brainstorming)", true, null, null),
                    QuestionOption("b", "Análisis SWOT", false, DistractorType.PLAUSIBLE_ALTERNATIVE, "swot_confusion"),
                    QuestionOption("c", "Mapa mental", false, DistractorType.PARTIALLY_CORRECT, "mindmap_related"),
                    QuestionOption("d", "Pensamiento lateral", false, DistractorType.PARTIALLY_CORRECT, "lateral_related")
                ),
                correctAnswers = listOf("a"),
                explanation = "Brainstorming: generación de ideas en cantidad sin censura inicial. 'Divergir' antes de 'converger'.",
                misconceptionExplanations = mapOf(
                    "swot_confusion" to "SWOT analiza fortalezas/debilidades, no genera ideas",
                    "mindmap_related" to "Mind map organiza ideas, brainstorming las genera",
                    "lateral_related" to "Lateral thinking es enfoque, brainstorming es técnica"
                )
            ),
            hints = defaultHints("Creatividad"),
            timeLimitSeconds = 45,
            cognitiveLoadEstimate = CognitiveLoad.LOW,
            prerequisites = emptyList(),
            learningObjective = "Identificar técnicas de generación de ideas",
            bloomsLevel = BloomsTaxonomyLevel.REMEMBER,
            estimatedDiscrimination = 0.5,
            estimatedDifficulty = 0.35
        )
    ) + generateMoreCreativityQuestions(99)
    
    // Preguntas de ciencia
    val scienceQuestions = listOf(
        AdaptiveAssessmentQuestion(
            id = "sci_001",
            conceptId = "science",
            questionType = QuestionType.MULTIPLE_CHOICE,
            difficulty = ContentDifficulty.BEGINNER,
            content = QuestionContent(
                stem = "¿Por qué el cielo es azul durante el día?",
                options = listOf(
                    QuestionOption("a", "Refleja el color del océano", false, DistractorType.COMMON_MISCONCEPTION, "ocean_reflection"),
                    QuestionOption("b", "La dispersión Rayleigh dispersa más la luz azul", true, null, null),
                    QuestionOption("c", "La atmósfera es de color azul", false, DistractorType.COMMON_MISCONCEPTION, "atmosphere_color"),
                    QuestionOption("d", "El sol emite luz azul", false, DistractorType.PARTIALLY_CORRECT, "sun_emits")
                ),
                correctAnswers = listOf("b"),
                explanation = "Dispersión Rayleigh: moléculas atmosféricas dispersan luz de menor longitud de onda (azul/violeta) más que roja. Por eso el cielo es azul y atardecer rojo.",
                misconceptionExplanations = mapOf(
                    "ocean_reflection" to "El océano refleja el cielo, no al revés",
                    "atmosphere_color" to "El aire es transparente, no azul",
                    "sun_emits" to "El sol emite todo el espectro, no solo azul"
                )
            ),
            hints = defaultHints("Óptica atmosférica"),
            timeLimitSeconds = 60,
            cognitiveLoadEstimate = CognitiveLoad.LOW,
            prerequisites = emptyList(),
            learningObjective = "Explicar fenómeno de dispersión atmosférica",
            bloomsLevel = BloomsTaxonomyLevel.UNDERSTAND,
            estimatedDiscrimination = 0.6,
            estimatedDifficulty = 0.4
        )
    ) + generateMoreScienceQuestions(99)
    
    // Preguntas de historia
    val historyQuestions = listOf(
        AdaptiveAssessmentQuestion(
            id = "hist_001",
            conceptId = "history",
            questionType = QuestionType.MULTIPLE_CHOICE,
            difficulty = ContentDifficulty.BEGINNER,
            content = QuestionContent(
                stem = "¿En qué año Cristóbal Colón llegó a América?",
                options = listOf(
                    QuestionOption("a", "1492", true, null, null),
                    QuestionOption("b", "1500", false, DistractorType.PARTIALLY_CORRECT, "close_year"),
                    QuestionOption("c", "1776", false, DistractorType.IRRELEVANT, "independence_confusion"),
                    QuestionOption("d", "1215", false, DistractorType.IRRELEVANT, "magna_carta_confusion")
                ),
                correctAnswers = listOf("a"),
                explanation = "12 de octubre de 1492: Colón llega a Guanahani (San Salvador). Inicia el contacto sostenido entre Europa y América.",
                misconceptionExplanations = mapOf(
                    "close_year" to "1500 es cercano pero incorrecto",
                    "independence_confusion" to "1776 es independencia de USA",
                    "magna_carta_confusion" to "1215 es Carta Magna"
                )
            ),
            hints = defaultHints("Descubrimiento de América"),
            timeLimitSeconds = 45,
            cognitiveLoadEstimate = CognitiveLoad.LOW,
            prerequisites = emptyList(),
            learningObjective = "Recordar fecha histórica significativa",
            bloomsLevel = BloomsTaxonomyLevel.REMEMBER,
            estimatedDiscrimination = 0.5,
            estimatedDifficulty = 0.3
        )
    ) + generateMoreHistoryQuestions(99)
    
    // Preguntas de finanzas
    val financeQuestions = listOf(
        AdaptiveAssessmentQuestion(
            id = "fin_001",
            conceptId = "finance",
            questionType = QuestionType.MULTIPLE_CHOICE,
            difficulty = ContentDifficulty.BEGINNER,
            content = QuestionContent(
                stem = "¿Qué es el 'interés compuesto'?",
                options = listOf(
                    QuestionOption("a", "Intereses que ganan intereses sobre intereses", true, null, null),
                    QuestionOption("b", "Una tarifa bancaria mensual", false, DistractorType.COMMON_MISCONCEPTION, "fee_confusion"),
                    QuestionOption("c", "La suma de depósitos realizados", false, DistractorType.PARTIALLY_CORRECT, "principal_only"),
                    QuestionOption("d", "El tipo de cambio entre monedas", false, DistractorType.IRRELEVANT, "exchange_rate")
                ),
                correctAnswers = listOf("a"),
                explanation = "Interés compuesto: intereses calculados sobre principal + intereses acumulados. Crecimiento exponencial: 'La octava maravilla del mundo' - Einstein.",
                misconceptionExplanations = mapOf(
                    "fee_confusion" to "Las tarifas son costos, no ganancia",
                    "principal_only" to "Interés simple usa solo principal, compuesto usa principal + intereses previos"
                )
            ),
            hints = defaultHints("Interés compuesto"),
            timeLimitSeconds = 60,
            cognitiveLoadEstimate = CognitiveLoad.LOW,
            prerequisites = emptyList(),
            learningObjective = "Definir interés compuesto",
            bloomsLevel = BloomsTaxonomyLevel.REMEMBER,
            estimatedDiscrimination = 0.5,
            estimatedDifficulty = 0.35
        )
    ) + generateMoreFinanceQuestions(99)
    
    // Preguntas generales
    val generalQuestions = listOf(
        AdaptiveAssessmentQuestion(
            id = "gen_001",
            conceptId = "general",
            questionType = QuestionType.MULTIPLE_CHOICE,
            difficulty = ContentDifficulty.ELEMENTARY,
            content = QuestionContent(
                stem = "¿Cuál es la capital de Francia?",
                options = listOf(
                    QuestionOption("a", "Londres", false, DistractorType.PLAUSIBLE_ALTERNATIVE, "uk_confusion"),
                    QuestionOption("b", "París", true, null, null),
                    QuestionOption("c", "Berlín", false, DistractorType.PLAUSIBLE_ALTERNATIVE, "germany_confusion"),
                    QuestionOption("d", "Madrid", false, DistractorType.PLAUSIBLE_ALTERNATIVE, "spain_confusion")
                ),
                correctAnswers = listOf("b"),
                explanation = "París es capital de Francia desde el siglo V. Centro político, económico y cultural.",
                misconceptionExplanations = mapOf(
                    "uk_confusion" to "Londres es capital de Reino Unido",
                    "germany_confusion" to "Berlín es capital de Alemania",
                    "spain_confusion" to "Madrid es capital de España"
                )
            ),
            hints = listOf(AdaptiveHint("h1", HintLevel.DIRECT, "Es conocida como la Ciudad de la Luz", null, listOf())),
            timeLimitSeconds = 30,
            cognitiveLoadEstimate = CognitiveLoad.LOW,
            prerequisites = emptyList(),
            learningObjective = "Conocimiento general geográfico",
            bloomsLevel = BloomsTaxonomyLevel.REMEMBER,
            estimatedDiscrimination = 0.3,
            estimatedDifficulty = 0.1
        )
    )
    
    // Mapa de preguntas por concepto
    private val generateQuestions = mapOf(
        "password_security" to passwordSecurityQuestions,
        "encryption" to encryptionQuestions,
        "algorithm" to algorithmQuestions,
        "network" to networkQuestions,
        "ai_ml" to aiQuestions,
        "cybersecurity" to cybersecurityQuestions,
        "data_privacy" to privacyQuestions,
        "emotional_intelligence" to emotionalIntelligenceQuestions,
        "critical_thinking" to criticalThinkingQuestions,
        "creativity" to creativityQuestions,
        "science" to scienceQuestions,
        "history" to historyQuestions,
        "finance" to financeQuestions,
        "general" to generalQuestions
    )
    
    // Funciones generadoras de más preguntas (placeholders para expansión)
    private fun generateMorePasswordQuestions(count: Int): List<AdaptiveAssessmentQuestion> = 
        List(count) { index ->
            AdaptiveAssessmentQuestion(
                id = "pwd_gen_${index + 11}",
                conceptId = "password_security",
                questionType = listOf(QuestionType.MULTIPLE_CHOICE, QuestionType.TRUE_FALSE, QuestionType.SHORT_ANSWER).random(),
                difficulty = ContentDifficulty.entries.toTypedArray().random(),
                content = QuestionContent(
                    stem = "Pregunta generada de seguridad de contraseñas #${index + 11}",
                    options = listOf(
                        QuestionOption("a", "Opción A", true, null, null),
                        QuestionOption("b", "Opción B", false, DistractorType.PLAUSIBLE_ALTERNATIVE, "distraction"),
                        QuestionOption("c", "Opción C", false, DistractorType.COMMON_MISCONCEPTION, "misconception"),
                        QuestionOption("d", "Opción D", false, DistractorType.IRRELEVANT, "irrelevant")
                    ),
                    correctAnswers = listOf("a"),
                    explanation = "Esta es una pregunta generada automáticamente para expansión.",
                    misconceptionExplanations = emptyMap()
                ),
                hints = defaultHints("Pregunta generada"),
                timeLimitSeconds = 60,
                cognitiveLoadEstimate = CognitiveLoad.MEDIUM,
                prerequisites = emptyList(),
                learningObjective = "Practicar conceptos de seguridad",
                bloomsLevel = BloomsTaxonomyLevel.entries.toTypedArray().random(),
                estimatedDiscrimination = 0.6,
                estimatedDifficulty = 0.5
            )
        }
    
    private fun generateMoreEncryptionQuestions(count: Int): List<AdaptiveAssessmentQuestion> = 
        List(count) { index ->
            AdaptiveAssessmentQuestion(
                id = "enc_gen_${index + 7}",
                conceptId = "encryption",
                questionType = QuestionType.MULTIPLE_CHOICE,
                difficulty = ContentDifficulty.entries.toTypedArray().random(),
                content = QuestionContent(
                    stem = "Pregunta generada de encriptación #${index + 7}",
                    options = listOf(
                        QuestionOption("a", "Respuesta correcta", true, null, null),
                        QuestionOption("b", "Distractor", false, DistractorType.PLAUSIBLE_ALTERNATIVE, null)
                    ),
                    correctAnswers = listOf("a"),
                    explanation = "Pregunta de expansión sobre criptografía.",
                    misconceptionExplanations = emptyMap()
                ),
                hints = defaultHints("Expansión"),
                timeLimitSeconds = 60,
                cognitiveLoadEstimate = CognitiveLoad.MEDIUM,
                prerequisites = emptyList(),
                learningObjective = "Practicar conceptos de encriptación",
                bloomsLevel = BloomsTaxonomyLevel.UNDERSTAND,
                estimatedDiscrimination = 0.6,
                estimatedDifficulty = 0.5
            )
        }
    
    private fun generateMoreAlgorithmQuestions(count: Int): List<AdaptiveAssessmentQuestion> = List(count) { index ->
        AdaptiveAssessmentQuestion(
            id = "alg_gen_${index + 6}",
            conceptId = "algorithm",
            questionType = QuestionType.entries.toTypedArray().random(),
            difficulty = ContentDifficulty.entries.toTypedArray().random(),
            content = QuestionContent(
                stem = "Pregunta de algoritmo #${index + 6}",
                options = listOf(QuestionOption("a", "Correcto", true, null, null)),
                correctAnswers = listOf("a"),
                explanation = "Pregunta de expansión.",
                misconceptionExplanations = emptyMap()
            ),
            hints = defaultHints("Algoritmo"),
            timeLimitSeconds = 60,
            cognitiveLoadEstimate = CognitiveLoad.MEDIUM,
            prerequisites = emptyList(),
            learningObjective = "Practicar algoritmos",
            bloomsLevel = BloomsTaxonomyLevel.UNDERSTAND,
            estimatedDiscrimination = 0.6,
            estimatedDifficulty = 0.5
        )
    }
    
    private fun generateMoreNetworkQuestions(count: Int): List<AdaptiveAssessmentQuestion> = List(count) { index ->
        AdaptiveAssessmentQuestion(
            id = "net_gen_${index + 6}",
            conceptId = "network",
            questionType = QuestionType.MULTIPLE_CHOICE,
            difficulty = ContentDifficulty.entries.toTypedArray().random(),
            content = QuestionContent(
                stem = "Pregunta de red #${index + 6}",
                options = listOf(QuestionOption("a", "Correcto", true, null, null)),
                correctAnswers = listOf("a"),
                explanation = "Pregunta de expansión.",
                misconceptionExplanations = emptyMap()
            ),
            hints = defaultHints("Redes"),
            timeLimitSeconds = 60,
            cognitiveLoadEstimate = CognitiveLoad.MEDIUM,
            prerequisites = emptyList(),
            learningObjective = "Practicar redes",
            bloomsLevel = BloomsTaxonomyLevel.UNDERSTAND,
            estimatedDiscrimination = 0.6,
            estimatedDifficulty = 0.5
        )
    }
    
    private fun generateMoreAIQuestions(count: Int): List<AdaptiveAssessmentQuestion> = List(count) { index ->
        AdaptiveAssessmentQuestion(
            id = "ai_gen_${index + 8}",
            conceptId = "ai_ml",
            questionType = QuestionType.MULTIPLE_CHOICE,
            difficulty = ContentDifficulty.entries.toTypedArray().random(),
            content = QuestionContent(
                stem = "Pregunta de IA #${index + 8}",
                options = listOf(QuestionOption("a", "Correcto", true, null, null)),
                correctAnswers = listOf("a"),
                explanation = "Pregunta de expansión.",
                misconceptionExplanations = emptyMap()
            ),
            hints = defaultHints("IA"),
            timeLimitSeconds = 60,
            cognitiveLoadEstimate = CognitiveLoad.MEDIUM,
            prerequisites = emptyList(),
            learningObjective = "Practicar IA",
            bloomsLevel = BloomsTaxonomyLevel.UNDERSTAND,
            estimatedDiscrimination = 0.6,
            estimatedDifficulty = 0.5
        )
    }
    
    private fun generateMoreCybersecurityQuestions(count: Int): List<AdaptiveAssessmentQuestion> = List(count) { index ->
        AdaptiveAssessmentQuestion(
            id = "cyber_gen_${index + 7}",
            conceptId = "cybersecurity",
            questionType = QuestionType.MULTIPLE_CHOICE,
            difficulty = ContentDifficulty.entries.toTypedArray().random(),
            content = QuestionContent(
                stem = "Pregunta de ciberseguridad #${index + 7}",
                options = listOf(QuestionOption("a", "Correcto", true, null, null)),
                correctAnswers = listOf("a"),
                explanation = "Pregunta de expansión.",
                misconceptionExplanations = emptyMap()
            ),
            hints = defaultHints("Ciberseguridad"),
            timeLimitSeconds = 60,
            cognitiveLoadEstimate = CognitiveLoad.MEDIUM,
            prerequisites = emptyList(),
            learningObjective = "Practicar ciberseguridad",
            bloomsLevel = BloomsTaxonomyLevel.UNDERSTAND,
            estimatedDiscrimination = 0.6,
            estimatedDifficulty = 0.5
        )
    }
    
    private fun generateMorePrivacyQuestions(count: Int): List<AdaptiveAssessmentQuestion> = List(count) { index ->
        AdaptiveAssessmentQuestion(
            id = "priv_gen_${index + 6}",
            conceptId = "data_privacy",
            questionType = QuestionType.MULTIPLE_CHOICE,
            difficulty = ContentDifficulty.entries.toTypedArray().random(),
            content = QuestionContent(
                stem = "Pregunta de privacidad #${index + 6}",
                options = listOf(QuestionOption("a", "Correcto", true, null, null)),
                correctAnswers = listOf("a"),
                explanation = "Pregunta de expansión.",
                misconceptionExplanations = emptyMap()
            ),
            hints = defaultHints("Privacidad"),
            timeLimitSeconds = 60,
            cognitiveLoadEstimate = CognitiveLoad.MEDIUM,
            prerequisites = emptyList(),
            learningObjective = "Practicar privacidad",
            bloomsLevel = BloomsTaxonomyLevel.UNDERSTAND,
            estimatedDiscrimination = 0.6,
            estimatedDifficulty = 0.5
        )
    }
    
    private fun generateMoreEmotionalIntelligenceQuestions(count: Int): List<AdaptiveAssessmentQuestion> = List(count) { index ->
        AdaptiveAssessmentQuestion(
            id = "ei_gen_${index + 2}",
            conceptId = "emotional_intelligence",
            questionType = QuestionType.MULTIPLE_CHOICE,
            difficulty = ContentDifficulty.entries.toTypedArray().random(),
            content = QuestionContent(
                stem = "Pregunta de inteligencia emocional #${index + 2}",
                options = listOf(QuestionOption("a", "Correcto", true, null, null)),
                correctAnswers = listOf("a"),
                explanation = "Pregunta de expansión.",
                misconceptionExplanations = emptyMap()
            ),
            hints = defaultHints("IE"),
            timeLimitSeconds = 60,
            cognitiveLoadEstimate = CognitiveLoad.MEDIUM,
            prerequisites = emptyList(),
            learningObjective = "Practicar IE",
            bloomsLevel = BloomsTaxonomyLevel.UNDERSTAND,
            estimatedDiscrimination = 0.6,
            estimatedDifficulty = 0.5
        )
    }
    
    private fun generateMoreCriticalThinkingQuestions(count: Int): List<AdaptiveAssessmentQuestion> = List(count) { index ->
        AdaptiveAssessmentQuestion(
            id = "ct_gen_${index + 2}",
            conceptId = "critical_thinking",
            questionType = QuestionType.MULTIPLE_CHOICE,
            difficulty = ContentDifficulty.entries.toTypedArray().random(),
            content = QuestionContent(
                stem = "Pregunta de pensamiento crítico #${index + 2}",
                options = listOf(QuestionOption("a", "Correcto", true, null, null)),
                correctAnswers = listOf("a"),
                explanation = "Pregunta de expansión.",
                misconceptionExplanations = emptyMap()
            ),
            hints = defaultHints("Pensamiento crítico"),
            timeLimitSeconds = 60,
            cognitiveLoadEstimate = CognitiveLoad.MEDIUM,
            prerequisites = emptyList(),
            learningObjective = "Practicar pensamiento crítico",
            bloomsLevel = BloomsTaxonomyLevel.UNDERSTAND,
            estimatedDiscrimination = 0.6,
            estimatedDifficulty = 0.5
        )
    }
    
    private fun generateMoreCreativityQuestions(count: Int): List<AdaptiveAssessmentQuestion> = List(count) { index ->
        AdaptiveAssessmentQuestion(
            id = "cre_gen_${index + 2}",
            conceptId = "creativity",
            questionType = QuestionType.MULTIPLE_CHOICE,
            difficulty = ContentDifficulty.entries.toTypedArray().random(),
            content = QuestionContent(
                stem = "Pregunta de creatividad #${index + 2}",
                options = listOf(QuestionOption("a", "Correcto", true, null, null)),
                correctAnswers = listOf("a"),
                explanation = "Pregunta de expansión.",
                misconceptionExplanations = emptyMap()
            ),
            hints = defaultHints("Creatividad"),
            timeLimitSeconds = 60,
            cognitiveLoadEstimate = CognitiveLoad.MEDIUM,
            prerequisites = emptyList(),
            learningObjective = "Practicar creatividad",
            bloomsLevel = BloomsTaxonomyLevel.UNDERSTAND,
            estimatedDiscrimination = 0.6,
            estimatedDifficulty = 0.5
        )
    }
    
    private fun generateMoreScienceQuestions(count: Int): List<AdaptiveAssessmentQuestion> = List(count) { index ->
        AdaptiveAssessmentQuestion(
            id = "sci_gen_${index + 2}",
            conceptId = "science",
            questionType = QuestionType.MULTIPLE_CHOICE,
            difficulty = ContentDifficulty.entries.toTypedArray().random(),
            content = QuestionContent(
                stem = "Pregunta de ciencia #${index + 2}",
                options = listOf(QuestionOption("a", "Correcto", true, null, null)),
                correctAnswers = listOf("a"),
                explanation = "Pregunta de expansión.",
                misconceptionExplanations = emptyMap()
            ),
            hints = defaultHints("Ciencia"),
            timeLimitSeconds = 60,
            cognitiveLoadEstimate = CognitiveLoad.MEDIUM,
            prerequisites = emptyList(),
            learningObjective = "Practicar ciencia",
            bloomsLevel = BloomsTaxonomyLevel.UNDERSTAND,
            estimatedDiscrimination = 0.6,
            estimatedDifficulty = 0.5
        )
    }
    
    private fun generateMoreHistoryQuestions(count: Int): List<AdaptiveAssessmentQuestion> = List(count) { index ->
        AdaptiveAssessmentQuestion(
            id = "hist_gen_${index + 2}",
            conceptId = "history",
            questionType = QuestionType.MULTIPLE_CHOICE,
            difficulty = ContentDifficulty.entries.toTypedArray().random(),
            content = QuestionContent(
                stem = "Pregunta de historia #${index + 2}",
                options = listOf(QuestionOption("a", "Correcto", true, null, null)),
                correctAnswers = listOf("a"),
                explanation = "Pregunta de expansión.",
                misconceptionExplanations = emptyMap()
            ),
            hints = defaultHints("Historia"),
            timeLimitSeconds = 60,
            cognitiveLoadEstimate = CognitiveLoad.MEDIUM,
            prerequisites = emptyList(),
            learningObjective = "Practicar historia",
            bloomsLevel = BloomsTaxonomyLevel.UNDERSTAND,
            estimatedDiscrimination = 0.6,
            estimatedDifficulty = 0.5
        )
    }
    
    private fun generateMoreFinanceQuestions(count: Int): List<AdaptiveAssessmentQuestion> = List(count) { index ->
        AdaptiveAssessmentQuestion(
            id = "fin_gen_${index + 2}",
            conceptId = "finance",
            questionType = QuestionType.MULTIPLE_CHOICE,
            difficulty = ContentDifficulty.entries.toTypedArray().random(),
            content = QuestionContent(
                stem = "Pregunta de finanzas #${index + 2}",
                options = listOf(QuestionOption("a", "Correcto", true, null, null)),
                correctAnswers = listOf("a"),
                explanation = "Pregunta de expansión.",
                misconceptionExplanations = emptyMap()
            ),
            hints = defaultHints("Finanzas"),
            timeLimitSeconds = 60,
            cognitiveLoadEstimate = CognitiveLoad.MEDIUM,
            prerequisites = emptyList(),
            learningObjective = "Practicar finanzas",
            bloomsLevel = BloomsTaxonomyLevel.UNDERSTAND,
            estimatedDiscrimination = 0.6,
            estimatedDifficulty = 0.5
        )
    }
    
    // Utilidades
    private fun filterByDifficulty(questions: List<AdaptiveAssessmentQuestion>, difficulty: ContentDifficulty): List<AdaptiveAssessmentQuestion> {
        return questions.filter { it.difficulty == difficulty }
    }
    
    private fun defaultHints(topic: String): List<AdaptiveHint> = listOf(
        AdaptiveHint("d1", HintLevel.SUBTLE, "Piensa cuidadosamente sobre el concepto de $topic", null, listOf()),
        AdaptiveHint("d2", HintLevel.GUIDING, "Revisa las opciones y elimina las claramente incorrectas", null, listOf()),
        AdaptiveHint("d3", HintLevel.DIRECT, "Lee la explicación después de responder para reforzar el aprendizaje", null, listOf())
    )
}
