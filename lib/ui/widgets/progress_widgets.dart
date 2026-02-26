import 'package:flutter/material.dart';
import '../../core/theme/app_theme.dart';

/// Barra de progreso visual animada para niños
/// Diseño colorido con feedback visual claro
class IslandProgressBar extends StatelessWidget {
  final int progress;
  final int maxProgress;
  final Color? fillColor;
  final double height;
  final bool showPercentage;
  final String? label;

  const IslandProgressBar({
    super.key,
    required this.progress,
    this.maxProgress = 100,
    this.fillColor,
    this.height = 20,
    this.showPercentage = true,
    this.label,
  });

  @override
  Widget build(BuildContext context) {
    final clampedProgress = progress.clamp(0, maxProgress);
    final percentage = (clampedProgress / maxProgress);
    final color = fillColor ?? IslaColors.oceanBlue;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        if (label != null) ...[
          Text(
            label!,
            style: Theme.of(context).textTheme.titleSmall?.copyWith(
                  color: IslaColors.oceanDark,
                  fontWeight: FontWeight.w600,
                ),
          ),
          const SizedBox(height: 8),
        ],
        Container(
          height: height,
          decoration: BoxDecoration(
            color: IslaColors.greyLight,
            borderRadius: BorderRadius.circular(height / 2),
            border: Border.all(
              color: IslaColors.grey,
              width: 2,
            ),
          ),
          child: ClipRRect(
            borderRadius: BorderRadius.circular(height / 2),
            child: Stack(
              children: [
                AnimatedContainer(
                  duration: const Duration(milliseconds: 500),
                  curve: Curves.easeInOut,
                  width: percentage * MediaQuery.of(context).size.width,
                  decoration: BoxDecoration(
                    gradient: LinearGradient(
                      colors: [
                        color,
                        _lightenColor(color, 0.2),
                      ],
                      begin: Alignment.centerLeft,
                      end: Alignment.centerRight,
                    ),
                  ),
                ),
                if (showPercentage)
                  Center(
                    child: Text(
                      '${(percentage * 100).toInt()}%',
                      style: TextStyle(
                        color: percentage > 0.5 ? IslaColors.white : IslaColors.greyDark,
                        fontWeight: FontWeight.bold,
                        fontSize: height * 0.5,
                      ),
                    ),
                  ),
              ],
            ),
          ),
        ),
      ],
    );
  }

  Color _lightenColor(Color color, double amount) {
    final hsl = HSLColor.fromColor(color);
    return hsl.withLightness((hsl.lightness + amount).clamp(0.0, 1.0)).toColor();
  }
}

/// Indicador de pasos/progreso con puntos
class StepIndicator extends StatelessWidget {
  final int currentStep;
  final int totalSteps;
  final Color? activeColor;
  final Color? inactiveColor;

  const StepIndicator({
    super.key,
    required this.currentStep,
    required this.totalSteps,
    this.activeColor,
    this.inactiveColor,
  });

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: List.generate(totalSteps, (index) {
        final isActive = index <= currentStep;
        return Container(
          width: 12,
          height: 12,
          margin: const EdgeInsets.symmetric(horizontal: 5),
          decoration: BoxDecoration(
            shape: BoxShape.circle,
            color: isActive
                ? (activeColor ?? IslaColors.palmGreen)
                : (inactiveColor ?? IslaColors.greyLight),
            border: Border.all(
              color: isActive
                  ? (activeColor ?? IslaColors.palmGreen)
                  : IslaColors.grey,
              width: 2,
            ),
          ),
          child: isActive
              ? const Icon(
                  Icons.check,
                  size: 10,
                  color: IslaColors.white,
                )
              : null,
        );
      }),
    );
  }
}

/// Widget de celebración con confeti (placeholder para integración con confetti package)
class CelebrationOverlay extends StatelessWidget {
  final bool isVisible;
  final String? message;
  final VoidCallback? onComplete;

  const CelebrationOverlay({
    super.key,
    required this.isVisible,
    this.message,
    this.onComplete,
  });

  @override
  Widget build(BuildContext context) {
    if (!isVisible) return const SizedBox.shrink();

    return Container(
      color: Colors.black.withOpacity(0.3),
      child: Center(
        child: TweenAnimationBuilder<double>(
          tween: Tween(begin: 0.0, end: 1.0),
          duration: const Duration(milliseconds: 500),
          builder: (context, value, child) {
            return Transform.scale(
              scale: value,
              child: Container(
                padding: const EdgeInsets.all(32),
                decoration: BoxDecoration(
                  color: IslaColors.white,
                  borderRadius: BorderRadius.circular(24),
                  boxShadow: [
                    BoxShadow(
                      color: IslaColors.sunYellow.withOpacity(0.5),
                      blurRadius: 24,
                      spreadRadius: 8,
                    ),
                  ],
                ),
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Icon(
                      Icons.star,
                      size: 64,
                      color: IslaColors.sunYellow,
                    ),
                    const SizedBox(height: 16),
                    Text(
                      message ?? '¡Excelente!',
                      style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                            color: IslaColors.oceanBlue,
                            fontWeight: FontWeight.bold,
                          ),
                    ),
                  ],
                ),
              ),
            );
          },
        ),
      ),
    );
  }
}
