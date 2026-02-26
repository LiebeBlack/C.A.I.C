import 'package:flutter/material.dart';

/// Utilidades compartidas para manipulación de colores.
/// Eliminan la duplicación de _darkenColor/_lightenColor en múltiples widgets.
class ColorUtils {
  /// Oscurece un color reduciendo su luminosidad.
  static Color darken(Color color, [double amount = 0.1]) {
    final hsl = HSLColor.fromColor(color);
    return hsl
        .withLightness((hsl.lightness - amount).clamp(0.0, 1.0))
        .toColor();
  }

  /// Aclara un color aumentando su luminosidad.
  static Color lighten(Color color, [double amount = 0.1]) {
    final hsl = HSLColor.fromColor(color);
    return hsl
        .withLightness((hsl.lightness + amount).clamp(0.0, 1.0))
        .toColor();
  }
}
