import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/theme/app_theme.dart';
import '../../../core/providers/app_providers.dart';
import '../../../core/models/badge.dart';
import '../../widgets/island_background.dart';
import '../../widgets/progress_widgets.dart';

/// Nivel 3: Explorador Seguro
/// Mini-juego Detective: identificar sitios seguros vs inseguros
class Level3Screen extends ConsumerStatefulWidget {
  const Level3Screen({super.key});

  @override
  ConsumerState<Level3Screen> createState() => _Level3ScreenState();
}

class _Level3ScreenState extends ConsumerState<Level3Screen> {
  int currentStep = 0;
  int totalProgress = 0;
  int score = 0;
  bool showCelebration = false;
  String celebrationMessage = '';

  final List<Map<String, dynamic>> rounds = [
    {
      'scenario': 'Quieres ver videos de animales',
      'websites': [
        {'name': 'VideosKids.com', 'icon': Icons.video_library, 'safe': true, 'shield': 'green'},
        {'name': 'FreeMovies.xyz', 'icon': Icons.warning, 'safe': false, 'shield': 'red'},
        {'name': 'AprendeJugando.edu', 'icon': Icons.school, 'safe': true, 'shield': 'green'},
      ],
    },
    {
      'scenario': 'Buscar información para tu tarea',
      'websites': [
        {'name': 'WikipediaKids', 'icon': Icons.menu_book, 'safe': true, 'shield': 'green'},
        {'name': 'DescargasRapidas.net', 'icon': Icons.download, 'safe': false, 'shield': 'red'},
        {'name': 'WikiRespuestas', 'icon': Icons.help, 'safe': true, 'shield': 'green'},
      ],
    },
    {
      'scenario': 'Jugar juegos en línea',
      'websites': [
        {'name': 'JuegosEducativos.gob', 'icon': Icons.games, 'safe': true, 'shield': 'green'},
        {'name': 'JuegosGratisPopups', 'icon': Icons.ads_click, 'safe': false, 'shield': 'red'},
        {'name': 'DiversiónSegura.app', 'icon': Icons.verified, 'safe': true, 'shield': 'green'},
      ],
    },
    {
      'scenario': 'Hablar con amigos',
      'websites': [
        {'name': 'ChatAmigos', 'icon': Icons.chat, 'safe': true, 'shield': 'green'},
        {'name': 'EncuentraAmigosX', 'icon': Icons.person_search, 'safe': false, 'shield': 'red'},
        {'name': 'ClubDeLectura', 'icon': Icons.book, 'safe': true, 'shield': 'green'},
      ],
    },
  ];

  void _selectSite(bool isSafe, String name) {
    if (isSafe) {
      score += 25;
      _showSuccessFeedback('¡Sitio seguro! 🛡️');
      setState(() {
        totalProgress += 25;
        if (currentStep < rounds.length - 1) {
          currentStep++;
        } else {
          _completeLevel();
        }
      });
      ref.read(levelProgressProvider('level_3').notifier).setProgress(totalProgress);
      ref.read(currentProfileProvider.notifier).addProgress('level_3', 25);
    } else {
      _showDangerFeedback(name);
    }
  }

  void _showDangerFeedback(String siteName) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Row(
          children: [
            const Icon(Icons.warning, color: IslaColors.error),
            const SizedBox(width: 8),
            Text('¡Cuidado!', style: TextStyle(color: IslaColors.error)),
          ],
        ),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            const Icon(Icons.local_fire_department, size: 64, color: IslaColors.error),
            const SizedBox(height: 16),
            Text('$siteName puede ser peligroso.'),
            const SizedBox(height: 8),
            const Text('Los sitios inseguros pueden tener virus o personas malintencionadas.'),
          ],
        ),
        actions: [
          ElevatedButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Entendido'),
          ),
        ],
      ),
    );
  }

  void _completeLevel() {
    setState(() {
      showCelebration = true;
      celebrationMessage = '¡Detective de la Isla!';
    });

    final badge = IslaBadges.getById('detective_seguro');
    if (badge != null) {
      ref.read(currentProfileProvider.notifier).addBadge(badge.id);
    }

    ref.read(currentProfileProvider.notifier).unlockLevel(4);

    Future.delayed(const Duration(seconds: 3), () {
      if (mounted) {
        setState(() => showCelebration = false);
        Navigator.pop(context);
      }
    });
  }

  void _showSuccessFeedback(String message) {
    setState(() {
      showCelebration = true;
      celebrationMessage = message;
    });
    Future.delayed(const Duration(seconds: 1), () {
      if (mounted) setState(() => showCelebration = false);
    });
  }

  @override
  Widget build(BuildContext context) {
    final currentRound = rounds[currentStep];

    return Scaffold(
      body: Stack(
        children: [
          IslandBackground(
            child: SafeArea(
              child: Column(
                children: [
                  _buildHeader(),
                  const SizedBox(height: 16),
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 24),
                    child: IslandProgressBar(
                      progress: ((currentStep / rounds.length) * 100).toInt(),
                      label: 'Progreso del Detective',
                      fillColor: IslaColors.sunsetPurple,
                    ),
                  ),
                  const SizedBox(height: 24),
                  StepIndicator(
                    currentStep: currentStep,
                    totalSteps: rounds.length,
                    activeColor: IslaColors.sunsetPurple,
                  ),
                  const SizedBox(height: 32),
                  Expanded(
                    child: _buildGameContent(currentRound),
                  ),
                ],
              ),
            ),
          ),
          CelebrationOverlay(
            isVisible: showCelebration,
            message: celebrationMessage,
          ),
        ],
      ),
    );
  }

  Widget _buildHeader() {
    return Padding(
      padding: const EdgeInsets.all(16),
      child: Row(
        children: [
          IconButton(
            onPressed: () => Navigator.pop(context),
            icon: const Icon(Icons.close),
            color: IslaColors.sunsetPurple,
          ),
          const SizedBox(width: 8),
          Expanded(
            child: Text(
              'Detective de Internet',
              style: Theme.of(context).textTheme.titleLarge?.copyWith(
                    color: IslaColors.sunsetPurple,
                    fontWeight: FontWeight.bold,
                  ),
            ),
          ),
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
            decoration: BoxDecoration(
              color: IslaColors.sunYellow.withOpacity(0.3),
              borderRadius: BorderRadius.circular(16),
            ),
            child: Text(
              '$score pts',
              style: Theme.of(context).textTheme.titleMedium?.copyWith(
                    color: IslaColors.sunOrange,
                    fontWeight: FontWeight.bold,
                  ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildGameContent(Map<String, dynamic> round) {
    final scenario = round['scenario'] as String;
    final websites = round['websites'] as List<dynamic>;

    return Padding(
      padding: const EdgeInsets.all(24),
      child: Column(
        children: [
          Container(
            padding: const EdgeInsets.all(20),
            decoration: BoxDecoration(
              color: IslaColors.sunsetPurple.withOpacity(0.1),
              borderRadius: BorderRadius.circular(20),
              border: Border.all(color: IslaColors.sunsetPurple, width: 2),
            ),
            child: Column(
              children: [
                const Icon(Icons.search, size: 48, color: IslaColors.sunsetPurple),
                const SizedBox(height: 12),
                Text(
                  'Escenario:',
                  style: Theme.of(context).textTheme.titleMedium?.copyWith(
                        color: IslaColors.sunsetPurple,
                      ),
                ),
                Text(
                  scenario,
                  style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                        color: IslaColors.black,
                        fontWeight: FontWeight.bold,
                      ),
                  textAlign: TextAlign.center,
                ),
              ],
            ),
          ),
          const SizedBox(height: 32),
          Text(
            '¿Cuál sitio es SEGURO?',
            style: Theme.of(context).textTheme.titleLarge?.copyWith(
                  color: IslaColors.oceanBlue,
                  fontWeight: FontWeight.bold,
                ),
          ),
          const SizedBox(height: 8),
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              _buildShieldIndicator('Verde = Seguro', IslaColors.success),
              const SizedBox(width: 16),
              _buildShieldIndicator('Rojo = Peligroso', IslaColors.error),
            ],
          ),
          const SizedBox(height: 24),
          Expanded(
            child: ListView.separated(
              itemCount: websites.length,
              separatorBuilder: (_, __) => const SizedBox(height: 16),
              itemBuilder: (context, index) {
                final site = websites[index];
                return _buildWebsiteCard(
                  site['name'] as String,
                  site['icon'] as IconData,
                  site['safe'] as bool,
                  site['shield'] as String,
                );
              },
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildShieldIndicator(String label, Color color) {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        Icon(Icons.shield, color: color, size: 20),
        const SizedBox(width: 4),
        Text(label, style: Theme.of(context).textTheme.bodySmall),
      ],
    );
  }

  Widget _buildWebsiteCard(String name, IconData icon, bool safe, String shieldColor) {
    final shield = shieldColor == 'green' ? IslaColors.success : IslaColors.error;
    final isOctopus = !safe;

    return Material(
      elevation: 6,
      borderRadius: BorderRadius.circular(20),
      child: InkWell(
        onTap: () => _selectSite(safe, name),
        borderRadius: BorderRadius.circular(20),
        child: Container(
          padding: const EdgeInsets.all(20),
          decoration: BoxDecoration(
            color: IslaColors.white,
            borderRadius: BorderRadius.circular(20),
            border: Border.all(
              color: shield.withOpacity(0.5),
              width: 3,
            ),
          ),
          child: Row(
            children: [
              Container(
                width: 60,
                height: 60,
                decoration: BoxDecoration(
                  color: safe ? IslaColors.palmLight.withOpacity(0.3) : IslaColors.error.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(16),
                ),
                child: Icon(
                  isOctopus ? Icons.pets : icon,
                  size: 32,
                  color: isOctopus ? IslaColors.error : IslaColors.oceanBlue,
                ),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      name,
                      style: Theme.of(context).textTheme.titleMedium?.copyWith(
                            fontWeight: FontWeight.bold,
                          ),
                    ),
                    const SizedBox(height: 4),
                    Row(
                      children: [
                        Icon(
                          Icons.shield,
                          size: 16,
                          color: shield,
                        ),
                        const SizedBox(width: 4),
                        Text(
                          safe ? 'Sitio verificado' : '¡Precaución!',
                          style: Theme.of(context).textTheme.bodySmall?.copyWith(
                                color: shield,
                              ),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
              Icon(
                isOctopus ? Icons.warning : Icons.verified,
                color: shield,
                size: 28,
              ),
            ],
          ),
        ),
      ),
    );
  }
}
