import 'package:flutter/material.dart';
import '../../core/theme/app_theme.dart';

/// Fondo personalizado con elementos visuales de la Isla de Margarita
/// Incluye gradientes de atardecer y elementos decorativos sutiles
class IslandBackground extends StatelessWidget {
  const IslandBackground({
    super.key,
    required this.child,
    this.showDecorations = true,
  });
  final Widget child;
  final bool showDecorations;

  @override
  Widget build(BuildContext context) {
    return DecoratedBox(
      decoration: const BoxDecoration(
        image: DecorationImage(
          image:
              AssetImage('assets/images/backgrounds/bg_beach_tropical_day.jpg'),
          fit: BoxFit.cover,
        ),
      ),
      child: Stack(
        children: [
          if (showDecorations) ...[
            Positioned(
              top: -50,
              right: -30,
              child: _buildSun(),
            ),
            Positioned(
              bottom: 0,
              left: 0,
              right: 0,
              child: _buildWaves(),
            ),
            Positioned(
              bottom: 20,
              left: 20,
              child: _buildPalmTree(),
            ),
          ],
          child,
        ],
      ),
    );
  }

  Widget _buildSun() {
    return Container(
      width: 150,
      height: 150,
      decoration: BoxDecoration(
        gradient: RadialGradient(
          colors: [
            IslaColors.sunYellow.withOpacity(0.6),
            IslaColors.sunYellow.withOpacity(0.2),
            Colors.transparent,
          ],
          stops: const [0.3, 0.6, 1.0],
        ),
        shape: BoxShape.circle,
      ),
    );
  }

  Widget _buildWaves() {
    return Container(
      height: 100,
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: [
            IslaColors.oceanLight.withOpacity(0.3),
            IslaColors.oceanBlue.withOpacity(0.5),
            IslaColors.oceanDark.withOpacity(0.6),
          ],
          begin: Alignment.topCenter,
          end: Alignment.bottomCenter,
        ),
        borderRadius: const BorderRadius.only(
          topLeft: Radius.circular(40),
          topRight: Radius.circular(40),
        ),
      ),
      child: CustomPaint(
        size: const Size(double.infinity, 100),
        painter: WavesPainter(),
      ),
    );
  }

  Widget _buildPalmTree() {
    return Icon(
      Icons.park,
      size: 80,
      color: IslaColors.palmGreen.withOpacity(0.3),
    );
  }
}

/// Pintor personalizado para dibujar olas estilizadas
class WavesPainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..color = IslaColors.white.withOpacity(0.3)
      ..style = PaintingStyle.stroke
      ..strokeWidth = 3
      ..strokeCap = StrokeCap.round;

    final path = Path();

    for (int i = 0; i < 3; i++) {
      final y = 20.0 + (i * 25);
      path.moveTo(0, y);

      for (double x = 0; x <= size.width; x += 20) {
        path.lineTo(
          x,
          y + (i.isEven ? 5 : -5) * (x % 40 == 0 ? 1 : -1),
        );
      }
    }

    canvas.drawPath(path, paint);
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}

/// Widget de tarjeta con estilo isleño
class IslandCard extends StatelessWidget {
  const IslandCard({
    super.key,
    required this.child,
    this.color,
    this.padding,
    this.onTap,
  });
  final Widget child;
  final Color? color;
  final EdgeInsets? padding;
  final VoidCallback? onTap;

  @override
  Widget build(BuildContext context) {
    final cardColor = color ?? IslaColors.white;

    Widget cardContent = Container(
      padding: padding ?? const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: cardColor,
        borderRadius: BorderRadius.circular(20),
        boxShadow: [
          BoxShadow(
            color: IslaColors.oceanBlue.withOpacity(0.1),
            blurRadius: 12,
            offset: const Offset(0, 6),
          ),
        ],
        border: Border.all(
          color: IslaColors.sand.withOpacity(0.5),
          width: 2,
        ),
      ),
      child: child,
    );

    if (onTap != null) {
      cardContent = InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(20),
        child: cardContent,
      );
    }

    return cardContent;
  }
}
