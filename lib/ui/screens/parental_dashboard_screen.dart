import 'dart:math';

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../core/models/badge.dart';
import '../../core/models/child_profile.dart';
import '../../core/models/parental_settings.dart';
import '../../core/providers/app_providers.dart';
import '../../core/theme/app_theme.dart';
import '../widgets/badge_card.dart';
import '../widgets/island_background.dart';

/// Dashboard de Control Parental con bloqueo matemático
/// Protege el acceso a configuraciones mediante operaciones simples
class ParentalDashboardScreen extends ConsumerStatefulWidget {
  const ParentalDashboardScreen({super.key});

  @override
  ConsumerState<ParentalDashboardScreen> createState() =>
      _ParentalDashboardScreenState();
}

class _ParentalDashboardScreenState
    extends ConsumerState<ParentalDashboardScreen> {
  bool _isAuthenticated = false;
  int _num1 = 0;
  int _num2 = 0;
  String _operation = '';
  final _answerController = TextEditingController();

  @override
  void initState() {
    super.initState();
    _generateMathProblem();
  }

  void _generateMathProblem() {
    final random = Random();
    _num1 = random.nextInt(10) + 1;
    _num2 = random.nextInt(10) + 1;

    final operations = ['+', '-'];
    _operation = operations[random.nextInt(operations.length)];

    if (_operation == '-' && _num1 < _num2) {
      final temp = _num1;
      _num1 = _num2;
      _num2 = temp;
    }
  }

  int _getCorrectAnswer() {
    switch (_operation) {
      case '+':
        return _num1 + _num2;
      case '-':
        return _num1 - _num2;
      default:
        return 0;
    }
  }

  void _checkAnswer() {
    final answer = int.tryParse(_answerController.text);
    if (answer == _getCorrectAnswer()) {
      setState(() => _isAuthenticated = true);
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Respuesta incorrecta. Intenta de nuevo.'),
          backgroundColor: IslaColors.error,
        ),
      );
      _answerController.clear();
      _generateMathProblem();
    }
  }

  @override
  void dispose() {
    _answerController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final profile = ref.watch(currentProfileProvider);
    final settings = ref.watch(parentalSettingsProvider);

    return Scaffold(
      body: IslandBackground(
        child: SafeArea(
          child: !_isAuthenticated
              ? _buildAuthScreen()
              : _buildDashboard(profile, settings),
        ),
      ),
    );
  }

  Widget _buildAuthScreen() {
    return Padding(
      padding: const EdgeInsets.all(24),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Icon(
            Icons.lock,
            size: 80,
            color: IslaColors.oceanBlue,
          ),
          const SizedBox(height: 24),
          Text(
            'Acceso para Padres',
            style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                  color: IslaColors.oceanBlue,
                  fontWeight: FontWeight.bold,
                ),
          ),
          const SizedBox(height: 16),
          Text(
            'Resuelve esta operación para continuar:',
            style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                  color: IslaColors.greyDark,
                ),
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: 32),
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 24),
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
                Text(
                  '$_num1 $_operation $_num2 = ?',
                  style: Theme.of(context).textTheme.displaySmall?.copyWith(
                        color: IslaColors.oceanBlue,
                        fontWeight: FontWeight.bold,
                      ),
                ),
                const SizedBox(height: 24),
                TextField(
                  controller: _answerController,
                  keyboardType: TextInputType.number,
                  textAlign: TextAlign.center,
                  style: Theme.of(context).textTheme.headlineMedium,
                  decoration: InputDecoration(
                    hintText: 'Tu respuesta',
                    filled: true,
                    fillColor: IslaColors.sandLight,
                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(16),
                      borderSide: BorderSide.none,
                    ),
                    contentPadding: const EdgeInsets.symmetric(vertical: 16),
                  ),
                ),
                const SizedBox(height: 24),
                SizedBox(
                  width: double.infinity,
                  child: ElevatedButton(
                    onPressed: _checkAnswer,
                    child: const Padding(
                      padding: EdgeInsets.symmetric(vertical: 16),
                      child: Text('Verificar'),
                    ),
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(height: 16),
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Volver'),
          ),
        ],
      ),
    );
  }

  Widget _buildDashboard(ChildProfile? profile, ParentalSettings settings) {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildDashboardHeader(),
          const SizedBox(height: 24),
          _buildChildStats(profile),
          const SizedBox(height: 24),
          _buildTimeLimitCard(settings),
          const SizedBox(height: 16),
          _buildSoundSettingsCard(settings),
          const SizedBox(height: 16),
          _buildBadgesCard(profile),
          const SizedBox(height: 32),
          SizedBox(
            width: double.infinity,
            child: OutlinedButton.icon(
              onPressed: () => Navigator.pop(context),
              icon: const Icon(Icons.logout),
              label: const Text('Salir del Dashboard'),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildDashboardHeader() {
    return Row(
      children: [
        Container(
          padding: const EdgeInsets.all(12),
          decoration: BoxDecoration(
            color: IslaColors.oceanBlue.withOpacity(0.1),
            borderRadius: BorderRadius.circular(12),
          ),
          child: const Icon(
            Icons.admin_panel_settings,
            color: IslaColors.oceanBlue,
            size: 32,
          ),
        ),
        const SizedBox(width: 16),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                'Panel de Control',
                style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                      color: IslaColors.oceanBlue,
                      fontWeight: FontWeight.bold,
                    ),
              ),
              Text(
                'Configuración parental',
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                      color: IslaColors.greyDark,
                    ),
              ),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildChildStats(ChildProfile? profile) {
    if (profile == null) {
      return const IslandCard(
        child: Text('No hay perfil activo'),
      );
    }

    return IslandCard(
      color: IslaColors.oceanBlue.withOpacity(0.05),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'Progreso de ${profile.name}',
            style: Theme.of(context).textTheme.titleLarge?.copyWith(
                  color: IslaColors.oceanBlue,
                  fontWeight: FontWeight.bold,
                ),
          ),
          const SizedBox(height: 16),
          _buildStatRow(
            Icons.person,
            'Edad',
            '${profile.age} años',
          ),
          const Divider(height: 24),
          _buildStatRow(
            Icons.timer,
            'Tiempo de uso total',
            '${profile.totalPlayTimeMinutes} min',
          ),
          const Divider(height: 24),
          _buildStatRow(
            Icons.emoji_events,
            'Insignias ganadas',
            '${profile.earnedBadges.length}',
          ),
          const Divider(height: 24),
          _buildStatRow(
            Icons.trending_up,
            'Nivel actual',
            '${profile.currentLevel}',
          ),
        ],
      ),
    );
  }

  Widget _buildStatRow(IconData icon, String label, String value) {
    return Row(
      children: [
        Icon(icon, color: IslaColors.oceanLight, size: 24),
        const SizedBox(width: 12),
        Text(
          label,
          style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                color: IslaColors.greyDark,
              ),
        ),
        const Spacer(),
        Text(
          value,
          style: Theme.of(context).textTheme.titleMedium?.copyWith(
                color: IslaColors.oceanBlue,
                fontWeight: FontWeight.bold,
              ),
        ),
      ],
    );
  }

  Widget _buildTimeLimitCard(ParentalSettings settings) {
    return IslandCard(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              const Icon(
                Icons.timer,
                color: IslaColors.sunsetCoral,
              ),
              const SizedBox(width: 8),
              Text(
                'Límite de tiempo diario',
                style: Theme.of(context).textTheme.titleLarge?.copyWith(
                      fontWeight: FontWeight.bold,
                    ),
              ),
            ],
          ),
          const SizedBox(height: 16),
          Slider(
            value: settings.dailyTimeLimitMinutes.toDouble(),
            min: 15,
            max: 120,
            divisions: 7,
            label: '${settings.dailyTimeLimitMinutes} min',
            onChanged: (value) {
              ref
                  .read(parentalSettingsProvider.notifier)
                  .updateTimeLimit(value.toInt());
            },
          ),
          Center(
            child: Text(
              '${settings.dailyTimeLimitMinutes} minutos por día',
              style: Theme.of(context).textTheme.titleMedium,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildSoundSettingsCard(ParentalSettings settings) {
    return IslandCard(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'Configuración de Sonido',
            style: Theme.of(context).textTheme.titleLarge?.copyWith(
                  fontWeight: FontWeight.bold,
                ),
          ),
          const SizedBox(height: 16),
          SwitchListTile(
            title: const Text('Efectos de sonido'),
            subtitle: const Text('Sonidos de éxito y feedback'),
            value: settings.soundEnabled,
            onChanged: (_) =>
                ref.read(parentalSettingsProvider.notifier).toggleSound(),
            activeThumbColor: IslaColors.oceanBlue,
          ),
          SwitchListTile(
            title: const Text('Música de fondo'),
            subtitle: const Text('Melodías tranquilas de la isla'),
            value: settings.musicEnabled,
            onChanged: (_) =>
                ref.read(parentalSettingsProvider.notifier).toggleMusic(),
            activeThumbColor: IslaColors.oceanBlue,
          ),
        ],
      ),
    );
  }

  Widget _buildBadgesCard(ChildProfile? profile) {
    final earnedIds = profile?.earnedBadges ?? [];

    return IslandCard(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'Insignias',
            style: Theme.of(context).textTheme.titleLarge?.copyWith(
                  fontWeight: FontWeight.bold,
                ),
          ),
          const SizedBox(height: 16),
          Wrap(
            spacing: 12,
            runSpacing: 16,
            children: IslaBadges.allBadges.map((badge) {
              return BadgeCard(
                badge: badge,
                isEarned: earnedIds.contains(badge.id),
                size: 56,
              );
            }).toList(),
          ),
        ],
      ),
    );
  }
}
