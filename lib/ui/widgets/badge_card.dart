import 'package:flutter/material.dart' hide Badge;
import '../../core/models/badge.dart';
import '../../core/theme/app_theme.dart';

/// Tarjeta visual para mostrar una insignia (Badge).
/// Diseño colorido y atractivo para niños.
class BadgeCard extends StatelessWidget {
  const BadgeCard({
    super.key,
    required this.badge,
    this.isEarned = false,
    this.size = 80,
  });

  final Badge badge;
  final bool isEarned;
  final double size;

  @override
  Widget build(BuildContext context) {
    return AnimatedOpacity(
      opacity: isEarned ? 1.0 : 0.4,
      duration: const Duration(milliseconds: 300),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Container(
            width: size,
            height: size,
            decoration: BoxDecoration(
              gradient: LinearGradient(
                colors: [
                  badge.color,
                  badge.color.withOpacity(0.7),
                ],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
              ),
              shape: BoxShape.circle,
              boxShadow: isEarned
                  ? [
                      BoxShadow(
                        color: badge.color.withOpacity(0.4),
                        blurRadius: 12,
                        spreadRadius: 2,
                      ),
                    ]
                  : null,
              border: Border.all(
                color: isEarned ? IslaColors.sunYellow : IslaColors.grey,
                width: isEarned ? 3 : 1,
              ),
            ),
            child: Icon(
              badge.icon,
              size: size * 0.45,
              color: IslaColors.white,
            ),
          ),
          const SizedBox(height: 8),
          SizedBox(
            width: size + 20,
            child: Text(
              badge.name,
              style: Theme.of(context).textTheme.bodySmall?.copyWith(
                    fontWeight: FontWeight.bold,
                    color: isEarned ? IslaColors.oceanDark : IslaColors.grey,
                  ),
              textAlign: TextAlign.center,
              maxLines: 2,
              overflow: TextOverflow.ellipsis,
            ),
          ),
        ],
      ),
    );
  }
}
