package com.LBs.EEDA.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class GameLevel(
    val id: Int,
    val title: String,
    val description: String,
    val phase: DigitalPhase,
    val difficulty: Int, // 1-5
    val xpReward: Long,
    val icon: String
)

object LevelData {
    fun getLevels(): List<GameLevel> = listOf(
        // SENSORIAL
        GameLevel(1, "El Despertar del Tacto", "Aprende a tocar los elementos que brillan.", DigitalPhase.SENSORIAL, 1, 50, "touch"),
        GameLevel(2, "Deslizamiento Estelar", "Sigue el camino de estrellas deslizando tu dedo.", DigitalPhase.SENSORIAL, 1, 60, "swipe"),
        GameLevel(3, "El Guardián de Secretos", "Encuentra la llave oculta (tu primera contraseña).", DigitalPhase.SENSORIAL, 2, 100, "security"),
        
        // CREATIVE
        GameLevel(10, "Arquitecto de Píxeles", "Construye una casa usando formas geométricas.", DigitalPhase.CREATIVE, 2, 200, "build"),
        GameLevel(11, "El Laberinto Lógico", "Guía al robot usando comandos de flechas.", DigitalPhase.CREATIVE, 3, 250, "code"),
        GameLevel(12, "Cazador de Virus", "Identifica y elimina los correos sospechosos.", DigitalPhase.CREATIVE, 3, 300, "shield"),
        
        // PROFESSIONAL
        GameLevel(20, "Entrenador de Modelos", "Clasifica imágenes para ayudar a una IA.", DigitalPhase.PROFESSIONAL, 4, 500, "ai"),
        GameLevel(21, "Analista de Mercados", "Decide cuándo comprar o vender en el simulador.", DigitalPhase.PROFESSIONAL, 4, 600, "finance"),
        GameLevel(22, "Ciber-Auditoría", "Protege una red empresarial de un ataque simulado.", DigitalPhase.PROFESSIONAL, 5, 800, "admin")
    )
}
