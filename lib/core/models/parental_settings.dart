/// Configuración del control parental.

/// Configuración del control parental.
/// Modelo separado para mantener la separación de responsabilidades.
class ParentalSettings {
  const ParentalSettings({
    this.dailyTimeLimitMinutes = 30,
    this.soundEnabled = true,
    this.musicEnabled = true,
    this.notificationsEnabled = true,
    this.allowedContacts = const [],
    this.lastAccess,
  });

  final int dailyTimeLimitMinutes;
  final bool soundEnabled;
  final bool musicEnabled;
  final bool notificationsEnabled;
  final List<String> allowedContacts;
  final DateTime? lastAccess;

  ParentalSettings copyWith({
    int? dailyTimeLimitMinutes,
    bool? soundEnabled,
    bool? musicEnabled,
    bool? notificationsEnabled,
    List<String>? allowedContacts,
    DateTime? lastAccess,
  }) {
    return ParentalSettings(
      dailyTimeLimitMinutes:
          dailyTimeLimitMinutes ?? this.dailyTimeLimitMinutes,
      soundEnabled: soundEnabled ?? this.soundEnabled,
      musicEnabled: musicEnabled ?? this.musicEnabled,
      notificationsEnabled: notificationsEnabled ?? this.notificationsEnabled,
      allowedContacts: allowedContacts ?? this.allowedContacts,
      lastAccess: lastAccess ?? this.lastAccess,
    );
  }

  /// Serializa el objeto a un Map para persistencia.
  Map<String, dynamic> toJson() => {
        'dailyTimeLimitMinutes': dailyTimeLimitMinutes,
        'soundEnabled': soundEnabled,
        'musicEnabled': musicEnabled,
        'notificationsEnabled': notificationsEnabled,
        'allowedContacts': allowedContacts,
      };

  /// Crea una instancia a partir de un Map.
  factory ParentalSettings.fromJson(Map<String, dynamic> json) {
    return ParentalSettings(
      dailyTimeLimitMinutes: json['dailyTimeLimitMinutes'] as int? ?? 30,
      soundEnabled: json['soundEnabled'] as bool? ?? true,
      musicEnabled: json['musicEnabled'] as bool? ?? true,
      notificationsEnabled: json['notificationsEnabled'] as bool? ?? true,
      allowedContacts:
          List<String>.from(json['allowedContacts'] as List? ?? []),
    );
  }
}
