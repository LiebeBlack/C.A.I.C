import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../core/theme/app_theme.dart';
import '../../core/providers/app_providers.dart';
import '../widgets/island_background.dart';
import '../widgets/progress_widgets.dart';
import '../levels/level1/level1_screen.dart';
import '../levels/level2/level2_screen.dart';
import '../levels/level3/level3_screen.dart';
import '../levels/level4/level4_screen.dart';
import '../levels/level5/level5_screen.dart';

class LevelSelectScreen extends ConsumerWidget {
  const LevelSelectScreen({super.key});

  final List<Map<String, dynamic>> levels = const [
    {
      'id': 'level_1',
      'title': 'Mi Primer Encuentro',
      'subtitle': 'Aprende a usar tu teléfono',
      'icon': Icons.touch_app,
      'color': IslaColors.oceanBlue,
      'unlocked': true,
    },
    {
      'id': 'level_2',
      'title': 'Conectados',
      'subtitle': 'Llamadas y mensajes seguros',
      'icon': Icons.phone_in_talk,
      'color': IslaColors.palmGreen,
      'unlocked': false,
    },
    {
      'id': 'level_3',
      'title': 'Explorador Seguro',
      'subtitle': 'Detective de Internet',
      'icon': Icons.search,
      'color': IslaColors.sunsetPurple,
      'unlocked': false,
    },
    {
      'id': 'level_4',
      'title': 'Super Tareas',
      'subtitle': 'Calculadora y cámara',
      'icon': Icons.calculate,
      'color': IslaColors.sunsetCoral,
      'unlocked': false,
    },
    {
      'id': 'level_5',
      'title': 'Pequeño Artista',
      'subtitle': 'Dibuja y crea música',
      'icon': Icons.palette,
      'color': IslaColors.sunsetPink,
      'unlocked': false,
    },
  ];

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final profile = ref.watch(currentProfileProvider);
    final currentLevel = profile?.currentLevel ?? 1;

    return Scaffold(
      body: IslandBackground(
        child: SafeArea(
          child: Column(
            children: [
              _buildHeader(context),
              const SizedBox(height: 16),
              Expanded(
                child: ListView.builder(
                  padding: const EdgeInsets.symmetric(horizontal: 16),
                  itemCount: levels.length,
                  itemBuilder: (context, index) {
                    final level = levels[index];
                    final isUnlocked = index < currentLevel;
                    final isCurrent = index == currentLevel - 1;
                    
                    return _buildLevelCard(
                      context,
                      level,
                      isUnlocked,
                      isCurrent,
                      profile?.levelProgress[level['id']] ?? 0,
                    );
                  },
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildHeader(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(16),
      child: Row(
        children: [
          IconButton(
            onPressed: () => Navigator.pop(context),
            icon: const Icon(Icons.arrow_back),
            color: IslaColors.oceanBlue,
          ),
          const SizedBox(width: 8),
          Text(
            'Elige un Nivel',
            style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                  color: IslaColors.oceanBlue,
                  fontWeight: FontWeight.bold,
                ),
          ),
        ],
      ),
    );
  }

  Widget _buildLevelCard(
    BuildContext context,
    Map<String, dynamic> level,
    bool isUnlocked,
    bool isCurrent,
    int progress,
  ) {
    final color = level['color'] as Color;
    
    return Container(
      margin: const EdgeInsets.only(bottom: 16),
      child: Opacity(
        opacity: isUnlocked ? 1.0 : 0.5,
        child: Card(
          elevation: isCurrent ? 8 : 4,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(20),
            side: isCurrent
                ? BorderSide(color: color, width: 3)
                : BorderSide.none,
          ),
          child: InkWell(
            onTap: isUnlocked
                ? () => _navigateToLevel(context, level['id'] as String)
                : null,
            borderRadius: BorderRadius.circular(20),
            child: Padding(
              padding: const EdgeInsets.all(20),
              child: Row(
                children: [
                  Container(
                    width: 64,
                    height: 64,
                    decoration: BoxDecoration(
                      gradient: LinearGradient(
                        colors: [color, _darkenColor(color, 0.2)],
                        begin: Alignment.topLeft,
                        end: Alignment.bottomRight,
                      ),
                      borderRadius: BorderRadius.circular(16),
                    ),
                    child: Icon(
                      level['icon'] as IconData,
                      size: 36,
                      color: IslaColors.white,
                    ),
                  ),
                  const SizedBox(width: 16),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          level['title'] as String,
                          style: Theme.of(context).textTheme.titleLarge?.copyWith(
                                fontWeight: FontWeight.bold,
                                color: isUnlocked ? IslaColors.black : IslaColors.greyDark,
                              ),
                        ),
                        const SizedBox(height: 4),
                        Text(
                          level['subtitle'] as String,
                          style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                                color: IslaColors.greyDark,
                              ),
                        ),
                        if (isUnlocked) ...[
                          const SizedBox(height: 12),
                          IslandProgressBar(
                            progress: progress,
                            height: 12,
                            fillColor: color,
                            showPercentage: false,
                          ),
                        ],
                      ],
                    ),
                  ),
                  if (!isUnlocked)
                    const Icon(
                      Icons.lock,
                      color: IslaColors.grey,
                      size: 28,
                    )
                  else if (progress >= 100)
                    const Icon(
                      Icons.check_circle,
                      color: IslaColors.success,
                      size: 32,
                    )
                  else
                    Icon(
                      Icons.play_circle_fill,
                      color: color,
                      size: 32,
                    ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }

  void _navigateToLevel(BuildContext context, String levelId) {
    switch (levelId) {
      case 'level_1':
        Navigator.push(
          context,
          MaterialPageRoute(builder: (context) => const Level1Screen()),
        );
        break;
      case 'level_2':
        Navigator.push(
          context,
          MaterialPageRoute(builder: (context) => const Level2Screen()),
        );
        break;
      case 'level_3':
        Navigator.push(
          context,
          MaterialPageRoute(builder: (context) => const Level3Screen()),
        );
        break;
      case 'level_4':
        Navigator.push(
          context,
          MaterialPageRoute(builder: (context) => const Level4Screen()),
        );
        break;
      case 'level_5':
        Navigator.push(
          context,
          MaterialPageRoute(builder: (context) => const Level5Screen()),
        );
        break;
      default:
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Nivel no disponible')),
        );
    }
  }

  Color _darkenColor(Color color, double amount) {
    final hsl = HSLColor.fromColor(color);
    return hsl.withLightness((hsl.lightness - amount).clamp(0.0, 1.0)).toColor();
  }
}
