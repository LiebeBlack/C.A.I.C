import 'package:flutter/material.dart';

/// Modelo de insignias del sistema de gamificación
/// Insignias inspiradas en la cultura y naturaleza de Margarita
class Badge {
  final String id;
  final String name;
  final String description;
  final String iconPath;
  final Color color;
  final int requiredPoints;
  final String levelId;

  const Badge({
    required this.id,
    required this.name,
    required this.description,
    required this.iconPath,
    required this.color,
    required this.requiredPoints,
    required this.levelId,
  });
}

/// Colección de Insignias Margariteñas disponibles
class IslaBadges {
  static const List<Badge> allBadges = [
    // Nivel 1: Mi Primer Encuentro
    Badge(
      id: 'primer_encuentro',
      name: 'Perla de Sabiduría',
      description: '¡Aprendiste a cuidar tu dispositivo como una verdadera perla!',
      iconPath: 'assets/icons/pearl.svg',
      color: Color(0xFF4DA6FF),
      requiredPoints: 100,
      levelId: 'level_1',
    ),
    Badge(
      id: 'maestro_botones',
      name: 'Explorador de Botones',
      description: 'Dominaste todos los botones y gestos del teléfono',
      iconPath: 'assets/icons/buttons.svg',
      color: Color(0xFFFFD93D),
      requiredPoints: 200,
      levelId: 'level_1',
    ),
    
    // Nivel 2: Conectados
    Badge(
      id: 'comunicador_seguro',
      name: 'Palometa del Mar',
      description: '¡Aprendiste a comunicarte de forma segura!',
      iconPath: 'assets/icons/fish.svg',
      color: Color(0xFF4CAF50),
      requiredPoints: 150,
      levelId: 'level_2',
    ),
    Badge(
      id: 'llamada_experta',
      name: 'Capitán del Teléfono',
      description: 'Realizaste tu primera llamada de práctica',
      iconPath: 'assets/icons/captain.svg',
      color: Color(0xFF0066CC),
      requiredPoints: 250,
      levelId: 'level_2',
    ),
    
    // Nivel 3: Explorador Seguro
    Badge(
      id: 'detective_seguro',
      name: 'Detective de la Isla',
      description: 'Descubriste qué información es segura en internet',
      iconPath: 'assets/icons/detective.svg',
      color: Color(0xFF9C27B0),
      requiredPoints: 200,
      levelId: 'level_3',
    ),
    Badge(
      id: 'escudo_digital',
      name: 'Escudo del Castillo',
      description: 'Proteges tu información como el castillo protege la isla',
      iconPath: 'assets/icons/shield.svg',
      color: Color(0xFF607D8B),
      requiredPoints: 300,
      levelId: 'level_3',
    ),
    
    // Nivel 4: Super Tareas
    Badge(
      id: 'calculador_frutas',
      name: 'Rey del Mango',
      description: '¡Eres experto sumando frutas tropicales!',
      iconPath: 'assets/icons/mango.svg',
      color: Color(0xFFFFA500),
      requiredPoints: 200,
      levelId: 'level_4',
    ),
    Badge(
      id: 'fotografo_misiones',
      name: 'Ojo de Cotorra',
      description: 'Capturaste todas las misiones con tu cámara',
      iconPath: 'assets/icons/camera.svg',
      color: Color(0xFFE91E63),
      requiredPoints: 300,
      levelId: 'level_4',
    ),
    
    // Nivel 5: Pequeño Artista
    Badge(
      id: 'artista_isla',
      name: 'Pintor de Atardeceres',
      description: 'Creaste obras de arte inspiradas en Margarita',
      iconPath: 'assets/icons/palette.svg',
      color: Color(0xFFFF6B9D),
      requiredPoints: 250,
      levelId: 'level_5',
    ),
    Badge(
      id: 'musician_tropical',
      name: 'Ritmo de Tambor',
      description: 'Compones música como los tambores de la isla',
      iconPath: 'assets/icons/drum.svg',
      color: Color(0xFFFF5722),
      requiredPoints: 350,
      levelId: 'level_5',
    ),
    
    // Logros especiales
    Badge(
      id: 'super_estrella',
      name: 'Estrella de Margarita',
      description: '¡Completaste todos los niveles de Isla Digital!',
      iconPath: 'assets/icons/star.svg',
      color: Color(0xFFFFD700),
      requiredPoints: 1000,
      levelId: 'all',
    ),
    Badge(
      id: 'explorador_diario',
      name: 'Sol Naciente',
      description: 'Usas Isla Digital todos los días',
      iconPath: 'assets/icons/sun.svg',
      color: Color(0xFFFFEB3B),
      requiredPoints: 500,
      levelId: 'daily',
    ),
  ];

  static Badge? getById(String id) {
    try {
      return allBadges.firstWhere((b) => b.id == id);
    } catch (e) {
      return null;
    }
  }

  static List<Badge> getByLevel(String levelId) {
    return allBadges.where((b) => b.levelId == levelId).toList();
  }
}
