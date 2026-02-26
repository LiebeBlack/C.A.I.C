import 'package:flutter/material.dart';
import '../../core/theme/app_theme.dart';
import '../../core/models/badge.dart' as models;

/// Widget de insignia con animación de escala y brillo
class AnimatedBadgeWidget extends StatefulWidget {
  final models.Badge badge;
  final VoidCallback? onTap;
  final double size;
  final bool showDetails;

  const AnimatedBadgeWidget({
    super.key,
    required this.badge,
    this.onTap,
    this.size = 120,
    this.showDetails = true,
  });

  @override
  State<AnimatedBadgeWidget> createState() => _AnimatedBadgeWidgetState();
}

class _AnimatedBadgeWidgetState extends State<AnimatedBadgeWidget>
    with TickerProviderStateMixin {
  late AnimationController _scaleController;
  late AnimationController _glowController;
  late Animation<double> _scaleAnimation;
  late Animation<double> _glowAnimation;

  @override
  void initState() {
    super.initState();
    
    _scaleController = AnimationController(
      duration: const Duration(milliseconds: 600),
      vsync: this,
    );
    
    _glowController = AnimationController(
      duration: const Duration(milliseconds: 1500),
      vsync: this,
    )..repeat(reverse: true);
    
    _scaleAnimation = CurvedAnimation(
      parent: _scaleController,
      curve: Curves.elasticOut,
    );
    
    _glowAnimation = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(parent: _glowController, curve: Curves.easeInOut),
    );
    
    // Iniciar animación de entrada
    Future.delayed(const Duration(milliseconds: 100), () {
      _scaleController.forward();
    });
  }

  @override
  void dispose() {
    _scaleController.dispose();
    _glowController.dispose();
    super.dispose();
  }

  void _onTap() {
    _scaleController.reverse().then((_) => _scaleController.forward());
    widget.onTap?.call();
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: _onTap,
      child: AnimatedBuilder(
        animation: Listenable.merge([_scaleAnimation, _glowAnimation]),
        builder: (context, child) {
          return Transform.scale(
            scale: 0.8 + (_scaleAnimation.value * 0.2),
            child: Container(
              width: widget.size,
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  // Badge con efecto de brillo
                  Container(
                    width: widget.size * 0.7,
                    height: widget.size * 0.7,
                    decoration: BoxDecoration(
                      gradient: RadialGradient(
                        colors: [
                          widget.badge.color.withOpacity(0.3 + (_glowAnimation.value * 0.3)),
                          widget.badge.color.withOpacity(0.1),
                          Colors.transparent,
                        ],
                        stops: const [0.0, 0.5, 1.0],
                      ),
                      shape: BoxShape.circle,
                    ),
                    child: Center(
                      child: Container(
                        width: widget.size * 0.55,
                        height: widget.size * 0.55,
                        decoration: BoxDecoration(
                          gradient: LinearGradient(
                            colors: [
                              widget.badge.color,
                              _darkenColor(widget.badge.color, 0.2),
                            ],
                            begin: Alignment.topLeft,
                            end: Alignment.bottomRight,
                          ),
                          shape: BoxShape.circle,
                          boxShadow: [
                            BoxShadow(
                              color: widget.badge.color.withOpacity(0.4 + (_glowAnimation.value * 0.4)),
                              blurRadius: 15 + (_glowAnimation.value * 10),
                              spreadRadius: 2 + (_glowAnimation.value * 3),
                            ),
                          ],
                        ),
                        child: Icon(
                          _getBadgeIcon(widget.badge.id),
                          size: widget.size * 0.3,
                          color: IslaColors.white,
                        ),
                      ),
                    ),
                  ),
                  if (widget.showDetails) ...[
                    const SizedBox(height: 12),
                    Text(
                      widget.badge.name,
                      textAlign: TextAlign.center,
                      style: Theme.of(context).textTheme.titleSmall?.copyWith(
                            fontWeight: FontWeight.bold,
                            color: widget.badge.color,
                          ),
                    ),
                    const SizedBox(height: 4),
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                      decoration: BoxDecoration(
                        color: widget.badge.color.withOpacity(0.1),
                        borderRadius: BorderRadius.circular(8),
                      ),
                      child: Text(
                        '${widget.badge.requiredPoints} pts',
                        style: Theme.of(context).textTheme.bodySmall?.copyWith(
                              color: widget.badge.color,
                              fontWeight: FontWeight.w600,
                            ),
                      ),
                    ),
                  ],
                ],
              ),
            ),
          );
        },
      ),
    );
  }

  IconData _getBadgeIcon(String badgeId) {
    switch (badgeId) {
      case 'primer_encuentro':
        return Icons.diamond;
      case 'maestro_botones':
        return Icons.touch_app;
      case 'comunicador_seguro':
        return Icons.message;
      case 'llamada_experta':
        return Icons.phone;
      case 'detective_seguro':
        return Icons.search;
      case 'escudo_digital':
        return Icons.shield;
      case 'calculador_frutas':
        return Icons.calculate;
      case 'fotografo_misiones':
        return Icons.camera_alt;
      case 'artista_isla':
        return Icons.palette;
      case 'musician_tropical':
        return Icons.music_note;
      case 'super_estrella':
        return Icons.star;
      case 'explorador_diario':
        return Icons.wb_sunny;
      default:
        return Icons.emoji_events;
    }
  }

  Color _darkenColor(Color color, double amount) {
    final hsl = HSLColor.fromColor(color);
    return hsl.withLightness((hsl.lightness - amount).clamp(0.0, 1.0)).toColor();
  }
}

/// Diálogo de recompensa al ganar una insignia
class BadgeRewardDialog extends StatelessWidget {
  final models.Badge badge;
  final VoidCallback? onContinue;

  const BadgeRewardDialog({
    super.key,
    required this.badge,
    this.onContinue,
  });

  @override
  Widget build(BuildContext context) {
    return Dialog(
      backgroundColor: Colors.transparent,
      child: Container(
        padding: const EdgeInsets.all(32),
        decoration: BoxDecoration(
          gradient: LinearGradient(
            colors: [
              IslaColors.sandLight,
              IslaColors.white,
            ],
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
          ),
          borderRadius: BorderRadius.circular(32),
          boxShadow: [
            BoxShadow(
              color: badge.color.withOpacity(0.3),
              blurRadius: 30,
              spreadRadius: 10,
            ),
          ],
        ),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            // Confeti animado simulado
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                _buildConfettiPiece(IslaColors.sunYellow, -20),
                _buildConfettiPiece(IslaColors.sunsetPink, 10),
                _buildConfettiPiece(IslaColors.palmGreen, -10),
                _buildConfettiPiece(IslaColors.oceanLight, 20),
              ],
            ),
            const SizedBox(height: 24),
            AnimatedBadgeWidget(
              badge: badge,
              size: 180,
              showDetails: false,
            ),
            const SizedBox(height: 24),
            Text(
              '¡Felicidades!',
              style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                    color: badge.color,
                    fontWeight: FontWeight.bold,
                  ),
            ),
            const SizedBox(height: 8),
            Text(
              'Has ganado:',
              style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                    color: IslaColors.greyDark,
                  ),
            ),
            const SizedBox(height: 8),
            Text(
              badge.name,
              textAlign: TextAlign.center,
              style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                    color: badge.color,
                    fontWeight: FontWeight.bold,
                  ),
            ),
            const SizedBox(height: 16),
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: badge.color.withOpacity(0.1),
                borderRadius: BorderRadius.circular(16),
              ),
              child: Text(
                badge.description,
                textAlign: TextAlign.center,
                style: Theme.of(context).textTheme.bodyMedium,
              ),
            ),
            const SizedBox(height: 32),
            ElevatedButton.icon(
              onPressed: onContinue ?? () => Navigator.pop(context),
              icon: const Icon(Icons.arrow_forward),
              label: const Text('Continuar'),
              style: ElevatedButton.styleFrom(
                backgroundColor: badge.color,
                foregroundColor: IslaColors.white,
                padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 16),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(16),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildConfettiPiece(Color color, double rotation) {
    return Transform.rotate(
      angle: rotation * 3.14159 / 180,
      child: Container(
        width: 12,
        height: 20,
        margin: const EdgeInsets.symmetric(horizontal: 4),
        decoration: BoxDecoration(
          color: color,
          borderRadius: BorderRadius.circular(2),
        ),
      ),
    );
  }
}

/// Pantalla de colección de insignias
class BadgesCollectionScreen extends StatelessWidget {
  final List<String> earnedBadgeIds;

  const BadgesCollectionScreen({
    super.key,
    required this.earnedBadgeIds,
  });

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: IslandBackground(
        child: SafeArea(
          child: Column(
            children: [
              _buildHeader(context),
              const SizedBox(height: 16),
              Expanded(
                child: _buildBadgesGrid(),
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
          Expanded(
            child: Text(
              'Colección de Insignias',
              style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                    color: IslaColors.oceanBlue,
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
              '${earnedBadgeIds.length}/${IslaBadges.allBadges.length}',
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

  Widget _buildBadgesGrid() {
    return GridView.builder(
      padding: const EdgeInsets.all(16),
      gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
        crossAxisCount: 2,
        childAspectRatio: 0.8,
        crossAxisSpacing: 16,
        mainAxisSpacing: 16,
      ),
      itemCount: IslaBadges.allBadges.length,
      itemBuilder: (context, index) {
        final badge = IslaBadges.allBadges[index];
        final isEarned = earnedBadgeIds.contains(badge.id);
        
        return _buildBadgeCard(badge, isEarned);
      },
    );
  }

  Widget _buildBadgeCard(models.Badge badge, bool isEarned) {
    return Card(
      elevation: isEarned ? 4 : 1,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(20),
        side: isEarned
            ? BorderSide(color: badge.color, width: 2)
            : BorderSide(color: IslaColors.greyLight, width: 1),
      ),
      child: Container(
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: isEarned ? null : IslaColors.greyLight.withOpacity(0.5),
          borderRadius: BorderRadius.circular(20),
        ),
        child: Opacity(
          opacity: isEarned ? 1.0 : 0.4,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(
                _getBadgeIcon(badge.id),
                size: 48,
                color: badge.color,
              ),
              const SizedBox(height: 12),
              Text(
                badge.name,
                textAlign: TextAlign.center,
                style: Theme.of(context).textTheme.titleSmall?.copyWith(
                      fontWeight: FontWeight.bold,
                      color: isEarned ? badge.color : IslaColors.grey,
                    ),
              ),
              const SizedBox(height: 4),
              Text(
                isEarned ? '¡Lograda!' : 'Bloqueada',
                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                      color: isEarned ? IslaColors.success : IslaColors.grey,
                    ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  IconData _getBadgeIcon(String badgeId) {
    switch (badgeId) {
      case 'primer_encuentro':
        return Icons.diamond;
      case 'maestro_botones':
        return Icons.touch_app;
      case 'comunicador_seguro':
        return Icons.message;
      case 'llamada_experta':
        return Icons.phone;
      case 'detective_seguro':
        return Icons.search;
      case 'escudo_digital':
        return Icons.shield;
      case 'calculador_frutas':
        return Icons.calculate;
      case 'fotografo_misiones':
        return Icons.camera_alt;
      case 'artista_isla':
        return Icons.palette;
      case 'musician_tropical':
        return Icons.music_note;
      case 'super_estrella':
        return Icons.star;
      case 'explorador_diario':
        return Icons.wb_sunny;
      default:
        return Icons.emoji_events;
    }
  }
}

// Re-export IslandBackground para usar en este archivo
export '../../ui/widgets/island_background.dart';
