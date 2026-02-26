import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:google_fonts/google_fonts.dart';

/// Paleta de colores inspirada en la Isla de Margarita
/// Tonos de azul mar, arena, vegetación tropical y atardeceres
class IslaColors {
  // Colores primarios - Océano y playas
  static const Color oceanBlue = Color(0xFF0066CC);
  static const Color oceanLight = Color(0xFF4DA6FF);
  static const Color oceanDark = Color(0xFF003D7A);
  
  // Colores de arena y sol
  static const Color sand = Color(0xFFF4E4C1);
  static const Color sandLight = Color(0xFFFDF6E3);
  static const Color sunYellow = Color(0xFFFFD93D);
  static const Color sunOrange = Color(0xFFFFA500);
  
  // Colores tropicales - Vegetación
  static const Color palmGreen = Color(0xFF2E7D32);
  static const Color palmLight = Color(0xFF66BB6A);
  static const Color palmDark = Color(0xFF1B5E20);
  
  // Colores de atardecer margariteño
  static const Color sunsetPink = Color(0xFFFF6B9D);
  static const Color sunsetPurple = Color(0xFF9C27B0);
  static const Color sunsetCoral = Color(0xFFFF7F50);
  
  // Colores funcionales
  static const Color success = Color(0xFF4CAF50);
  static const Color warning = Color(0xFFFFC107);
  static const Color error = Color(0xFFE53935);
  static const Color info = Color(0xFF2196F3);
  
  // Neutros
  static const Color white = Color(0xFFFFFFFF);
  static const Color black = Color(0xFF212121);
  static const Color grey = Color(0xFF9E9E9E);
  static const Color greyLight = Color(0xFFE0E0E0);
  static const Color greyDark = Color(0xFF616161);
}

/// Configuración de fuentes accesibles para niños
/// Nunito es amigable, redondeada y legible para pre-lectores
class IslaTypography {
  static TextTheme get textTheme {
    return GoogleFonts.nunitoTextTheme().copyWith(
      displayLarge: GoogleFonts.nunito(
        fontSize: 38,
        fontWeight: FontWeight.bold,
        color: IslaColors.black,
      ),
      displayMedium: GoogleFonts.nunito(
        fontSize: 28,
        fontWeight: FontWeight.bold,
        color: IslaColors.black,
      ),
      displaySmall: GoogleFonts.nunito(
        fontSize: 22,
        fontWeight: FontWeight.bold,
        color: IslaColors.black,
      ),
      headlineLarge: GoogleFonts.nunito(
        fontSize: 19,
        fontWeight: FontWeight.w800,
        color: IslaColors.black,
      ),
      headlineMedium: GoogleFonts.nunito(
        fontSize: 16,
        fontWeight: FontWeight.w700,
        color: IslaColors.black,
      ),
      headlineSmall: GoogleFonts.nunito(
        fontSize: 14,
        fontWeight: FontWeight.w700,
        color: IslaColors.black,
      ),
      titleLarge: GoogleFonts.nunito(
        fontSize: 16,
        fontWeight: FontWeight.w600,
        color: IslaColors.black,
      ),
      titleMedium: GoogleFonts.nunito(
        fontSize: 13,
        fontWeight: FontWeight.w600,
        color: IslaColors.black,
      ),
      titleSmall: GoogleFonts.nunito(
        fontSize: 11,
        fontWeight: FontWeight.w600,
        color: IslaColors.black,
      ),
      bodyLarge: GoogleFonts.nunito(
        fontSize: 14,
        fontWeight: FontWeight.normal,
        color: IslaColors.black,
      ),
      bodyMedium: GoogleFonts.nunito(
        fontSize: 13,
        fontWeight: FontWeight.normal,
        color: IslaColors.black,
      ),
      bodySmall: GoogleFonts.nunito(
        fontSize: 11,
        fontWeight: FontWeight.normal,
        color: IslaColors.black,
      ),
    );
  }
}

/// Temas de la aplicación
class IslaThemes {
  static ThemeData get lightTheme {
    return ThemeData(
      useMaterial3: true,
      brightness: Brightness.light,
      colorScheme: ColorScheme.fromSeed(
        seedColor: IslaColors.oceanBlue,
        brightness: Brightness.light,
        primary: IslaColors.oceanBlue,
        secondary: IslaColors.sunYellow,
        tertiary: IslaColors.palmGreen,
        surface: IslaColors.sandLight,
        background: IslaColors.sandLight,
        onPrimary: IslaColors.white,
        onSecondary: IslaColors.black,
        onSurface: IslaColors.black,
        onBackground: IslaColors.black,
      ),
      textTheme: IslaTypography.textTheme,
      scaffoldBackgroundColor: IslaColors.sandLight,
      appBarTheme: AppBarTheme(
        backgroundColor: IslaColors.oceanBlue,
        foregroundColor: IslaColors.white,
        elevation: 0,
        centerTitle: true,
        titleTextStyle: IslaTypography.textTheme.headlineMedium?.copyWith(
          color: IslaColors.white,
        ),
      ),
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          backgroundColor: IslaColors.oceanBlue,
          foregroundColor: IslaColors.white,
          padding: const EdgeInsets.symmetric(horizontal: 26, vertical: 12),
          minimumSize: const Size(96, 44),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(16),
          ),
          textStyle: IslaTypography.textTheme.titleLarge?.copyWith(
            color: IslaColors.white,
          ),
        ),
      ),
      outlinedButtonTheme: OutlinedButtonThemeData(
        style: OutlinedButton.styleFrom(
          foregroundColor: IslaColors.oceanBlue,
          padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
          minimumSize: const Size(80, 38),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(16),
          ),
          side: const BorderSide(color: IslaColors.oceanBlue, width: 2),
          textStyle: IslaTypography.textTheme.titleMedium,
        ),
      ),
      cardTheme: CardThemeData(
        elevation: 4,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(20),
        ),
        color: IslaColors.white,
      ),
      inputDecorationTheme: InputDecorationTheme(
        filled: true,
        fillColor: IslaColors.white,
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(16),
          borderSide: const BorderSide(color: IslaColors.greyLight),
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(16),
          borderSide: const BorderSide(color: IslaColors.greyLight),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(16),
          borderSide: const BorderSide(color: IslaColors.oceanBlue, width: 2),
        ),
        contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 16),
      ),
    );
  }
}

/// Extensiones para acceso rápido a colores en widgets
extension IslaColorScheme on BuildContext {
  Color get oceanBlue => IslaColors.oceanBlue;
  Color get oceanLight => IslaColors.oceanLight;
  Color get oceanDark => IslaColors.oceanDark;
  Color get sand => IslaColors.sand;
  Color get sandLight => IslaColors.sandLight;
  Color get sunYellow => IslaColors.sunYellow;
  Color get sunOrange => IslaColors.sunOrange;
  Color get palmGreen => IslaColors.palmGreen;
  Color get palmLight => IslaColors.palmLight;
  Color get palmDark => IslaColors.palmDark;
  Color get sunsetPink => IslaColors.sunsetPink;
  Color get sunsetPurple => IslaColors.sunsetPurple;
  Color get sunsetCoral => IslaColors.sunsetCoral;
}
