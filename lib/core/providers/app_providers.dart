import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../models/models.dart';
import '../services/services.dart';

/// Provider para el perfil del niño actual
final currentProfileProvider = StateNotifierProvider<ProfileNotifier, ChildProfile?>((ref) {
  final notifier = ProfileNotifier();
  notifier.loadSavedProfile();
  return notifier;
});

/// Notifier que maneja el estado del perfil con persistencia
class ProfileNotifier extends StateNotifier<ChildProfile?> {
  ProfileNotifier() : super(null);

  void loadSavedProfile() {
    final savedProfile = LocalStorageService.loadProfile();
    if (savedProfile != null) {
      state = savedProfile;
    }
  }

  Future<void> createProfile(String name, int age, {String avatar = 'default'}) async {
    final profile = ChildProfile(
      id: DateTime.now().millisecondsSinceEpoch.toString(),
      name: name,
      age: age,
      avatar: avatar,
    );
    state = profile;
    await LocalStorageService.saveProfile(profile);
  }

  Future<void> updateProfile({String? name, int? age, String? avatar}) async {
    if (state != null) {
      final updated = state!.copyWith(
        name: name,
        age: age,
        avatar: avatar,
      );
      state = updated;
      await LocalStorageService.saveProfile(updated);
    }
  }

  Future<void> addProgress(String levelId, int points) async {
    if (state != null) {
      final newProgress = Map<String, int>.from(state!.levelProgress);
      newProgress[levelId] = (newProgress[levelId] ?? 0) + points;
      final updated = state!.copyWith(levelProgress: newProgress);
      state = updated;
      await LocalStorageService.saveProfile(updated);
      await LocalStorageService.saveLevelProgress(levelId, newProgress[levelId]!);
    }
  }

  Future<void> unlockLevel(int level) async {
    if (state != null && level > state!.currentLevel) {
      final updated = state!.copyWith(currentLevel: level);
      state = updated;
      await LocalStorageService.saveProfile(updated);
    }
  }

  Future<void> addBadge(String badgeId) async {
    if (state != null && !state!.earnedBadges.contains(badgeId)) {
      final newBadges = List<String>.from(state!.earnedBadges)..add(badgeId);
      final updated = state!.copyWith(earnedBadges: newBadges);
      state = updated;
      await LocalStorageService.saveProfile(updated);
      await LocalStorageService.addBadge(badgeId);
    }
  }

  Future<void> addPlayTime(int minutes) async {
    if (state != null) {
      final updated = state!.copyWith(
        totalPlayTimeMinutes: state!.totalPlayTimeMinutes + minutes,
      );
      state = updated;
      await LocalStorageService.saveProfile(updated);
      await LocalStorageService.addPlayTime(minutes);
    }
  }

  Future<void> clear() async {
    state = null;
    await LocalStorageService.deleteProfile();
  }
}

/// Provider para la configuración parental con persistencia
final parentalSettingsProvider = StateNotifierProvider<ParentalSettingsNotifier, ParentalSettings>((ref) {
  final notifier = ParentalSettingsNotifier();
  notifier.loadSavedSettings();
  return notifier;
});

/// Configuración del control parental
class ParentalSettings {
  final int dailyTimeLimitMinutes;
  final bool soundEnabled;
  final bool musicEnabled;
  final bool notificationsEnabled;
  final List<String> allowedContacts;
  final DateTime? lastAccess;

  const ParentalSettings({
    this.dailyTimeLimitMinutes = 30,
    this.soundEnabled = true,
    this.musicEnabled = true,
    this.notificationsEnabled = true,
    this.allowedContacts = const [],
    this.lastAccess,
  });

  ParentalSettings copyWith({
    int? dailyTimeLimitMinutes,
    bool? soundEnabled,
    bool? musicEnabled,
    bool? notificationsEnabled,
    List<String>? allowedContacts,
    DateTime? lastAccess,
  }) {
    return ParentalSettings(
      dailyTimeLimitMinutes: dailyTimeLimitMinutes ?? this.dailyTimeLimitMinutes,
      soundEnabled: soundEnabled ?? this.soundEnabled,
      musicEnabled: musicEnabled ?? this.musicEnabled,
      notificationsEnabled: notificationsEnabled ?? this.notificationsEnabled,
      allowedContacts: allowedContacts ?? this.allowedContacts,
      lastAccess: lastAccess ?? this.lastAccess,
    );
  }
}

class ParentalSettingsNotifier extends StateNotifier<ParentalSettings> {
  ParentalSettingsNotifier() : super(const ParentalSettings());

  void loadSavedSettings() {
    final savedSettings = LocalStorageService.loadParentalSettings();
    state = savedSettings;
  }

  Future<void> updateTimeLimit(int minutes) async {
    final updated = state.copyWith(dailyTimeLimitMinutes: minutes);
    state = updated;
    await LocalStorageService.saveParentalSettings(updated);
  }

  Future<void> toggleSound() async {
    final updated = state.copyWith(soundEnabled: !state.soundEnabled);
    state = updated;
    await LocalStorageService.saveParentalSettings(updated);
  }

  Future<void> toggleMusic() async {
    final updated = state.copyWith(musicEnabled: !state.musicEnabled);
    state = updated;
    await LocalStorageService.saveParentalSettings(updated);
  }

  Future<void> addAllowedContact(String contact) async {
    final newContacts = List<String>.from(state.allowedContacts)..add(contact);
    final updated = state.copyWith(allowedContacts: newContacts);
    state = updated;
    await LocalStorageService.saveParentalSettings(updated);
  }

  Future<void> removeAllowedContact(String contact) async {
    final newContacts = List<String>.from(state.allowedContacts)..remove(contact);
    final updated = state.copyWith(allowedContacts: newContacts);
    state = updated;
    await LocalStorageService.saveParentalSettings(updated);
  }
}

/// Provider para el estado de la aplicación
final appStateProvider = StateNotifierProvider<AppStateNotifier, AppState>((ref) {
  return AppStateNotifier();
});

class AppState {
  final bool isLoading;
  final String? currentRoute;
  final bool showCelebration;

  const AppState({
    this.isLoading = false,
    this.currentRoute,
    this.showCelebration = false,
  });

  AppState copyWith({
    bool? isLoading,
    String? currentRoute,
    bool? showCelebration,
  }) {
    return AppState(
      isLoading: isLoading ?? this.isLoading,
      currentRoute: currentRoute ?? this.currentRoute,
      showCelebration: showCelebration ?? this.showCelebration,
    );
  }
}

class AppStateNotifier extends StateNotifier<AppState> {
  AppStateNotifier() : super(const AppState());

  void setLoading(bool loading) {
    state = state.copyWith(isLoading: loading);
  }

  void setRoute(String route) {
    state = state.copyWith(currentRoute: route);
  }

  void triggerCelebration() {
    state = state.copyWith(showCelebration: true);
    Future.delayed(const Duration(seconds: 3), () {
      state = state.copyWith(showCelebration: false);
    });
  }
}

/// Provider para tracking de progreso en niveles
final levelProgressProvider = StateNotifierProvider.family<LevelProgressNotifier, int, String>(
  (ref, levelId) => LevelProgressNotifier(levelId),
);

class LevelProgressNotifier extends StateNotifier<int> {
  final String levelId;
  LevelProgressNotifier(this.levelId) : super(0);

  void addPoints(int points) {
    state = (state + points).clamp(0, 100);
  }

  void setProgress(int progress) {
    state = progress.clamp(0, 100);
  }

  void reset() {
    state = 0;
  }
}
