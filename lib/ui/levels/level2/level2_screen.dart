import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/theme/app_theme.dart';
import '../../../core/providers/app_providers.dart';
import '../../../core/models/badge.dart';
import '../../widgets/island_background.dart';
import '../../widgets/progress_widgets.dart';
import '../../widgets/big_button.dart';

/// Nivel 2: Conectados
/// Simulador de Chat Seguro y Llamadas de Emergencia
class Level2Screen extends ConsumerStatefulWidget {
  const Level2Screen({super.key});

  @override
  ConsumerState<Level2Screen> createState() => _Level2ScreenState();
}

class _Level2ScreenState extends ConsumerState<Level2Screen> {
  int currentStep = 0;
  int totalProgress = 0;
  bool showCelebration = false;
  String celebrationMessage = '';

  final List<Map<String, dynamic>> steps = [
    {
      'title': 'Chat Seguro',
      'subtitle': 'Solo habla con personas que conoces',
      'type': 'safe_chat',
    },
    {
      'title': 'Llamada de Emergencia',
      'subtitle': 'Aprende cuándo llamar por ayuda',
      'type': 'emergency_call',
    },
    {
      'title': 'Tus Fotos',
      'subtitle': 'Comparte solo lo que es seguro',
      'type': 'photo_share',
    },
    {
      'title': 'Quiz de Seguridad',
      'subtitle': '¿Es seguro compartir esto?',
      'type': 'safety_quiz',
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

    ref.read(levelProgressProvider('level_2').notifier).setProgress(totalProgress);
    ref.read(currentProfileProvider.notifier).addProgress('level_2', points);
  }

  void _completeLevel() {
    setState(() {
      showCelebration = true;
      celebrationMessage = '¡Eres un Palometa del Mar!';
    });

    final badge = IslaBadges.getById('comunicador_seguro');
    if (badge != null) {
      ref.read(currentProfileProvider.notifier).addBadge(badge.id);
    }

    ref.read(currentProfileProvider.notifier).unlockLevel(3);

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
                      fillColor: IslaColors.palmGreen,
                    ),
                  ),
                  const SizedBox(height: 24),
                  StepIndicator(
                    currentStep: currentStep,
                    totalSteps: steps.length,
                    activeColor: IslaColors.palmGreen,
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
            color: IslaColors.palmGreen,
          ),
          const SizedBox(width: 8),
          Expanded(
            child: Text(
              'Conectados',
              style: Theme.of(context).textTheme.titleLarge?.copyWith(
                    color: IslaColors.palmGreen,
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
      case 'safe_chat':
        return _buildSafeChatStep(step);
      case 'emergency_call':
        return _buildEmergencyCallStep(step);
      case 'photo_share':
        return _buildPhotoShareStep(step);
      case 'safety_quiz':
        return _buildSafetyQuizStep(step);
      default:
        return const SizedBox.shrink();
    }
  }

  Widget _buildSafeChatStep(Map<String, dynamic> step) {
    final contacts = [
      {'name': 'Mamá', 'icon': Icons.face_3, 'safe': true, 'color': IslaColors.sunsetPink},
      {'name': 'Papá', 'icon': Icons.face, 'safe': true, 'color': IslaColors.oceanBlue},
      {'name': 'Amigo', 'icon': Icons.face_2, 'safe': true, 'color': IslaColors.palmGreen},
      {'name': 'Desconocido', 'icon': Icons.person_outline, 'safe': false, 'color': IslaColors.grey},
    ];

    return Padding(
      padding: const EdgeInsets.all(24),
      child: Column(
        children: [
          Icon(Icons.chat_bubble, size: 80, color: IslaColors.palmGreen.withOpacity(0.5)),
          const SizedBox(height: 24),
          Text(step['title'], style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                color: IslaColors.palmGreen, fontWeight: FontWeight.bold),
            textAlign: TextAlign.center),
          const SizedBox(height: 8),
          Text(step['subtitle'], style: Theme.of(context).textTheme.bodyLarge,
            textAlign: TextAlign.center),
          const SizedBox(height: 32),
          Expanded(
            child: GridView.count(
              crossAxisCount: 2,
              mainAxisSpacing: 16,
              crossAxisSpacing: 16,
              children: contacts.map((contact) {
                return _buildContactCard(
                  contact['name'] as String,
                  contact['icon'] as IconData,
                  contact['safe'] as bool,
                  contact['color'] as Color,
                );
              }).toList(),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildContactCard(String name, IconData icon, bool safe, Color color) {
    return Material(
      elevation: 4,
      borderRadius: BorderRadius.circular(20),
      child: InkWell(
        onTap: () {
          if (safe) {
            _showSuccessFeedback('¡Correcto! Conoces a $name');
            _completeStep(25);
          } else {
            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(
                content: Text('¡Cuidado! No conocemos a esta persona'),
                backgroundColor: IslaColors.warning,
                duration: Duration(seconds: 1),
              ),
            );
          }
        },
        borderRadius: BorderRadius.circular(20),
        child: Container(
          decoration: BoxDecoration(
            color: color.withOpacity(0.2),
            borderRadius: BorderRadius.circular(20),
            border: Border.all(color: color, width: 3),
          ),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(icon, size: 48, color: color),
              const SizedBox(height: 8),
              Text(name, style: Theme.of(context).textTheme.titleMedium?.copyWith(
                    color: color, fontWeight: FontWeight.bold)),
              if (!safe) const Icon(Icons.warning, color: IslaColors.warning, size: 20),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildEmergencyCallStep(Map<String, dynamic> step) {
    final emergencies = [
      {'icon': Icons.local_hospital, 'label': 'Doctor', 'number': '911', 'color': IslaColors.error},
      {'icon': Icons.local_police, 'label': 'Policía', 'number': '911', 'color': IslaColors.oceanBlue},
      {'icon': Icons.fire_truck, 'label': 'Bomberos', 'number': '911', 'color': IslaColors.sunOrange},
    ];

    return Padding(
      padding: const EdgeInsets.all(24),
      child: Column(
        children: [
          Icon(Icons.phone, size: 80, color: IslaColors.palmGreen.withOpacity(0.5)),
          const SizedBox(height: 24),
          Text(step['title'], style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                color: IslaColors.palmGreen, fontWeight: FontWeight.bold),
            textAlign: TextAlign.center),
          const SizedBox(height: 8),
          Text(step['subtitle'], style: Theme.of(context).textTheme.bodyLarge,
            textAlign: TextAlign.center),
          const SizedBox(height: 32),
          Expanded(
            child: ListView.separated(
              itemCount: emergencies.length,
              separatorBuilder: (_, __) => const SizedBox(height: 16),
              itemBuilder: (context, index) {
                final emergency = emergencies[index];
                return BigButton(
                  icon: emergency['icon'] as IconData,
                  label: emergency['label'] as String,
                  color: emergency['color'] as Color,
                  onPressed: () {
                    _showEmergencyDialog(emergency['label'] as String, emergency['number'] as String);
                  },
                );
              },
            ),
          ),
          const SizedBox(height: 24),
          Text('Solo en emergencias reales', style: Theme.of(context).textTheme.bodySmall?.copyWith(
                color: IslaColors.grey)),
        ],
      ),
    );
  }

  void _showEmergencyDialog(String service, String number) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text('Llamar a $service'),
        content: Text('Número: $number\n\nEn esta práctica, simulamos la llamada.'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Cancelar'),
          ),
          ElevatedButton(
            onPressed: () {
              Navigator.pop(context);
              _showSuccessFeedback('¡Bien! Sabes pedir ayuda');
              _completeStep(25);
            },
            child: const Text('Llamar (práctica)'),
          ),
        ],
      ),
    );
  }

  Widget _buildPhotoShareStep(Map<String, dynamic> step) {
    final photos = [
      {'icon': Icons.pets, 'label': 'Mi mascota', 'safe': true},
      {'icon': Icons.toys, 'label': 'Mi juguete', 'safe': true},
      {'icon': Icons.house, 'label': 'Mi casa', 'safe': false, 'reason': 'Muestra dónde vives'},
      {'icon': Icons.school, 'label': 'Mi escuela', 'safe': false, 'reason': 'Muestra dónde estudias'},
    ];

    return Padding(
      padding: const EdgeInsets.all(24),
      child: Column(
        children: [
          Icon(Icons.photo_library, size: 80, color: IslaColors.palmGreen.withOpacity(0.5)),
          const SizedBox(height: 24),
          Text(step['title'], style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                color: IslaColors.palmGreen, fontWeight: FontWeight.bold),
            textAlign: TextAlign.center),
          const SizedBox(height: 8),
          Text(step['subtitle'], style: Theme.of(context).textTheme.bodyLarge,
            textAlign: TextAlign.center),
          const SizedBox(height: 32),
          Expanded(
            child: ListView.builder(
              itemCount: photos.length,
              itemBuilder: (context, index) {
                final photo = photos[index];
                return _buildPhotoCard(
                  photo['icon'] as IconData,
                  photo['label'] as String,
                  photo['safe'] as bool,
                  photo['reason'] as String?,
                );
              },
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildPhotoCard(IconData icon, String label, bool safe, String? reason) {
    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      child: ListTile(
        leading: CircleAvatar(
          backgroundColor: safe ? IslaColors.palmLight : IslaColors.warning.withOpacity(0.3),
          child: Icon(icon, color: safe ? IslaColors.palmDark : IslaColors.warning),
        ),
        title: Text(label),
        subtitle: safe ? const Text('Seguro para compartir') : Text('Cuidado: $reason'),
        trailing: IconButton(
          icon: Icon(safe ? Icons.check_circle : Icons.warning,
              color: safe ? IslaColors.success : IslaColors.warning),
          onPressed: () {
            if (safe) {
              _showSuccessFeedback('¡Buena elección!');
              _completeStep(13);
            } else {
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(
                  content: Text('No compartas fotos que $reason'),
                  backgroundColor: IslaColors.warning,
                ),
              );
            }
          },
        ),
      ),
    );
  }

  Widget _buildSafetyQuizStep(Map<String, dynamic> step) {
    final questions = [
      {'question': '¿Compartir tu dirección con un amigo en el chat?', 'answer': false, 'explanation': 'Habla con tus padres primero'},
      {'question': '¿Enviar una foto de tu mascota?', 'answer': true, 'explanation': '¡Las mascotas son seguras!'},
      {'question': '¿Contar tu edad a desconocidos?', 'answer': false, 'explanation': 'Mantén tu información privada'},
    ];

    return Padding(
      padding: const EdgeInsets.all(24),
      child: Column(
        children: [
          Icon(Icons.quiz, size: 80, color: IslaColors.palmGreen.withOpacity(0.5)),
          const SizedBox(height: 24),
          Text(step['title'], style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                color: IslaColors.palmGreen, fontWeight: FontWeight.bold),
            textAlign: TextAlign.center),
          const SizedBox(height: 8),
          Text(step['subtitle'], style: Theme.of(context).textTheme.bodyLarge,
            textAlign: TextAlign.center),
          const SizedBox(height: 32),
          Expanded(
            child: ListView.builder(
              itemCount: questions.length,
              itemBuilder: (context, index) {
                final q = questions[index];
                return _buildQuizCard(q['question'] as String, q['answer'] as bool, q['explanation'] as String);
              },
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildQuizCard(String question, bool correctAnswer, String explanation) {
    return Card(
      margin: const EdgeInsets.only(bottom: 16),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(question, style: Theme.of(context).textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold)),
            const SizedBox(height: 12),
            Row(
              children: [
                Expanded(
                  child: ElevatedButton.icon(
                    onPressed: () => _checkAnswer(true, correctAnswer == true, explanation),
                    icon: const Icon(Icons.check),
                    label: const Text('Sí'),
                    style: ElevatedButton.styleFrom(backgroundColor: IslaColors.success),
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: ElevatedButton.icon(
                    onPressed: () => _checkAnswer(false, correctAnswer == false, explanation),
                    icon: const Icon(Icons.close),
                    label: const Text('No'),
                    style: ElevatedButton.styleFrom(backgroundColor: IslaColors.error),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  void _checkAnswer(bool selected, bool correct, String explanation) {
    if (selected == correct) {
      _showSuccessFeedback('¡Correcto!');
      _completeStep(8);
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(explanation), backgroundColor: IslaColors.info),
      );
    }
  }
}
