import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/theme/app_theme.dart';
import '../../../core/providers/app_providers.dart';
import '../../../core/models/badge.dart';
import '../../widgets/island_background.dart';
import '../../widgets/big_button.dart';
import '../../widgets/progress_widgets.dart';

/// Nivel 1: Mi Primer Encuentro
/// Simulación interactiva para familiarizarse con el dispositivo
class Level1Screen extends ConsumerStatefulWidget {
  const Level1Screen({super.key});

  @override
  ConsumerState<Level1Screen> createState() => _Level1ScreenState();
}

class _Level1ScreenState extends ConsumerState<Level1Screen> {
  int currentStep = 0;
  int totalProgress = 0;
  bool showCelebration = false;
  String celebrationMessage = '';

  final List<Map<String, dynamic>> steps = [
    {
      'title': '¡Enciende el teléfono!',
      'instruction': 'Toca el botón de encendido',
      'type': 'power_button',
    },
    {
      'title': 'Desbloquea la pantalla',
      'instruction': 'Desliza hacia arriba para desbloquear',
      'type': 'swipe_unlock',
    },
    {
      'title': 'Explora los botones',
      'instruction': 'Toca los botones de volumen',
      'type': 'volume_buttons',
    },
    {
      'title': 'Cuida tu teléfono',
      'instruction': 'Selecciona las cosas que protegen tu teléfono',
      'type': 'care_quiz',
    },
  ];

  void _completeStep(int points) {
    setState(() {
      totalProgress += points;
      if (currentStep < steps.length - 1) {
        currentStep++;
      } else {
        _completeLevel();
      }
    });

    // Update global progress
    ref.read(levelProgressProvider('level_1').notifier).setProgress(totalProgress);
    ref.read(currentProfileProvider.notifier).addProgress('level_1', points);
  }

  void _completeLevel() {
    setState(() {
      showCelebration = true;
      celebrationMessage = '¡Ganaste la Perla de Sabiduría!';
    });

    // Award badge
    final badge = IslaBadges.getById('primer_encuentro');
    if (badge != null) {
      ref.read(currentProfileProvider.notifier).addBadge(badge.id);
    }

    // Unlock next level
    ref.read(currentProfileProvider.notifier).unlockLevel(2);

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
      if (mounted) {
        setState(() => showCelebration = false);
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    final currentStepData = steps[currentStep];

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
                      progress: ((currentStep / steps.length) * 100).toInt(),
                      label: 'Progreso del Nivel',
                      fillColor: IslaColors.oceanBlue,
                    ),
                  ),
                  const SizedBox(height: 24),
                  StepIndicator(
                    currentStep: currentStep,
                    totalSteps: steps.length,
                    activeColor: IslaColors.oceanBlue,
                  ),
                  const SizedBox(height: 32),
                  Expanded(
                    child: _buildStepContent(currentStepData),
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
            color: IslaColors.oceanBlue,
          ),
          const SizedBox(width: 8),
          Expanded(
            child: Text(
              'Mi Primer Encuentro',
              style: Theme.of(context).textTheme.titleLarge?.copyWith(
                    color: IslaColors.oceanBlue,
                    fontWeight: FontWeight.bold,
                  ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildStepContent(Map<String, dynamic> step) {
    final type = step['type'] as String;

    switch (type) {
      case 'power_button':
        return _buildPowerButtonStep(step);
      case 'swipe_unlock':
        return _buildSwipeStep(step);
      case 'volume_buttons':
        return _buildVolumeStep(step);
      case 'care_quiz':
        return _buildCareQuizStep(step);
      default:
        return const SizedBox.shrink();
    }
  }

  Widget _buildPowerButtonStep(Map<String, dynamic> step) {
    return Padding(
      padding: const EdgeInsets.all(24),
      child: Column(
        children: [
          Icon(
            Icons.phone_android,
            size: 120,
            color: IslaColors.oceanBlue.withOpacity(0.3),
          ),
          const SizedBox(height: 32),
          Text(
            step['title'] as String,
            style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                  color: IslaColors.oceanBlue,
                  fontWeight: FontWeight.bold,
                ),
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: 16),
          Text(
            step['instruction'] as String,
            style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                  color: IslaColors.greyDark,
                ),
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: 48),
          AnimatedIconButton(
            icon: Icons.power_settings_new,
            color: IslaColors.success,
            size: 100,
            onPressed: () {
              _showSuccessFeedback('¡Bien hecho!');
              _completeStep(25);
            },
          ),
        ],
      ),
    );
  }

  Widget _buildSwipeStep(Map<String, dynamic> step) {
    return Padding(
      padding: const EdgeInsets.all(24),
      child: Column(
        children: [
          Container(
            width: 200,
            height: 320,
            decoration: BoxDecoration(
              color: IslaColors.oceanBlue.withOpacity(0.1),
              borderRadius: BorderRadius.circular(20),
              border: Border.all(
                color: IslaColors.oceanBlue,
                width: 3,
              ),
            ),
            child: const Center(
              child: Icon(
                Icons.lock,
                size: 48,
                color: IslaColors.oceanBlue,
              ),
            ),
          ),
          const SizedBox(height: 32),
          Text(
            step['title'] as String,
            style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                  color: IslaColors.oceanBlue,
                  fontWeight: FontWeight.bold,
                ),
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: 16),
          Text(
            step['instruction'] as String,
            style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                  color: IslaColors.greyDark,
                ),
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: 32),
          GestureDetector(
            onVerticalDragEnd: (details) {
              if (details.primaryVelocity != null && details.primaryVelocity! < 0) {
                _showSuccessFeedback('¡Excelente!');
                _completeStep(25);
              }
            },
            child: Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: IslaColors.sunYellow.withOpacity(0.3),
                borderRadius: BorderRadius.circular(16),
              ),
              child: Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  const Icon(
                    Icons.arrow_upward,
                    color: IslaColors.sunOrange,
                    size: 32,
                  ),
                  const SizedBox(width: 8),
                  Text(
                    'Desliza aquí',
                    style: Theme.of(context).textTheme.titleMedium?.copyWith(
                          color: IslaColors.sunOrange,
                          fontWeight: FontWeight.bold,
                        ),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildVolumeStep(Map<String, dynamic> step) {
    double volumeLevel = 0.5;
    
    return Padding(
      padding: const EdgeInsets.all(24),
      child: StatefulBuilder(
        builder: (context, setLocalState) {
          return Column(
            children: [
              Text(
                step['title'] as String,
                style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                      color: IslaColors.oceanBlue,
                      fontWeight: FontWeight.bold,
                    ),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 16),
              Text(
                step['instruction'] as String,
                style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                      color: IslaColors.greyDark,
                    ),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 48),
              Container(
                padding: const EdgeInsets.all(24),
                decoration: BoxDecoration(
                  color: IslaColors.white,
                  borderRadius: BorderRadius.circular(20),
                  boxShadow: [
                    BoxShadow(
                      color: IslaColors.oceanBlue.withOpacity(0.1),
                      blurRadius: 12,
                      offset: const Offset(0, 6),
                    ),
                  ],
                ),
                child: Column(
                  children: [
                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        IconButton(
                          icon: const Icon(Icons.remove_circle),
                          iconSize: 48,
                          color: IslaColors.oceanBlue,
                          onPressed: () {
                            setLocalState(() {
                              volumeLevel = (volumeLevel - 0.1).clamp(0.0, 1.0);
                            });
                          },
                        ),
                        const SizedBox(width: 24),
                        Icon(
                          volumeLevel > 0.5 ? Icons.volume_up : Icons.volume_down,
                          size: 48,
                          color: IslaColors.palmGreen,
                        ),
                        const SizedBox(width: 24),
                        IconButton(
                          icon: const Icon(Icons.add_circle),
                          iconSize: 48,
                          color: IslaColors.oceanBlue,
                          onPressed: () {
                            setLocalState(() {
                              volumeLevel = (volumeLevel + 0.1).clamp(0.0, 1.0);
                            });
                            if (volumeLevel >= 0.8) {
                              _showSuccessFeedback('¡Perfecto!');
                              Future.delayed(const Duration(milliseconds: 500), () {
                                _completeStep(25);
                              });
                            }
                          },
                        ),
                      ],
                    ),
                    const SizedBox(height: 16),
                    IslandProgressBar(
                      progress: (volumeLevel * 100).toInt(),
                      fillColor: IslaColors.palmGreen,
                      showPercentage: false,
                    ),
                  ],
                ),
              ),
            ],
          );
        },
      ),
    );
  }

  Widget _buildCareQuizStep(Map<String, dynamic> step) {
    final options = [
      {'icon': Icons.water_drop, 'label': 'Agua', 'correct': false},
      {'icon': Icons.sunny, 'label': 'Sol', 'correct': false},
      {'icon': Icons.shield, 'label': 'Funda', 'correct': true},
      {'icon': Icons.layers, 'label': 'Suelo', 'correct': false},
    ];

    return Padding(
      padding: const EdgeInsets.all(24),
      child: Column(
        children: [
          Text(
            step['title'] as String,
            style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                  color: IslaColors.oceanBlue,
                  fontWeight: FontWeight.bold,
                ),
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: 16),
          Text(
            step['instruction'] as String,
            style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                  color: IslaColors.greyDark,
                ),
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: 32),
          Expanded(
            child: GridView.count(
              crossAxisCount: 2,
              mainAxisSpacing: 16,
              crossAxisSpacing: 16,
              children: options.map((option) {
                return _buildOptionCard(
                  option['icon'] as IconData,
                  option['label'] as String,
                  option['correct'] as bool,
                );
              }).toList(),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildOptionCard(IconData icon, String label, bool correct) {
    return Material(
      elevation: 4,
      borderRadius: BorderRadius.circular(16),
      child: InkWell(
        onTap: () {
          if (correct) {
            _showSuccessFeedback('¡Correcto!');
            _completeStep(25);
          } else {
            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(
                content: Text('Intenta de nuevo'),
                backgroundColor: IslaColors.warning,
                duration: Duration(seconds: 1),
              ),
            );
          }
        },
        borderRadius: BorderRadius.circular(16),
        child: Container(
          decoration: BoxDecoration(
            color: IslaColors.white,
            borderRadius: BorderRadius.circular(16),
            border: Border.all(
              color: IslaColors.oceanLight,
              width: 2,
            ),
          ),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(
                icon,
                size: 48,
                color: IslaColors.oceanBlue,
              ),
              const SizedBox(height: 8),
              Text(
                label,
                style: Theme.of(context).textTheme.titleMedium,
              ),
            ],
          ),
        ),
      ),
    );
  }
}

// Widget auxiliar para botón animado con icono
class AnimatedIconButton extends StatefulWidget {
  final IconData icon;
  final Color color;
  final VoidCallback onPressed;
  final double size;

  const AnimatedIconButton({
    super.key,
    required this.icon,
    required this.color,
    required this.onPressed,
    this.size = 80,
  });

  @override
  State<AnimatedIconButton> createState() => _AnimatedIconButtonState();
}

class _AnimatedIconButtonState extends State<AnimatedIconButton>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _scaleAnimation;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      duration: const Duration(milliseconds: 150),
      vsync: this,
    );
    _scaleAnimation = Tween<double>(begin: 1.0, end: 0.9).animate(
      CurvedAnimation(parent: _controller, curve: Curves.easeInOut),
    );
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTapDown: (_) => _controller.forward(),
      onTapUp: (_) {
        _controller.reverse();
        widget.onPressed();
      },
      onTapCancel: () => _controller.reverse(),
      child: ScaleTransition(
        scale: _scaleAnimation,
        child: Container(
          width: widget.size,
          height: widget.size,
          decoration: BoxDecoration(
            color: widget.color,
            shape: BoxShape.circle,
            boxShadow: [
              BoxShadow(
                color: widget.color.withOpacity(0.4),
                blurRadius: 12,
                offset: const Offset(0, 6),
              ),
            ],
          ),
          child: Icon(
            widget.icon,
            size: widget.size * 0.5,
            color: IslaColors.white,
          ),
        ),
      ),
    );
  }
}
