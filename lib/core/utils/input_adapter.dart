import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';

/// Adaptador de entrada para soportar mouse (Windows/Linux) y touch (Android)
/// Proporciona comportamiento consistente entre plataformas
class InputAdapter {
  /// Detectar si estamos en una plataforma de escritorio
  static bool get isDesktop {
    return const bool.fromEnvironment('dart.vm.product', defaultValue: false) == false
        ? false
        : true;
  }

  /// Configuración de scroll para mouse (con rueda) y touch
  static ScrollBehavior getScrollBehavior() {
    return MaterialScrollBehavior().copyWith(
      // Soportar drag con mouse en desktop
      dragDevices: {
        PointerDeviceKind.touch,
        PointerDeviceKind.mouse,
        PointerDeviceKind.trackpad,
        PointerDeviceKind.stylus,
      },
    );
  }

  /// Hover effect para elementos interactivos
  static Widget wrapWithHover({
    required Widget child,
    required VoidCallback onTap,
    Color hoverColor = Colors.transparent,
  }) {
    return MouseRegion(
      cursor: SystemMouseCursors.click,
      child: GestureDetector(
        onTap: onTap,
        child: Container(
          color: hoverColor,
          child: child,
        ),
      ),
    );
  }
}

/// Widget de botón adaptable que funciona bien con mouse y touch
class AdaptiveButton extends StatefulWidget {
  final Widget child;
  final VoidCallback onPressed;
  final Color? backgroundColor;
  final EdgeInsets padding;
  final BorderRadius? borderRadius;

  const AdaptiveButton({
    super.key,
    required this.child,
    required this.onPressed,
    this.backgroundColor,
    this.padding = const EdgeInsets.all(16),
    this.borderRadius,
  });

  @override
  State<AdaptiveButton> createState() => _AdaptiveButtonState();
}

class _AdaptiveButtonState extends State<AdaptiveButton> {
  bool _isHovered = false;
  bool _isPressed = false;

  @override
  Widget build(BuildContext context) {
    return MouseRegion(
      onEnter: (_) => setState(() => _isHovered = true),
      onExit: (_) => setState(() => _isHovered = false),
      cursor: SystemMouseCursors.click,
      child: GestureDetector(
        onTapDown: (_) => setState(() => _isPressed = true),
        onTapUp: (_) {
          setState(() => _isPressed = false);
          widget.onPressed();
        },
        onTapCancel: () => setState(() => _isPressed = false),
        child: AnimatedContainer(
          duration: const Duration(milliseconds: 100),
          padding: widget.padding,
          decoration: BoxDecoration(
            color: widget.backgroundColor ?? Theme.of(context).primaryColor,
            borderRadius: widget.borderRadius ?? BorderRadius.circular(12),
            boxShadow: _isHovered || _isPressed
                ? [
                    BoxShadow(
                      color: Colors.black.withOpacity(0.2),
                      blurRadius: 8,
                      offset: const Offset(0, 4),
                    ),
                  ]
                : [],
            transform: _isPressed
                ? Matrix4.identity()..scale(0.98)
                : Matrix4.identity(),
          ),
          child: widget.child,
        ),
      ),
    );
  }
}

/// Widget de área de juego que soporta tanto touch como mouse
class AdaptiveGameArea extends StatelessWidget {
  final Widget child;
  final Function(Offset position) onTap;
  final Function(Offset position)? onPanStart;
  final Function(Offset position)? onPanUpdate;
  final Function()? onPanEnd;

  const AdaptiveGameArea({
    super.key,
    required this.child,
    required this.onTap,
    this.onPanStart,
    this.onPanUpdate,
    this.onPanEnd,
  });

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      behavior: HitTestBehavior.opaque,
      onTapUp: (details) => onTap(details.localPosition),
      onPanStart: onPanStart != null
          ? (details) => onPanStart!(details.localPosition)
          : null,
      onPanUpdate: onPanUpdate != null
          ? (details) => onPanUpdate!(details.localPosition)
          : null,
      onPanEnd: onPanEnd != null ? (_) => onPanEnd!() : null,
      child: child,
    );
  }
}

/// Optimizaciones de memoria para dispositivos de gama baja
class MemoryOptimizations {
  /// Deshabilitar animaciones pesadas en dispositivos de bajos recursos
  static bool get shouldReduceAnimations {
    // En una app real, esto detectaría la memoria disponible
    return false;
  }

  /// Limitar el número de elementos en listas
  static int get maxListItems => 50;

  /// Calidad de imágenes reducida
  static double get imageQuality => 0.8;

  /// Cache size para imágenes
  static int get imageCacheSize => 50;
}

/// Extension para optimizar Image widgets
extension OptimizedImage on Image {
  static Widget optimizedNetwork(String url, {double? width, double? height}) {
    return Image.network(
      url,
      width: width,
      height: height,
      fit: BoxFit.cover,
      cacheWidth: width != null ? (width * 2).toInt() : null,
      cacheHeight: height != null ? (height * 2).toInt() : null,
      loadingBuilder: (context, child, loadingProgress) {
        if (loadingProgress == null) return child;
        return Container(
          width: width,
          height: height,
          color: Colors.grey[300],
          child: const Center(
            child: CircularProgressIndicator(),
          ),
        );
      },
      errorBuilder: (context, error, stackTrace) {
        return Container(
          width: width,
          height: height,
          color: Colors.grey[300],
          child: const Icon(Icons.broken_image),
        );
      },
    );
  }
}
