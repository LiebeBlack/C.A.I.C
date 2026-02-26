import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/theme/app_theme.dart';
import '../../../core/providers/app_providers.dart';
import '../../../core/models/badge.dart';
import '../../widgets/island_background.dart';
import '../../widgets/progress_widgets.dart';

/// Nivel 4: Super Tareas
/// Calculadora temática, Calendario cultural, Misión Cámara
class Level4Screen extends ConsumerStatefulWidget {
  const Level4Screen({super.key});

  @override
  ConsumerState<Level4Screen> createState() => _Level4ScreenState();
}

class _Level4ScreenState extends ConsumerState<Level4Screen> {
  int currentTab = 0;
  int totalProgress = 0;
  bool showCelebration = false;
  String celebrationMessage = '';

  final List<String> tabs = ['Calculadora', 'Calendario', 'Misión Cámara'];

  void _addProgress(int points) {
    setState(() {
      totalProgress += points;
    });
    ref.read(levelProgressProvider('level_4').notifier).setProgress(totalProgress);
    ref.read(currentProfileProvider.notifier).addProgress('level_4', points);
    
    if (totalProgress >= 100) {
      _completeLevel();
    }
  }

  void _completeLevel() {
    setState(() {
      showCelebration = true;
      celebrationMessage = '¡Rey del Mango!';
    });

    final badge = IslaBadges.getById('calculador_frutas');
    if (badge != null) {
      ref.read(currentProfileProvider.notifier).addBadge(badge.id);
    }

    ref.read(currentProfileProvider.notifier).unlockLevel(5);

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
                      progress: totalProgress.clamp(0, 100),
                      label: 'Progreso de Super Tareas',
                      fillColor: IslaColors.sunsetCoral,
                    ),
                  ),
                  const SizedBox(height: 16),
                  _buildTabBar(),
                  const SizedBox(height: 16),
                  Expanded(
                    child: IndexedStack(
                      index: currentTab,
                      children: [
                        CalculatorWidget(onProgress: _addProgress, onSuccess: _showSuccessFeedback),
                        CalendarWidget(onProgress: _addProgress, onSuccess: _showSuccessFeedback),
                        CameraMissionWidget(onProgress: _addProgress, onSuccess: _showSuccessFeedback),
                      ],
                    ),
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
            color: IslaColors.sunsetCoral,
          ),
          const SizedBox(width: 8),
          Expanded(
            child: Text(
              'Super Tareas',
              style: Theme.of(context).textTheme.titleLarge?.copyWith(
                    color: IslaColors.sunsetCoral,
                    fontWeight: FontWeight.bold,
                  ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildTabBar() {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 16),
      padding: const EdgeInsets.all(4),
      decoration: BoxDecoration(
        color: IslaColors.greyLight,
        borderRadius: BorderRadius.circular(16),
      ),
      child: Row(
        children: List.generate(tabs.length, (index) {
          final isSelected = currentTab == index;
          return Expanded(
            child: GestureDetector(
              onTap: () => setState(() => currentTab = index),
              child: Container(
                padding: const EdgeInsets.symmetric(vertical: 12),
                decoration: BoxDecoration(
                  color: isSelected ? IslaColors.sunsetCoral : Colors.transparent,
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Text(
                  tabs[index],
                  textAlign: TextAlign.center,
                  style: Theme.of(context).textTheme.titleSmall?.copyWith(
                        color: isSelected ? IslaColors.white : IslaColors.greyDark,
                        fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
                      ),
                ),
              ),
            ),
          );
        }),
      ),
    );
  }
}

/// Calculadora temática con perlas y pescados
class CalculatorWidget extends StatefulWidget {
  final Function(int) onProgress;
  final Function(String) onSuccess;

  const CalculatorWidget({super.key, required this.onProgress, required this.onSuccess});

  @override
  State<CalculatorWidget> createState() => _CalculatorWidgetState();
}

class _CalculatorWidgetState extends State<CalculatorWidget> {
  int num1 = 0;
  int num2 = 0;
  int? answer;
  int correctCount = 0;

  final List<Map<String, dynamic>> themes = [
    {'icon': Icons.circle, 'label': 'Perlas', 'color': IslaColors.sunYellow},
    {'icon': Icons.set_meal, 'label': 'Peces', 'color': IslaColors.oceanBlue},
    {'icon': Icons.local_florist, 'label': 'Flores', 'color': IslaColors.sunsetPink},
    {'icon': Icons.star, 'label': 'Estrellas', 'color': IslaColors.sunsetPurple},
  ];

  int currentTheme = 0;

  void _generateProblem() {
    setState(() {
      num1 = (correctCount % 5) + 1;
      num2 = ((correctCount + 2) % 4) + 1;
      answer = null;
    });
  }

  void _checkAnswer(int selected) {
    final correct = num1 + num2;
    if (selected == correct) {
      widget.onSuccess('¡Muy bien!');
      widget.onProgress(15);
      setState(() {
        correctCount++;
        if (correctCount % 3 == 0) {
          currentTheme = (currentTheme + 1) % themes.length;
        }
      });
      _generateProblem();
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Intenta de nuevo'),
          backgroundColor: IslaColors.warning,
          duration: Duration(seconds: 1),
        ),
      );
    }
  }

  @override
  void initState() {
    super.initState();
    _generateProblem();
  }

  @override
  Widget build(BuildContext context) {
    final theme = themes[currentTheme];

    return Padding(
      padding: const EdgeInsets.all(24),
      child: Column(
        children: [
          Container(
            padding: const EdgeInsets.all(20),
            decoration: BoxDecoration(
              color: (theme['color'] as Color).withOpacity(0.1),
              borderRadius: BorderRadius.circular(20),
            ),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Icon(theme['icon'] as IconData, color: theme['color'] as Color, size: 32),
                const SizedBox(width: 8),
                Text(
                  'Sumando ${theme['label']}',
                  style: Theme.of(context).textTheme.titleLarge?.copyWith(
                        color: theme['color'] as Color,
                        fontWeight: FontWeight.bold,
                      ),
                ),
              ],
            ),
          ),
          const SizedBox(height: 32),
          Text(
            '¿Cuántos hay en total?',
            style: Theme.of(context).textTheme.headlineSmall,
          ),
          const SizedBox(height: 24),
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              _buildNumberVisual(num1, theme['icon'] as IconData, theme['color'] as Color),
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 16),
                child: Text('+', style: Theme.of(context).textTheme.displayMedium),
              ),
              _buildNumberVisual(num2, theme['icon'] as IconData, theme['color'] as Color),
            ],
          ),
          const SizedBox(height: 32),
          Text('=', style: Theme.of(context).textTheme.displayLarge),
          const SizedBox(height: 24),
          Wrap(
            spacing: 16,
            runSpacing: 16,
            alignment: WrapAlignment.center,
            children: List.generate(8, (index) {
              final option = index + 2;
              return _buildOptionButton(option);
            }),
          ),
        ],
      ),
    );
  }

  Widget _buildNumberVisual(int count, IconData icon, Color color) {
    return Column(
      children: [
        Wrap(
          spacing: 4,
          runSpacing: 4,
          children: List.generate(count, (_) {
            return Icon(icon, color: color, size: 24);
          }),
        ),
        const SizedBox(height: 8),
        Text('$count', style: Theme.of(context).textTheme.headlineSmall),
      ],
    );
  }

  Widget _buildOptionButton(int number) {
    return Material(
      elevation: 4,
      borderRadius: BorderRadius.circular(16),
      child: InkWell(
        onTap: () => _checkAnswer(number),
        borderRadius: BorderRadius.circular(16),
        child: Container(
          width: 70,
          height: 70,
          decoration: BoxDecoration(
            color: IslaColors.oceanBlue,
            borderRadius: BorderRadius.circular(16),
          ),
          child: Center(
            child: Text(
              '$number',
              style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                    color: IslaColors.white,
                    fontWeight: FontWeight.bold,
                  ),
            ),
          ),
        ),
      ),
    );
  }
}

/// Calendario de eventos culturales de Margarita
class CalendarWidget extends StatelessWidget {
  final Function(int) onProgress;
  final Function(String) onSuccess;

  const CalendarWidget({super.key, required this.onProgress, required this.onSuccess});

  final List<Map<String, dynamic>> events = const [
    {'day': '15', 'month': 'Feb', 'title': 'Fiesta de la Virgen', 'icon': Icons.church},
    {'day': '25', 'month': 'Mar', 'title': 'Día del Pescador', 'icon': Icons.set_meal},
    {'day': '08', 'month': 'May', 'title': 'Festival del Cactus', 'icon': Icons.local_florist},
    {'day': '24', 'month': 'Jul', 'title': 'Día de Margarita', 'icon': Icons.celebration},
    {'day': '12', 'month': 'Oct', 'title': 'Fiesta del Mar', 'icon': Icons.waves},
    {'day': '25', 'month': 'Dic', 'title': 'Navidad Isleña', 'icon': Icons.festival},
  ];

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(16),
      child: Column(
        children: [
          Text(
            'Eventos de Nueva Esparta',
            style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                  color: IslaColors.sunsetCoral,
                  fontWeight: FontWeight.bold,
                ),
          ),
          const SizedBox(height: 8),
          Text(
            'Toca cada evento para aprender',
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(color: IslaColors.grey),
          ),
          const SizedBox(height: 16),
          Expanded(
            child: ListView.builder(
              itemCount: events.length,
              itemBuilder: (context, index) {
                final event = events[index];
                return _buildEventCard(context, event);
              },
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildEventCard(BuildContext context, Map<String, dynamic> event) {
    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      elevation: 4,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: InkWell(
        onTap: () {
          onSuccess('¡Evento descubierto!');
          onProgress(10);
          _showEventDetails(context, event);
        },
        borderRadius: BorderRadius.circular(16),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Row(
            children: [
              Container(
                width: 60,
                height: 60,
                decoration: BoxDecoration(
                  color: IslaColors.sunsetCoral.withOpacity(0.2),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Text(
                      event['day'] as String,
                      style: Theme.of(context).textTheme.titleLarge?.copyWith(
                            color: IslaColors.sunsetCoral,
                            fontWeight: FontWeight.bold,
                          ),
                    ),
                    Text(
                      event['month'] as String,
                      style: Theme.of(context).textTheme.bodySmall?.copyWith(
                            color: IslaColors.sunsetCoral,
                          ),
                    ),
                  ],
                ),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: Text(
                  event['title'] as String,
                  style: Theme.of(context).textTheme.titleMedium?.copyWith(
                        fontWeight: FontWeight.bold,
                      ),
                ),
              ),
              Icon(
                event['icon'] as IconData,
                color: IslaColors.sunsetCoral,
                size: 28,
              ),
            ],
          ),
        ),
      ),
    );
  }

  void _showEventDetails(BuildContext context, Map<String, dynamic> event) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text(event['title'] as String),
        content: Text('${event['day']} de ${event['month']}\n\nEste es un evento importante de la Isla de Margarita.'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Cerrar'),
          ),
        ],
      ),
    );
  }
}

/// Misión Cámara - Simulador de fotos del hogar
class CameraMissionWidget extends StatefulWidget {
  final Function(int) onProgress;
  final Function(String) onSuccess;

  const CameraMissionWidget({super.key, required this.onProgress, required this.onSuccess});

  @override
  State<CameraMissionWidget> createState() => _CameraMissionWidgetState();
}

class _CameraMissionWidgetState extends State<CameraMissionWidget> {
  final List<Map<String, dynamic>> missions = [
    {'target': 'Una planta', 'icon': Icons.local_florist, 'completed': false},
    {'target': 'Algo azul', 'icon': Icons.color_lens, 'completed': false},
    {'target': 'Una fruta', 'icon': Icons.apple, 'completed': false},
    {'target': 'Tu juguete favorito', 'icon': Icons.toys, 'completed': false},
  ];

  void _completeMission(int index) {
    setState(() {
      missions[index]['completed'] = true;
    });
    widget.onSuccess('¡Misión completada!');
    widget.onProgress(15);
  }

  @override
  Widget build(BuildContext context) {
    final completedCount = missions.where((m) => m['completed'] as bool).length;

    return Padding(
      padding: const EdgeInsets.all(24),
      child: Column(
        children: [
          Text(
            'Misión Cámara 📸',
            style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                  color: IslaColors.sunsetCoral,
                  fontWeight: FontWeight.bold,
                ),
          ),
          const SizedBox(height: 8),
          Text(
            'Encuentra y "fotografía" estos objetos',
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(color: IslaColors.grey),
          ),
          const SizedBox(height: 16),
          Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              color: IslaColors.sunYellow.withOpacity(0.2),
              borderRadius: BorderRadius.circular(16),
            ),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const Icon(Icons.camera_alt, color: IslaColors.sunOrange),
                const SizedBox(width: 8),
                Text(
                  '$completedCount / ${missions.length} misiones',
                  style: Theme.of(context).textTheme.titleMedium?.copyWith(
                        color: IslaColors.sunOrange,
                        fontWeight: FontWeight.bold,
                      ),
                ),
              ],
            ),
          ),
          const SizedBox(height: 24),
          Expanded(
            child: GridView.count(
              crossAxisCount: 2,
              mainAxisSpacing: 16,
              crossAxisSpacing: 16,
              children: List.generate(missions.length, (index) {
                final mission = missions[index];
                return _buildMissionCard(
                  mission['target'] as String,
                  mission['icon'] as IconData,
                  mission['completed'] as bool,
                  () => _completeMission(index),
                );
              }),
            ),
          ),
          const SizedBox(height: 16),
          Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              color: IslaColors.greyLight,
              borderRadius: BorderRadius.circular(12),
            ),
            child: Row(
              children: [
                const Icon(Icons.info, color: IslaColors.info),
                const SizedBox(width: 8),
                Expanded(
                  child: Text(
                    'En la versión final, usa la cámara real del dispositivo',
                    style: Theme.of(context).textTheme.bodySmall,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildMissionCard(String target, IconData icon, bool completed, VoidCallback onTap) {
    return Material(
      elevation: completed ? 2 : 6,
      borderRadius: BorderRadius.circular(20),
      child: InkWell(
        onTap: completed ? null : onTap,
        borderRadius: BorderRadius.circular(20),
        child: Container(
          decoration: BoxDecoration(
            color: completed ? IslaColors.palmLight.withOpacity(0.3) : IslaColors.white,
            borderRadius: BorderRadius.circular(20),
            border: Border.all(
              color: completed ? IslaColors.success : IslaColors.sunsetCoral,
              width: completed ? 2 : 3,
            ),
          ),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(
                completed ? Icons.check_circle : icon,
                size: 48,
                color: completed ? IslaColors.success : IslaColors.sunsetCoral,
              ),
              const SizedBox(height: 12),
              Text(
                target,
                textAlign: TextAlign.center,
                style: Theme.of(context).textTheme.titleSmall?.copyWith(
                      fontWeight: FontWeight.bold,
                      color: completed ? IslaColors.success : IslaColors.black,
                    ),
              ),
              if (completed) ...[
                const SizedBox(height: 4),
                const Icon(Icons.done, color: IslaColors.success, size: 20),
              ],
            ],
          ),
        ),
      ),
    );
  }
}
