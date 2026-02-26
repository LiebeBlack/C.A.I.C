import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:lottie/lottie.dart';

import '../../core/models/badge.dart';
import '../../core/models/child_profile.dart';
import '../../core/providers/app_providers.dart';
import '../../core/theme/app_theme.dart';
import '../widgets/badge_card.dart';
import '../widgets/big_button.dart';
import '../widgets/island_background.dart';

class HomeScreen extends ConsumerWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final profile = ref.watch(currentProfileProvider);

    return Scaffold(
      body: IslandBackground(
        child: SafeArea(
          child: Stack(
            children: [
              // Lottie Animation: Meditating Giraffe (Left side)
              Positioned(
                left: -20,
                bottom: 150,
                height: 200,
                child: Lottie.asset(
                  'assets/animations/characters/Meditating Giraffe.json',
                ),
              ),
              // Lottie Animation: Cat Movement (Right side, slightly floating)
              Positioned(
                right: 10,
                bottom: 250,
                height: 150,
                child: Lottie.asset(
                  'assets/animations/characters/Cat Movement.json',
                ),
              ),
              Column(
                children: [
                  _buildHeader(context, profile),
                  const SizedBox(height: 24),
                  _buildWelcomeCard(context, profile),
                  const SizedBox(height: 32),
                  _buildMainActions(context, ref),
                  const Spacer(),
                  _buildParentalButton(context),
                  const SizedBox(height: 16),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildHeader(BuildContext context, ChildProfile? profile) {
    return Padding(
      padding: const EdgeInsets.all(16),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Row(
            children: [
              const CircleAvatar(
                radius: 28,
                backgroundColor: IslaColors.sunYellow,
                child: Icon(
                  Icons.person,
                  size: 32,
                  color: IslaColors.oceanBlue,
                ),
              ),
              const SizedBox(width: 12),
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    '¡Hola!',
                    style: Theme.of(context).textTheme.titleMedium?.copyWith(
                          color: IslaColors.oceanDark,
                        ),
                  ),
                  Text(
                    profile?.name ?? 'Explorador',
                    style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                          color: IslaColors.oceanBlue,
                          fontWeight: FontWeight.bold,
                        ),
                  ),
                ],
              ),
            ],
          ),
          _buildBadgeCounter(context, profile),
        ],
      ),
    );
  }

  Widget _buildBadgeCounter(BuildContext context, ChildProfile? profile) {
    final badgeCount = profile?.earnedBadges.length ?? 0;
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
      decoration: BoxDecoration(
        color: IslaColors.sunYellow.withOpacity(0.3),
        borderRadius: BorderRadius.circular(20),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          const Icon(
            Icons.star,
            color: IslaColors.sunOrange,
            size: 24,
          ),
          const SizedBox(width: 4),
          Text(
            '$badgeCount',
            style: Theme.of(context).textTheme.titleLarge?.copyWith(
                  color: IslaColors.sunOrange,
                  fontWeight: FontWeight.bold,
                ),
          ),
        ],
      ),
    );
  }

  Widget _buildWelcomeCard(BuildContext context, ChildProfile? profile) {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 16),
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        gradient: const LinearGradient(
          colors: [IslaColors.oceanBlue, IslaColors.oceanLight],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
        borderRadius: BorderRadius.circular(24),
        boxShadow: [
          BoxShadow(
            color: IslaColors.oceanBlue.withOpacity(0.3),
            blurRadius: 12,
            offset: const Offset(0, 6),
          ),
        ],
      ),
      child: Column(
        children: [
          const Icon(
            Icons.wb_sunny,
            size: 48,
            color: IslaColors.sunYellow,
          ),
          const SizedBox(height: 12),
          Text(
            '¡Bienvenido a Isla Digital!',
            style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                  color: IslaColors.white,
                  fontWeight: FontWeight.bold,
                ),
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: 8),
          Text(
            'Descubre todos los secretos de tu teléfono como un verdadero explorador de Margarita',
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: IslaColors.white.withOpacity(0.9),
                ),
            textAlign: TextAlign.center,
          ),
        ],
      ),
    );
  }

  Widget _buildMainActions(BuildContext context, WidgetRef ref) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 24),
      child: Column(
        children: [
          BigButton(
            icon: Icons.play_circle_fill,
            label: '¡Jugar!',
            color: IslaColors.palmGreen,
            onPressed: () => Navigator.pushNamed(context, '/levels'),
          ),
          const SizedBox(height: 16),
          BigButton(
            icon: Icons.emoji_events,
            label: 'Mis Insignias',
            color: IslaColors.sunsetCoral,
            onPressed: () => _showBadgesDialog(context, ref),
          ),
          const SizedBox(height: 16),
          BigButton(
            icon: Icons.settings,
            label: 'Mi Perfil',
            color: IslaColors.sunsetPurple,
            onPressed: () => Navigator.pushNamed(context, '/profile'),
          ),
          const SizedBox(height: 16),
          BigButton(
            icon: Icons.photo_library,
            label: 'Galería Secreta (Assets)',
            color: IslaColors.oceanDark,
            onPressed: () => Navigator.pushNamed(context, '/showcase'),
          ),
        ],
      ),
    );
  }

  Widget _buildParentalButton(BuildContext context) {
    return TextButton.icon(
      onPressed: () => Navigator.pushNamed(context, '/parental'),
      icon: const Icon(Icons.lock, size: 20),
      label: const Text('Acceso Padres'),
      style: TextButton.styleFrom(
        foregroundColor: IslaColors.grey,
      ),
    );
  }

  void _showBadgesDialog(BuildContext context, WidgetRef ref) {
    final profile = ref.read(currentProfileProvider);
    final earnedIds = profile?.earnedBadges ?? [];

    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Mis Insignias'),
        content: SizedBox(
          width: double.maxFinite,
          child: IslaBadges.allBadges.isEmpty
              ? const Text('¡Completa actividades para ganar insignias!')
              : Wrap(
                  spacing: 12,
                  runSpacing: 16,
                  alignment: WrapAlignment.center,
                  children: IslaBadges.allBadges.map((badge) {
                    return BadgeCard(
                      badge: badge,
                      isEarned: earnedIds.contains(badge.id),
                      size: 60,
                    );
                  }).toList(),
                ),
        ),
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
