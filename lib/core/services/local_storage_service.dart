import 'dart:convert';
import 'package:shared_preferences/shared_preferences.dart';
import '../models/models.dart';
import '../providers/app_providers.dart';

/// Servicio de persistencia local usando SharedPreferences
/// Almacena el perfil del niño, configuración parental y progreso
class LocalStorageService {
  static const String _profileKey = 'child_profile';
  static const String _settingsKey = 'parental_settings';
  static const String _badgesKey = 'earned_badges';
  static const String _playTimeKey = 'total_play_time';
  static const String _lastSessionKey = 'last_session';

  static late SharedPreferences _prefs;

  /// Inicializar el servicio de almacenamiento
  static Future<void> initialize() async {
    try {
      _prefs = await SharedPreferences.getInstance();
    } catch (e) {
      // Manejar error de inicialización
      throw Exception('No se pudo inicializar SharedPreferences: $e');
    }
  }

  /// Guardar perfil del niño
  static Future<bool> saveProfile(ChildProfile profile) async {
    final jsonString = jsonEncode(profile.toJson());
    return await _prefs.setString(_profileKey, jsonString);
  }

  /// Cargar perfil del niño
  static ChildProfile? loadProfile() {
    try {
      final jsonString = _prefs.getString(_profileKey);
      if (jsonString == null || jsonString.isEmpty) return null;
      
      final json = jsonDecode(jsonString) as Map<String, dynamic>;
      return ChildProfile.fromJson(json);
    } catch (e) {
      // Manejar error de parsing
      return null;
    }
  }

  /// Eliminar perfil
  static Future<bool> deleteProfile() async {
    return await _prefs.remove(_profileKey);
  }

  /// Guardar configuración parental
  static Future<bool> saveParentalSettings(ParentalSettings settings) async {
    final json = {
      'dailyTimeLimitMinutes': settings.dailyTimeLimitMinutes,
      'soundEnabled': settings.soundEnabled,
      'musicEnabled': settings.musicEnabled,
      'notificationsEnabled': settings.notificationsEnabled,
      'allowedContacts': settings.allowedContacts,
    };
    return await _prefs.setString(_settingsKey, jsonEncode(json));
  }

  /// Cargar configuración parental
  static ParentalSettings loadParentalSettings() {
    try {
      final jsonString = _prefs.getString(_settingsKey);
      if (jsonString == null || jsonString.isEmpty) return const ParentalSettings();
      
      final json = jsonDecode(jsonString) as Map<String, dynamic>;
      return ParentalSettings(
        dailyTimeLimitMinutes: json['dailyTimeLimitMinutes'] as int? ?? 30,
        soundEnabled: json['soundEnabled'] as bool? ?? true,
        musicEnabled: json['musicEnabled'] as bool? ?? true,
        notificationsEnabled: json['notificationsEnabled'] as bool? ?? true,
        allowedContacts: List<String>.from(json['allowedContacts'] as List? ?? []),
      );
    } catch (e) {
      // Manejar error de parsing
      return const ParentalSettings();
    }
  }

  /// Guardar tiempo de juego total
  static Future<bool> savePlayTime(int minutes) async {
    return await _prefs.setInt(_playTimeKey, minutes);
  }

  /// Cargar tiempo de juego total
  static int loadPlayTime() {
    return _prefs.getInt(_playTimeKey) ?? 0;
  }

  /// Agregar tiempo de juego
  static Future<int> addPlayTime(int minutes) async {
    final current = loadPlayTime();
    final updated = current + minutes;
    await savePlayTime(updated);
    return updated;
  }

  /// Guardar sesión
  static Future<bool> saveLastSession() async {
    return await _prefs.setString(_lastSessionKey, DateTime.now().toIso8601String());
  }

  /// Cargar última sesión
  static DateTime? loadLastSession() {
    try {
      final dateString = _prefs.getString(_lastSessionKey);
      if (dateString == null || dateString.isEmpty) return null;
      return DateTime.tryParse(dateString);
    } catch (e) {
      // Manejar error de parsing
      return null;
    }
  }

  /// Verificar si es un nuevo día
  static bool isNewDay() {
    final lastSession = loadLastSession();
    if (lastSession == null) return true;
    
    final now = DateTime.now();
    return lastSession.day != now.day ||
           lastSession.month != now.month ||
           lastSession.year != now.year;
  }

  /// Guardar progreso de nivel
  static Future<bool> saveLevelProgress(String levelId, int progress) async {
    return await _prefs.setInt('progress_$levelId', progress);
  }

  /// Cargar progreso de nivel
  static int loadLevelProgress(String levelId) {
    return _prefs.getInt('progress_$levelId') ?? 0;
  }

  /// Guardar insignias ganadas
  static Future<bool> saveEarnedBadges(List<String> badges) async {
    return await _prefs.setStringList(_badgesKey, badges);
  }

  /// Cargar insignias ganadas
  static List<String> loadEarnedBadges() {
    try {
      final badges = _prefs.getStringList(_badgesKey);
      return badges ?? [];
    } catch (e) {
      // Manejar error de carga
      return [];
    }
  }

  /// Agregar insignia
  static Future<List<String>> addBadge(String badgeId) async {
    final badges = loadEarnedBadges();
    if (!badges.contains(badgeId)) {
      badges.add(badgeId);
      await saveEarnedBadges(badges);
    }
    return badges;
  }

  /// Limpiar todos los datos (usar con precaución)
  static Future<bool> clearAll() async {
    return await _prefs.clear();
  }

  /// Obtener estadísticas para padres
  static Map<String, dynamic> getStatsForParents() {
    final profile = loadProfile();
    final playTime = loadPlayTime();
    final badges = loadEarnedBadges();
    final lastSession = loadLastSession();
    
    return {
      'profile': profile?.toJson(),
      'totalPlayTimeMinutes': playTime,
      'earnedBadges': badges,
      'lastSession': lastSession?.toIso8601String(),
      'isNewDay': isNewDay(),
    };
  }
}
