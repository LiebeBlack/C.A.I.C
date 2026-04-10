package com.LBs.EEDA.domain.model.educational

import kotlinx.serialization.Serializable

/**
 * Sistema de adaptación de contenido por edad.
 * Proporciona vocabulario, analogías y complejidad apropiados para cada rango etario.
 */
@Serializable
data class AgeAdaptiveContent(
    val baseConcept: String,
    val ageBracket: AgeBracket,
    val displayTitle: String,
    val analogy: String,
    val technicalTerm: String? = null,
    val explanation: String,
    val interactiveElements: List<InteractiveElement>,
    val difficulty: ContentDifficulty
)

enum class AgeBracket(val range: IntRange, val cognitiveLevel: CognitiveLevel) {
    EARLY_CHILDHOOD(3..5, CognitiveLevel.PREOPERATIONAL),
    CHILDHOOD(6..8, CognitiveLevel.CONCRETE_EARLY),
    PRE_ADOLESCENT(9..11, CognitiveLevel.CONCRETE_MATURE),
    ADOLESCENT_EARLY(12..14, CognitiveLevel.FORMAL_EARLY),
    ADOLESCENT_LATE(15..17, CognitiveLevel.FORMAL_MATURE),
    YOUNG_ADULT(18..20, CognitiveLevel.ABSTRACT)
}

enum class CognitiveLevel {
    PREOPERATIONAL,      // Pensamiento simbólico, egocéntrico
    CONCRETE_EARLY,      // Lógica concreta, clasificación simple
    CONCRETE_MATURE,     // Lógica concreta, seriación y conservación
    FORMAL_EARLY,      // Pensamiento hipotético-deductivo inicial
    FORMAL_MATURE,     // Abstracción, metacognición
    ABSTRACT           // Pensamiento abstracto complejo
}

enum class ContentDifficulty(val value: Int) {
    BEGINNER(1),
    ELEMENTARY(2),
    INTERMEDIATE(3),
    ADVANCED(4),
    EXPERT(5)
}

@Serializable
data class InteractiveElement(
    val type: ElementType,
    val description: String,
    val expectedOutcome: String,
    val hintOnFailure: String
)

enum class ElementType {
    DRAG_DROP,
    SEQUENCE_ORDER,
    MULTIPLE_CHOICE,
    CODE_COMPLETION,
    SIMULATION,
    PUZZLE,
    MATCHING,
    MEMORY_GAME
}

/**
 * Motor de adaptación de contenido que transforma conceptos técnicos
 * según la edad del usuario.
 */
object ContentAdaptationEngine {
    
    fun adaptContent(baseConcept: String, age: Int): AgeAdaptiveContent {
        val bracket = AgeBracket.entries.find { age in it.range } ?: AgeBracket.YOUNG_ADULT
        
        return when (baseConcept) {
            "password_security" -> adaptPasswordSecurity(bracket)
            "encryption" -> adaptEncryption(bracket)
            "algorithm" -> adaptAlgorithm(bracket)
            "network" -> adaptNetwork(bracket)
            "ai_ml" -> adaptAiMl(bracket)
            "cybersecurity" -> adaptCybersecurity(bracket)
            "data_privacy" -> adaptDataPrivacy(bracket)
            "programming_logic" -> adaptProgrammingLogic(bracket)
            else -> createGenericAdaptation(baseConcept, bracket)
        }
    }
    
    private fun adaptPasswordSecurity(bracket: AgeBracket): AgeAdaptiveContent {
        return when (bracket) {
            AgeBracket.EARLY_CHILDHOOD -> AgeAdaptiveContent(
                baseConcept = "password_security",
                ageBracket = bracket,
                displayTitle = "Tu Palabra Mágica Secreta",
                analogy = "Como la llave de tu casa, solo tú debes conocerla",
                technicalTerm = null,
                explanation = "Crea una palabra especial que solo tú sepas. No la compartas con extraños, ¡así tu tesoro estará seguro!",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.DRAG_DROP,
                        description = "Arrastra objetos para crear tu 'palabra mágica'",
                        expectedOutcome = "Combinar 3 elementos para formar una contraseña visual",
                        hintOnFailure = "Prueba con tu animal favorito + tu número favorito + un color"
                    )
                ),
                difficulty = ContentDifficulty.BEGINNER
            )
            
            AgeBracket.CHILDHOOD -> AgeAdaptiveContent(
                baseConcept = "password_security",
                ageBracket = bracket,
                displayTitle = "El Guardián de Secretos",
                analogy = "Como un código secreto entre amigos",
                technicalTerm = "Contraseña",
                explanation = "Una contraseña es como un código secreto. Debe ser difícil de adivinar pero fácil de recordar para ti. Mezcla letras, números y símbolos.",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.SEQUENCE_ORDER,
                        description = "Ordena los elementos de una contraseña fuerte",
                        expectedOutcome = "Secuencia: Letra mayúscula + minúsculas + número + símbolo",
                        hintOnFailure = "Recuerda: Mayúscula, minúscula, número, símbolo. ¡Como Gato123*!"
                    )
                ),
                difficulty = ContentDifficulty.ELEMENTARY
            )
            
            AgeBracket.PRE_ADOLESCENT -> AgeAdaptiveContent(
                baseConcept = "password_security",
                ageBracket = bracket,
                displayTitle = "Fortaleza Digital",
                analogy = "Como una combinación de seguridad con múltiples candados",
                technicalTerm = "Autenticación Multi-factor",
                explanation = "Las contraseñas fuertes combinan diferentes tipos de caracteres. Piensa en una frase que solo tú conozcas y conviértela en código usando las primeras letras.",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.CODE_COMPLETION,
                        description = "Completa la función para generar una contraseña segura",
                        expectedOutcome = "Password con entropía > 50 bits",
                        hintOnFailure = "Usa una frase personal: 'Mi perro Luna nació en 2020' → MpLnée2020!"
                    )
                ),
                difficulty = ContentDifficulty.INTERMEDIATE
            )
            
            AgeBracket.ADOLESCENT_EARLY -> AgeAdaptiveContent(
                baseConcept = "password_security",
                ageBracket = bracket,
                displayTitle = "Hashing y Entropía",
                analogy = "Como una función matemática de un solo sentido",
                technicalTerm = "Hash Criptográfico",
                explanation = "Los sistemas no guardan tu contraseña en texto plano. Usan funciones hash como SHA-256 que transforman tu contraseña en un valor único irreversible.",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.SIMULATION,
                        description = "Visualiza cómo funciona un hash SHA-256",
                        expectedOutcome = "Entender que pequeños cambios generan hashes completamente diferentes",
                        hintOnFailure = "Observa cómo cambiar una sola letra transforma completamente el hash"
                    )
                ),
                difficulty = ContentDifficulty.ADVANCED
            )
            
            AgeBracket.ADOLESCENT_LATE, AgeBracket.YOUNG_ADULT -> AgeAdaptiveContent(
                baseConcept = "password_security",
                ageBracket = bracket,
                displayTitle = "Políticas de Seguridad y Gestores de Contraseñas",
                analogy = "Infraestructura de clave pública y privada",
                technicalTerm = "Cifrado Asimétrico",
                explanation = "Implementa arquitecturas de seguridad robustas usando gestores de contraseñas con cifrado AES-256, autenticación multi-factor basada en TOTP, y políticas de rotación de credenciales.",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.CODE_COMPLETION,
                        description = "Implementa una función de hash con sal (salt)",
                        expectedOutcome = "PBKDF2 con salt aleatorio de 16 bytes y 100k iteraciones",
                        hintOnFailure = "Recuerda: el salt debe ser único por usuario y almacenarse junto al hash"
                    ),
                    InteractiveElement(
                        type = ElementType.SIMULATION,
                        description = "Configura políticas de MFA en un sistema simulado",
                        expectedOutcome = "TOTP + backup codes + hardware key simulation",
                        hintOnFailure = "Considera vectores de ataque: phishing, SIM swapping, keyloggers"
                    )
                ),
                difficulty = ContentDifficulty.EXPERT
            )
        }
    }
    
    private fun adaptEncryption(bracket: AgeBracket): AgeAdaptiveContent {
        return when (bracket) {
            AgeBracket.EARLY_CHILDHOOD -> AgeAdaptiveContent(
                baseConcept = "encryption",
                ageBracket = bracket,
                displayTitle = "El Código Secreto",
                analogy = "Como escribir con tinta invisible que solo tú puedes ver",
                technicalTerm = null,
                explanation = "Transformamos mensajes para que solo la persona correcta los pueda leer. ¡Es magia de protección!",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.MATCHING,
                        description = "Empareja mensajes con sus versiones 'secretas'",
                        expectedOutcome = "3 pares correctos de mensaje original ↔ cifrado",
                        hintOnFailure = "Busca patrones: las vocales se convierten en números"
                    )
                ),
                difficulty = ContentDifficulty.BEGINNER
            )
            
            AgeBracket.CHILDHOOD -> AgeAdaptiveContent(
                baseConcept = "encryption",
                ageBracket = bracket,
                displayTitle = "El Cifrado del César",
                analogy = "Como el alfabeto que se desplaza",
                technicalTerm = "Cifrado por Sustitución",
                explanation = "El cifrado mueve cada letra un número fijo de posiciones. Si desplazamos 3, la A se convierte en D.",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.PUZZLE,
                        description = "Descifra el mensaje usando el desplazamiento correcto",
                        expectedOutcome = "Mensaje legible descifrado con clave 3",
                        hintOnFailure = "Prueba con palabras cortas primero. ¿'Hola' con desplazamiento 3 es 'Krod'?"
                    )
                ),
                difficulty = ContentDifficulty.ELEMENTARY
            )
            
            AgeBracket.PRE_ADOLESCENT -> AgeAdaptiveContent(
                baseConcept = "encryption",
                ageBracket = bracket,
                displayTitle = "Criptografía Simétrica",
                analogy = "Como una caja fuerte donde emisor y receptor tienen la misma llave",
                technicalTerm = "Clave Simétrica",
                explanation = "En criptografía simétrica, la misma clave encripta y desencripta. El desafío es compartir la clave de forma segura.",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.CODE_COMPLETION,
                        description = "Implementa XOR básico para cifrar un mensaje",
                        expectedOutcome = "Función XOR que cifra y descifra con la misma clave",
                        hintOnFailure = "Recuerda: XOR es su propia inversa. (A XOR B) XOR B = A"
                    )
                ),
                difficulty = ContentDifficulty.INTERMEDIATE
            )
            
            AgeBracket.ADOLESCENT_EARLY -> AgeAdaptiveContent(
                baseConcept = "encryption",
                ageBracket = bracket,
                displayTitle = "Infraestructura de Clave Pública",
                analogy = "Como un buzón donde cualquiera puede depositar, pero solo el dueño puede abrir",
                technicalTerm = "RSA / Criptografía Asimétrica",
                explanation = "La criptografía asimétrica usa dos claves: una pública para cifrar (cualquiera puede usarla) y una privada para descifrar (solo el dueño la tiene).",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.SIMULATION,
                        description = "Simula el intercambio de claves Diffie-Hellman",
                        expectedOutcome = "Ambas partes derivan la misma clave secreta sin transmitirla",
                        hintOnFailure = "La magia está en las matemáticas modulares: (g^a)^b mod p = (g^b)^a mod p"
                    )
                ),
                difficulty = ContentDifficulty.ADVANCED
            )
            
            AgeBracket.ADOLESCENT_LATE, AgeBracket.YOUNG_ADULT -> AgeAdaptiveContent(
                baseConcept = "encryption",
                ageBracket = bracket,
                displayTitle = "Algoritmos Modernos y Post-Cuánticos",
                analogy = "Curvas elípticas y resistencia a computación cuántica",
                technicalTerm = "ECC / AES-256 / Kyber",
                explanation = "Explora AES-256 en modo GCM para autenticación integrada, curvas elípticas para eficiencia, y algoritmos post-cuánticos como Kyber para resistencia futura.",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.CODE_COMPLETION,
                        description = "Implementa AES-GCM con autenticación",
                        expectedOutcome = "Encriptación con nonce, tag de autenticación HMAC",
                        hintOnFailure = "El nonce debe ser único pero no necesita ser secreto. El tag verifica integridad."
                    ),
                    InteractiveElement(
                        type = ElementType.SIMULATION,
                        description = "Compara rendimiento RSA vs ECC vs Kyber",
                        expectedOutcome = "ECC más rápido en claves pequeñas, Kyber resistente a Shor's algorithm",
                        hintOnFailure = "Considera: tamaño de clave, velocidad, y resistencia cuántica"
                    )
                ),
                difficulty = ContentDifficulty.EXPERT
            )
        }
    }
    
    private fun adaptAlgorithm(bracket: AgeBracket): AgeAdaptiveContent {
        return when (bracket) {
            AgeBracket.EARLY_CHILDHOOD -> AgeAdaptiveContent(
                baseConcept = "algorithm",
                ageBracket = bracket,
                displayTitle = "Instrucciones para un Robot",
                analogy = "Como dar pasos para armar un bloque de LEGO",
                technicalTerm = null,
                explanation = "Un algoritmo es una lista de pasos ordenados para hacer algo. Primero esto, luego esto, después aquello.",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.SEQUENCE_ORDER,
                        description = "Ordena los pasos para cepillarse los dientes",
                        expectedOutcome = "Secuencia lógica: tomar cepillo → poner pasta → cepillar → enjuagar",
                        hintOnFailure = "Piensa: ¿qué pasaría si enjuagaras antes de cepillar?"
                    )
                ),
                difficulty = ContentDifficulty.BEGINNER
            )
            
            AgeBracket.CHILDHOOD -> AgeAdaptiveContent(
                baseConcept = "algorithm",
                ageBracket = bracket,
                displayTitle = "El Laberinto del Robot",
                analogy = "Como un conjunto de reglas para salir de un laberinto",
                technicalTerm = "Algoritmo",
                explanation = "Los algoritmos siguen reglas: 'si pared a la derecha, gira izquierda'. Son instrucciones precisas que una computadora puede seguir.",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.DRAG_DROP,
                        description = "Arrastra bloques de código para guiar al robot",
                        expectedOutcome = "Secuencia de comandos: avanzar, girar, detectar obstáculos",
                        hintOnFailure = "Usa 'repetir' para no escribir 'avanzar' 10 veces"
                    )
                ),
                difficulty = ContentDifficulty.ELEMENTARY
            )
            
            AgeBracket.PRE_ADOLESCENT -> AgeAdaptiveContent(
                baseConcept = "algorithm",
                ageBracket = bracket,
                displayTitle = "Estructuras de Control",
                analogy = "Como flujos de decisión en un diagrama",
                technicalTerm = "Condicionales y Bucles",
                explanation = "Los algoritmos usan 'if' para decisiones y 'for/while' para repeticiones. Son patrones de solución aplicables a múltiples problemas.",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.CODE_COMPLETION,
                        description = "Completa un algoritmo de búsqueda binaria",
                        expectedOutcome = "Función que divide el espacio de búsqueda a la mitad cada iteración",
                        hintOnFailure = "Recuerda: ordenar primero, luego comparar con el medio, descartar mitad"
                    )
                ),
                difficulty = ContentDifficulty.INTERMEDIATE
            )
            
            AgeBracket.ADOLESCENT_EARLY -> AgeAdaptiveContent(
                baseConcept = "algorithm",
                ageBracket = bracket,
                displayTitle = "Complejidad Algorítmica",
                analogy = "Como medir qué tan rápido crece el esfuerzo vs los datos",
                technicalTerm = "Notación Big-O",
                explanation = "Analizamos eficiencia: O(1) constante, O(n) lineal, O(n²) cuadrático, O(log n) logarítmico. La elección del algoritmo impacta en escalabilidad.",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.SIMULATION,
                        description = "Compara O(n²) vs O(n log n) con diferentes tamaños de entrada",
                        expectedOutcome = "Visualizar cómo O(n²) se vuelve impráctico con n > 10000",
                        hintOnFailure = "Observa: para n=1M, n² = 1 billón operaciones, n log n ≈ 20M"
                    )
                ),
                difficulty = ContentDifficulty.ADVANCED
            )
            
            AgeBracket.ADOLESCENT_LATE, AgeBracket.YOUNG_ADULT -> AgeAdaptiveContent(
                baseConcept = "algorithm",
                ageBracket = bracket,
                displayTitle = "Algoritmos Avanzados y Optimización",
                analogy = "Diseño de sistemas algorítmicos para escala masiva",
                technicalTerm = "Algoritmos de Grafos, Programación Dinámica",
                explanation = "Implementa Dijkstra para caminos mínimos, árboles de decisión para optimización, y programación dinámica para subestructuras óptimas.",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.CODE_COMPLETION,
                        description = "Optimiza el problema de la mochila con PD",
                        expectedOutcome = "Solución O(nW) usando memoización bottom-up",
                        hintOnFailure = "Definición recursiva: dp[i][w] = max(dp[i-1][w], dp[i-1][w-wi] + vi)"
                    ),
                    InteractiveElement(
                        type = ElementType.SIMULATION,
                        description = "Visualiza algoritmos de grafos en redes reales",
                        expectedOutcome = "Dijkstra, A*, y detección de ciclos",
                        hintOnFailure = "A* usa heurística: f(n) = g(n) + h(n), g=costo real, h=estimación"
                    )
                ),
                difficulty = ContentDifficulty.EXPERT
            )
        }
    }
    
    private fun adaptNetwork(bracket: AgeBracket): AgeAdaptiveContent {
        return when (bracket) {
            AgeBracket.EARLY_CHILDHOOD -> AgeAdaptiveContent(
                baseConcept = "network",
                ageBracket = bracket,
                displayTitle = "Los Caminos de los Mensajes",
                analogy = "Como cartas que viajan por caminos hasta llegar a tus amigos",
                technicalTerm = null,
                explanation = "Los mensajes viajan por caminos invisibles para llegar a otras computadoras. Cada una tiene una dirección especial.",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.MATCHING,
                        description = "Conecta mensajes con sus destinos correctos",
                        expectedOutcome = "3 conexiones correctas: mensaje ↔ computadora destino",
                        hintOnFailure = "Mira las direcciones: cada casa tiene un número especial"
                    )
                ),
                difficulty = ContentDifficulty.BEGINNER
            )
            
            AgeBracket.CHILDHOOD -> AgeAdaptiveContent(
                baseConcept = "network",
                ageBracket = bracket,
                displayTitle = "Internet como una Telaraña",
                analogy = "Como una red de caminos que conectan ciudades",
                technicalTerm = "Red de Computadoras",
                explanation = "Internet es una red de redes. Las computadoras hablan usando direcciones IP, como 'calle número casa'. Los routers guían los mensajes.",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.PUZZLE,
                        description = "Dibuja la ruta más corta entre dos puntos en la red",
                        expectedOutcome = "Ruta óptima evitando nodos congestionados",
                        hintOnFailure = "Los routers eligen caminos con menos 'tráfico'"
                    )
                ),
                difficulty = ContentDifficulty.ELEMENTARY
            )
            
            AgeBracket.PRE_ADOLESCENT -> AgeAdaptiveContent(
                baseConcept = "network",
                ageBracket = bracket,
                displayTitle = "Protocolos y Capas",
                analogy = "Como capas de una carta: sobre, sello, dirección",
                technicalTerm = "TCP/IP, OSI Model",
                explanation = "Las redes funcionan en capas: física (cables), red (IP), transporte (TCP), aplicación (HTTP). Cada capa tiene su responsabilidad.",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.MATCHING,
                        description = "Empareja protocolos con sus funciones",
                        expectedOutcome = "TCP ↔ confiabilidad, IP ↔ direccionamiento, HTTP ↔ web",
                        hintOnFailure = "TCP garantiza entrega, IP encuentra la ruta, HTTP es para navegadores"
                    )
                ),
                difficulty = ContentDifficulty.INTERMEDIATE
            )
            
            AgeBracket.ADOLESCENT_EARLY -> AgeAdaptiveContent(
                baseConcept = "network",
                ageBracket = bracket,
                displayTitle = "Arquitectura de Redes",
                analogy = "Como diseño de infraestructura urbana",
                technicalTerm = "DNS, DHCP, NAT, Subnetting",
                explanation = "Explora cómo DNS traduce nombres a IPs, cómo DHCP asigna direcciones dinámicas, y cómo NAT permite compartir IPs públicas.",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.SIMULATION,
                        description = "Simula una consulta DNS completa recursiva",
                        expectedOutcome = "Traza: Root → TLD → Authoritative → Respuesta cacheada",
                        hintOnFailure = "Los resolvers recursivos cachean para reducir latencia"
                    )
                ),
                difficulty = ContentDifficulty.ADVANCED
            )
            
            AgeBracket.ADOLESCENT_LATE, AgeBracket.YOUNG_ADULT -> AgeAdaptiveContent(
                baseConcept = "network",
                ageBracket = bracket,
                displayTitle = "Redes Avanzadas y Seguridad",
                analogy = "Arquitectura de sistemas distribuidos y SDN",
                technicalTerm = "BGP, SDN, Zero Trust, CDN",
                explanation = "Analiza BGP para routing inter-AS, Software Defined Networking para control centralizado, arquitecturas Zero Trust, y optimización con CDNs.",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.SIMULATION,
                        description = "Configura políticas BGP en un AS simulado",
                        expectedOutcome = "Rutas preferidas por atributos: local_pref, AS_path, MED",
                        hintOnFailure = "Local_PREF más alto = preferido. AS_PATH más corto = preferido."
                    )
                ),
                difficulty = ContentDifficulty.EXPERT
            )
        }
    }
    
    private fun adaptAiMl(bracket: AgeBracket): AgeAdaptiveContent {
        return when (bracket) {
            AgeBracket.EARLY_CHILDHOOD -> AgeAdaptiveContent(
                baseConcept = "ai_ml",
                ageBracket = bracket,
                displayTitle = "La Computadora que Aprende",
                analogy = "Como enseñar a un amigo a reconocer animales mostrándole fotos",
                technicalTerm = null,
                explanation = "Podemos enseñar a las computadoras mostrándoles muchos ejemplos. Ven muchas fotos de gatos y aprenden a reconocer gatos.",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.MATCHING,
                        description = "Clasifica imágenes en 'Gato' o 'Perro' para entrenar",
                        expectedOutcome = "10 clasificaciones correctas",
                        hintOnFailure = "Mira las orejas: puntiagudas = gato, caídas = perro"
                    )
                ),
                difficulty = ContentDifficulty.BEGINNER
            )
            
            AgeBracket.CHILDHOOD -> AgeAdaptiveContent(
                baseConcept = "ai_ml",
                ageBracket = bracket,
                displayTitle = "El Detective de Patrones",
                analogy = "Como encontrar patrones en un rompecabezas",
                technicalTerm = "Machine Learning",
                explanation = "El Machine Learning encuentra patrones en datos. Si mostramos miles de números escritos a mano, aprende a leerlos.",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.DRAG_DROP,
                        description = "Entrena un clasificador arrastrando ejemplos",
                        expectedOutcome = "Línea de separación entre dos clases de datos",
                        hintOnFailure = "Los puntos más cercanos a la línea son los más 'difíciles'"
                    )
                ),
                difficulty = ContentDifficulty.ELEMENTARY
            )
            
            AgeBracket.PRE_ADOLESCENT -> AgeAdaptiveContent(
                baseConcept = "ai_ml",
                ageBracket = bracket,
                displayTitle = "Redes Neuronales Simples",
                analogy = "Como el cerebro con neuronas conectadas",
                technicalTerm = "Perceptrón",
                explanation = "Las redes neuronales tienen capas: entrada (datos), oculta (procesamiento), salida (resultado). Cada conexión tiene un peso que se ajusta.",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.SIMULATION,
                        description = "Visualiza cómo los pesos afectan la salida",
                        expectedOutcome = "Entender que pesos altos = más importancia",
                        hintOnFailure = "Observa: si un peso es 0, esa entrada no importa"
                    )
                ),
                difficulty = ContentDifficulty.INTERMEDIATE
            )
            
            AgeBracket.ADOLESCENT_EARLY -> AgeAdaptiveContent(
                baseConcept = "ai_ml",
                ageBracket = bracket,
                displayTitle = "Descenso del Gradiente",
                analogy = "Como encontrar el valle más profundo caminando cuesta abajo",
                technicalTerm = "Backpropagation, Gradient Descent",
                explanation = "Las redes aprenden ajustando pesos para minimizar error. Backpropagation calcula qué tan responsable es cada peso del error total.",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.SIMULATION,
                        description = "Visualiza el descenso del gradiente en 2D",
                        expectedOutcome = "Entender learning rate, mínimos locales, convergencia",
                        hintOnFailure = "Learning rate muy alto = oscila. Muy bajo = lento."
                    )
                ),
                difficulty = ContentDifficulty.ADVANCED
            )
            
            AgeBracket.ADOLESCENT_LATE, AgeBracket.YOUNG_ADULT -> AgeAdaptiveContent(
                baseConcept = "ai_ml",
                ageBracket = bracket,
                displayTitle = "Arquitecturas Profundas y Transformers",
                analogy = "Transformers, atención, y modelos de lenguaje grandes",
                technicalTerm = "Transformer, Attention, LLM",
                explanation = "Explora arquitecturas Transformer con mecanismos de atención self-attention, embeddings contextuales, y técnicas de fine-tuning con LoRA.",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.CODE_COMPLETION,
                        description = "Implementa atención multi-cabeza simplificada",
                        expectedOutcome = "Q, K, V matrices y softmax(QK^T/√d)V",
                        hintOnFailure = "Scaled dot-product: √d_k evita gradientes pequeños"
                    ),
                    InteractiveElement(
                        type = ElementType.SIMULATION,
                        description = "Visualiza mapas de atención en traducción",
                        expectedOutcome = "Ver qué palabras de entrada atiende cada palabra de salida",
                        hintOnFailure = "Las conexiones más oscuras = mayor atención"
                    )
                ),
                difficulty = ContentDifficulty.EXPERT
            )
        }
    }
    
    private fun adaptCybersecurity(bracket: AgeBracket): AgeAdaptiveContent {
        return when (bracket) {
            AgeBracket.EARLY_CHILDHOOD -> AgeAdaptiveContent(
                baseConcept = "cybersecurity",
                ageBracket = bracket,
                displayTitle = "El Escudo Mágico",
                analogy = "Como un escudo que protege tu castillo",
                technicalTerm = null,
                explanation = "La seguridad protege tu computadora como un escudo. No abras puertas a extraños y no compartas tus secretos.",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.MEMORY_GAME,
                        description = "Empareja amenazas con sus protecciones",
                        expectedOutcome = "Virus ↔ Antivirus, Ladrón ↔ Contraseña, Engaño ↔ Desconfianza",
                        hintOnFailure = "Piensa: ¿qué protege contra qué?"
                    )
                ),
                difficulty = ContentDifficulty.BEGINNER
            )
            
            AgeBracket.CHILDHOOD -> AgeAdaptiveContent(
                baseConcept = "cybersecurity",
                ageBracket = bracket,
                displayTitle = "El Guardián de la Red",
                analogy = "Como un guardia que revisa quién entra al edificio",
                technicalTerm = "Firewall",
                explanation = "Un firewall revisa todo lo que entra y sale. Bloquea lo peligroso y permite lo seguro. Es como un filtro protector.",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.PUZZLE,
                        description = "Configura reglas de firewall: permitir/bloquear",
                        expectedOutcome = "Permitir navegación, bloquear descargas sospechosas",
                        hintOnFailure = "Las reglas se evalúan en orden. Primero las específicas."
                    )
                ),
                difficulty = ContentDifficulty.ELEMENTARY
            )
            
            AgeBracket.PRE_ADOLESCENT -> AgeAdaptiveContent(
                baseConcept = "cybersecurity",
                ageBracket = bracket,
                displayTitle = "Análisis de Malware",
                analogy = "Como detectives analizando pruebas de un crimen",
                technicalTerm = "Antivirus, Heurística",
                explanation = "Los antivirus usan firmas (patrones conocidos) y heurística (comportamiento sospechoso). También pueden analizar en sandbox.",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.MATCHING,
                        description = "Identifica tipos de malware por comportamiento",
                        expectedOutcome = "Virus ↔ replica, Troyano ↔ se disfraza, Ransomware ↔ secuestra",
                        hintOnFailure = "Troyano: parece inofensivo. Ransom: pide rescate."
                    )
                ),
                difficulty = ContentDifficulty.INTERMEDIATE
            )
            
            AgeBracket.ADOLESCENT_EARLY -> AgeAdaptiveContent(
                baseConcept = "cybersecurity",
                ageBracket = bracket,
                displayTitle = "Ingeniería Social y Phishing",
                analogy = "Como estafadores que manipulan personas, no sistemas",
                technicalTerm = "Social Engineering, OSINT",
                explanation = "La ingeniería social explota la confianza humana. El phishing suplanta identidades. OSINT recopila información pública para ataques dirigidos.",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.SIMULATION,
                        description = "Analiza un email de phishing y detecta señales",
                        expectedOutcome = "Identificar: remitente falso, URGENTE, links sospechosos",
                        hintOnFailure = "Verifica el dominio real al pasar el mouse sobre links"
                    )
                ),
                difficulty = ContentDifficulty.ADVANCED
            )
            
            AgeBracket.ADOLESCENT_LATE, AgeBracket.YOUNG_ADULT -> AgeAdaptiveContent(
                baseConcept = "cybersecurity",
                ageBracket = bracket,
                displayTitle = "Pentesting y Respuesta a Incidentes",
                analogy = "Como auditorías de seguridad y gestión de crisis",
                technicalTerm = "Penetration Testing, Incident Response, SIEM",
                explanation = "Metodologías de pentesting: reconocimiento, escaneo, explotación, post-explotación, informe. Incident response: preparación, detección, contención, erradicación, recuperación, lecciones.",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.SIMULATION,
                        description = "Investiga un incidente simulado de ransomware",
                        expectedOutcome = "Timeline, IOCs identificados, contención ejecutada",
                        hintOnFailure = "Sigue el framework: quarantine → forensic → eradicate → restore"
                    )
                ),
                difficulty = ContentDifficulty.EXPERT
            )
        }
    }
    
    private fun adaptDataPrivacy(bracket: AgeBracket): AgeAdaptiveContent {
        return when (bracket) {
            AgeBracket.EARLY_CHILDHOOD -> AgeAdaptiveContent(
                baseConcept = "data_privacy",
                ageBracket = bracket,
                displayTitle = "Mis Cosas Especiales",
                analogy = "Como tus juguetes favoritos que no compartes con cualquiera",
                technicalTerm = null,
                explanation = "Algunas cosas son solo tuyas: tu nombre completo, dónde vives, fotos tuyas. No las compartas con extraños en internet.",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.MATCHING,
                        description = "Clasifica información como 'Compartir' o 'Privado'",
                        expectedOutcome = "Nombre completo = Privado, Color favorito = Compartir",
                        hintOnFailure = "¿Un extraño podría encontrarte con esa información?"
                    )
                ),
                difficulty = ContentDifficulty.BEGINNER
            )
            
            AgeBracket.CHILDHOOD -> AgeAdaptiveContent(
                baseConcept = "data_privacy",
                ageBracket = bracket,
                displayTitle = "El Rastro Digital",
                analogy = "Como huellas que dejas en la playa",
                technicalTerm = "Huella Digital",
                explanation = "Todo lo que haces en internet deja huellas. Las empresas pueden ver qué sitios visitas y qué buscas. Tu información tiene valor.",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.PUZZLE,
                        description = "Minimiza el rastro digital de un personaje",
                        expectedOutcome = "Desactivar cookies, usar modo privado, VPN básica",
                        hintOnFailure = "Las cookies son como 'recordatorios' que sitios guardan"
                    )
                ),
                difficulty = ContentDifficulty.ELEMENTARY
            )
            
            AgeBracket.PRE_ADOLESCENT -> AgeAdaptiveContent(
                baseConcept = "data_privacy",
                ageBracket = bracket,
                displayTitle = "Regulaciones de Privacidad",
                analogy = "Como leyes que protegen tu información personal",
                technicalTerm = "GDPR, Consentimiento",
                explanation = "Leyes como GDPR protegen tus datos. Empresas necesitan tu consentimiento informado. Tienes derecho a saber qué datos tienen y pedir que los borren.",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.MATCHING,
                        description = "Empareja derechos GDPR con ejemplos",
                        expectedOutcome = "Acceso ↔ ver datos, Rectificación ↔ corregir, Olvido ↔ borrar",
                        hintOnFailure = "Derecho al olvido = borrar tus datos completamente"
                    )
                ),
                difficulty = ContentDifficulty.INTERMEDIATE
            )
            
            AgeBracket.ADOLESCENT_EARLY -> AgeAdaptiveContent(
                baseConcept = "data_privacy",
                ageBracket = bracket,
                displayTitle = "Anonimización y Pseudonimización",
                analogy = "Como usar máscaras vs disfraces completos",
                technicalTerm = "k-anonimato, Diferencial Privacy",
                explanation = "La pseudonimización reemplaza identificadores directos. La anonimización elimina toda posibilidad de re-identificación. Differential privacy agrega ruido matemático.",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.CODE_COMPLETION,
                        description = "Implementa k-anonimato en un dataset",
                        expectedOutcome = "Generalizar atributos quasi-identificadores hasta k=5",
                        hintOnFailure = "Generaliza: edad exacta → rango, código postal → primeros 3 dígitos"
                    )
                ),
                difficulty = ContentDifficulty.ADVANCED
            )
            
            AgeBracket.ADOLESCENT_LATE, AgeBracket.YOUNG_ADULT -> AgeAdaptiveContent(
                baseConcept = "data_privacy",
                ageBracket = bracket,
                displayTitle = "Privacy by Design y Compliance",
                analogy = "Arquitectura de sistemas con privacidad integrada",
                technicalTerm = "PbD, DPIA, TOMs",
                explanation = "Privacy by Design: protección desde el diseño, no como parche. DPIA evalúa riesgos antes de procesar datos. TOMs: medidas técnicas y organizativas como cifrado, minimización, retention limits.",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.SIMULATION,
                        description = "Conduce una DPIA para un sistema de reconocimiento facial",
                        expectedOutcome = "Identificar riesgos: mas surveillance, false positives, consent issues",
                        hintOnFailure = "Considera: necesidad, proporcionalidad, alternativas menos invasivas"
                    )
                ),
                difficulty = ContentDifficulty.EXPERT
            )
        }
    }
    
    private fun adaptProgrammingLogic(bracket: AgeBracket): AgeAdaptiveContent {
        return when (bracket) {
            AgeBracket.EARLY_CHILDHOOD -> AgeAdaptiveContent(
                baseConcept = "programming_logic",
                ageBracket = bracket,
                displayTitle = "Ordenando mi Día",
                analogy = "Como seguir pasos para vestirse",
                technicalTerm = null,
                explanation = "Los programas siguen pasos uno tras otro. Primero esto, luego esto. Si lo hacemos en orden diferente, ¡el resultado cambia!",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.SEQUENCE_ORDER,
                        description = "Ordena los pasos para hacer un sándwich",
                        expectedOutcome = "Pan → Mantequilla → Jamón → Queso → Pan",
                        hintOnFailure = "¿Puedes poner el jamón antes del pan?"
                    )
                ),
                difficulty = ContentDifficulty.BEGINNER
            )
            
            AgeBracket.CHILDHOOD -> AgeAdaptiveContent(
                baseConcept = "programming_logic",
                ageBracket = bracket,
                displayTitle = "Si... Entonces...",
                analogy = "Como decisiones en un cuento de 'elige tu propia aventura'",
                technicalTerm = "Condicional",
                explanation = "Los programas pueden decidir: SI pasa esto, ENTONCES haz esto, SINO haz lo otro. Es como elegir caminos.",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.DRAG_DROP,
                        description = "Crea un árbol de decisiones para identificar un animal",
                        expectedOutcome = "Árbol: ¿tiene plumas? → ave : ¿tiene escamas? → reptil",
                        hintOnFailure = "Cada pregunta debe dividir en dos grupos claros"
                    )
                ),
                difficulty = ContentDifficulty.ELEMENTARY
            )
            
            AgeBracket.PRE_ADOLESCENT -> AgeAdaptiveContent(
                baseConcept = "programming_logic",
                ageBracket = bracket,
                displayTitle = "Bucles y Variables",
                analogy = "Como contar usando tu mano, repitiendo hasta llegar a 5",
                technicalTerm = "Iteración, Estado",
                explanation = "Las variables guardan información que cambia. Los bucles repiten acciones. Juntas son poderosas: contar, sumar, buscar.",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.CODE_COMPLETION,
                        description = "Completa un bucle que suma números del 1 al 10",
                        expectedOutcome = "sum = 0; for i in 1..10: sum += i; result = 55",
                        hintOnFailure = "La variable 'sum' debe empezar en 0 y sumar cada número"
                    )
                ),
                difficulty = ContentDifficulty.INTERMEDIATE
            )
            
            AgeBracket.ADOLESCENT_EARLY -> AgeAdaptiveContent(
                baseConcept = "programming_logic",
                ageBracket = bracket,
                displayTitle = "Estructuras de Datos",
                analogy = "Como organizar una biblioteca: listas, pilas, colas, mapas",
                technicalTerm = "Array, Stack, Queue, HashMap",
                explanation = "Diferentes problemas necesitan diferentes estructuras. Arrays para acceso indexado, pilas para undo, colas para FIFO, hashmaps para búsqueda O(1).",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.CODE_COMPLETION,
                        description = "Implementa una pila (stack) con push/pop/peek",
                        expectedOutcome = "LIFO: Last In First Out, operaciones O(1)",
                        hintOnFailure = "Usa una lista y solo opera en el último elemento"
                    )
                ),
                difficulty = ContentDifficulty.ADVANCED
            )
            
            AgeBracket.ADOLESCENT_LATE, AgeBracket.YOUNG_ADULT -> AgeAdaptiveContent(
                baseConcept = "programming_logic",
                ageBracket = bracket,
                displayTitle = "Paradigmas de Programación",
                analogy = "Diferentes formas de pensar: imperativa, funcional, orientada a objetos",
                technicalTerm = "OOP, FP, SOLID",
                explanation = "Paradigmas ofrecen diferentes abstracciones. OOP encapsula estado y comportamiento. FP favorece inmutabilidad y composición. SOLID principios para diseño mantenible.",
                interactiveElements = listOf(
                    InteractiveElement(
                        type = ElementType.CODE_COMPLETION,
                        description = "Refactoriza código siguiendo principio Open/Closed",
                        expectedOutcome = "Usar polimorfismo para extender sin modificar código existente",
                        hintOnFailure = "Interfaces permiten nuevas implementaciones sin tocar código viejo"
                    ),
                    InteractiveElement(
                        type = ElementType.SIMULATION,
                        description = "Compara performance OOP vs FP en procesamiento de listas",
                        expectedOutcome = "FP lazy evaluation puede ser más eficiente para cadenas largas",
                        hintOnFailure = "Lazy evaluation evita crear estructuras intermedias"
                    )
                ),
                difficulty = ContentDifficulty.EXPERT
            )
        }
    }
    
    private fun createGenericAdaptation(baseConcept: String, bracket: AgeBracket): AgeAdaptiveContent {
        return AgeAdaptiveContent(
            baseConcept = baseConcept,
            ageBracket = bracket,
            displayTitle = "Concepto: ${baseConcept.replace("_", " ").capitalize()}",
            analogy = "Concepto técnico adaptado para ${bracket.range.first}-${bracket.range.last} años",
            technicalTerm = if (bracket.cognitiveLevel.ordinal >= CognitiveLevel.FORMAL_EARLY.ordinal) baseConcept else null,
            explanation = "Contenido educativo adaptado al nivel cognitivo ${bracket.cognitiveLevel.name}",
            interactiveElements = listOf(
                InteractiveElement(
                    type = ElementType.MULTIPLE_CHOICE,
                    description = "Evaluación de comprensión",
                    expectedOutcome = "Respuesta correcta basada en la explicación",
                    hintOnFailure = "Revisa la explicación anterior y prueba de nuevo"
                )
            ),
            difficulty = ContentDifficulty.entries[bracket.cognitiveLevel.ordinal.coerceAtMost(4)]
        )
    }
    
    private fun String.capitalize(): String {
        return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
}
