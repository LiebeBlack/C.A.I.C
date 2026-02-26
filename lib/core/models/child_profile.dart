/// Modelo de usuario/niño que usa la aplicación
class ChildProfile {
  final String id;
  String name;
  int age;
  String avatar;
  DateTime createdAt;
  int totalPlayTimeMinutes;
  int currentLevel;
  Map<String, int> levelProgress;
  List<String> earnedBadges;

  ChildProfile({
    required this.id,
    required this.name,
    required this.age,
    this.avatar = 'default',
    DateTime? createdAt,
    this.totalPlayTimeMinutes = 0,
    this.currentLevel = 1,
    Map<String, int>? levelProgress,
    List<String>? earnedBadges,
  })  : createdAt = createdAt ?? DateTime.now(),
        levelProgress = levelProgress ?? {},
        earnedBadges = earnedBadges ?? [];

  Map<String, dynamic> toJson() => {
        'id': id,
        'name': name,
        'age': age,
        'avatar': avatar,
        'createdAt': createdAt.toIso8601String(),
        'totalPlayTimeMinutes': totalPlayTimeMinutes,
        'currentLevel': currentLevel,
        'levelProgress': levelProgress,
        'earnedBadges': earnedBadges,
      };

  factory ChildProfile.fromJson(Map<String, dynamic> json) => ChildProfile(
        id: json['id'] as String,
        name: json['name'] as String,
        age: json['age'] as int,
        avatar: json['avatar'] as String? ?? 'default',
        createdAt: DateTime.parse(json['createdAt'] as String),
        totalPlayTimeMinutes: json['totalPlayTimeMinutes'] as int? ?? 0,
        currentLevel: json['currentLevel'] as int? ?? 1,
        levelProgress: Map<String, int>.from(json['levelProgress'] as Map? ?? {}),
        earnedBadges: List<String>.from(json['earnedBadges'] as List? ?? []),
      );

  ChildProfile copyWith({
    String? name,
    int? age,
    String? avatar,
    int? totalPlayTimeMinutes,
    int? currentLevel,
    Map<String, int>? levelProgress,
    List<String>? earnedBadges,
  }) =>
      ChildProfile(
        id: id,
        name: name ?? this.name,
        age: age ?? this.age,
        avatar: avatar ?? this.avatar,
        createdAt: createdAt,
        totalPlayTimeMinutes: totalPlayTimeMinutes ?? this.totalPlayTimeMinutes,
        currentLevel: currentLevel ?? this.currentLevel,
        levelProgress: levelProgress ?? Map<String, int>.from(this.levelProgress),
        earnedBadges: earnedBadges ?? List<String>.from(this.earnedBadges),
      );
}
