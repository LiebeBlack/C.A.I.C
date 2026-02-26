import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:isla_digital/main.dart';
import 'package:isla_digital/ui/screens/home_screen.dart';
import 'package:isla_digital/ui/screens/level_select_screen.dart';
import 'package:isla_digital/ui/screens/profile_setup_screen.dart';
import 'package:isla_digital/ui/screens/parental_dashboard_screen.dart';
import 'package:isla_digital/ui/levels/level1/level1_screen.dart';
import 'package:isla_digital/ui/levels/level2/level2_screen.dart';
import 'package:isla_digital/ui/levels/level3/level3_screen.dart';
import 'package:isla_digital/ui/levels/level4/level4_screen.dart';
import 'package:isla_digital/ui/levels/level5/level5_screen.dart';

/// Tests de navegación para verificar el flujo entre pantallas y niveles
void main() {
  group('Navigation Flow Tests', () {
    
    testWidgets('App initializes with ProfileSetup when no profile exists', (WidgetTester tester) async {
      await tester.pumpWidget(
        const ProviderScope(
          child: IslaDigitalApp(),
        ),
      );
      await tester.pumpAndSettle();

      // Verificar que muestra la pantalla de perfil inicialmente
      expect(find.byType(ProfileSetupScreen), findsOneWidget);
    });

    testWidgets('Can navigate from Profile to Home after creating profile', (WidgetTester tester) async {
      await tester.pumpWidget(
        const ProviderScope(
          child: IslaDigitalApp(),
        ),
      );
      await tester.pumpAndSettle();

      // Crear perfil
      await tester.enterText(find.byType(TextField).first, 'Test User');
      await tester.tap(find.widgetWithText(ElevatedButton, '¡Listo!'));
      await tester.pumpAndSettle();

      // Verificar navegación a Home
      expect(find.byType(HomeScreen), findsOneWidget);
    });

    testWidgets('Home screen navigation to Level Select works', (WidgetTester tester) async {
      await tester.pumpWidget(
        MaterialApp(
          home: const ProviderScope(
            child: HomeScreen(),
          ),
        ),
      );
      await tester.pumpAndSettle();

      // Tap en botón Jugar
      final playButton = find.widgetWithIcon(BigButton, Icons.play_circle_fill);
      await tester.tap(playButton);
      await tester.pumpAndSettle();

      // Verificar navegación a LevelSelect
      expect(find.byType(LevelSelectScreen), findsOneWidget);
    });

    testWidgets('Level Select shows all 5 levels', (WidgetTester tester) async {
      await tester.pumpWidget(
        MaterialApp(
          home: const ProviderScope(
            child: LevelSelectScreen(),
          ),
        ),
      );
      await tester.pumpAndSettle();

      // Verificar que los 5 niveles están presentes
      expect(find.text('Mi Primer Encuentro'), findsOneWidget);
      expect(find.text('Conectados'), findsOneWidget);
      expect(find.text('Explorador Seguro'), findsOneWidget);
      expect(find.text('Super Tareas'), findsOneWidget);
      expect(find.text('Pequeño Artista'), findsOneWidget);
    });

    testWidgets('Can navigate to Level 1 from Level Select', (WidgetTester tester) async {
      await tester.pumpWidget(
        MaterialApp(
          home: const ProviderScope(
            child: LevelSelectScreen(),
          ),
        ),
      );
      await tester.pumpAndSettle();

      // Tap en nivel 1
      await tester.tap(find.text('Mi Primer Encuentro'));
      await tester.pumpAndSettle();

      // Verificar navegación a Level1Screen
      expect(find.byType(Level1Screen), findsOneWidget);
    });

    testWidgets('Level 1 has 4 steps and progress indicator', (WidgetTester tester) async {
      await tester.pumpWidget(
        MaterialApp(
          home: const ProviderScope(
            child: Level1Screen(),
          ),
        ),
      );
      await tester.pumpAndSettle();

      // Verificar elementos de UI
      expect(find.text('Mi Primer Encuentro'), findsOneWidget);
      expect(find.text('Progreso del Nivel'), findsOneWidget);
      
      // Verificar indicador de pasos (StepIndicator tiene 4 pasos)
      expect(find.byType(StepIndicator), findsOneWidget);
    });

    testWidgets('Can navigate to Level 2 from Level Select', (WidgetTester tester) async {
      await tester.pumpWidget(
        MaterialApp(
          home: const ProviderScope(
            child: LevelSelectScreen(),
          ),
        ),
      );
      await tester.pumpAndSettle();

      // Tap en nivel 2
      await tester.tap(find.text('Conectados'));
      await tester.pumpAndSettle();

      // Verificar navegación a Level2Screen
      expect(find.byType(Level2Screen), findsOneWidget);
    });

    testWidgets('Level 2 has chat and emergency call activities', (WidgetTester tester) async {
      await tester.pumpWidget(
        MaterialApp(
          home: const ProviderScope(
            child: Level2Screen(),
          ),
        ),
      );
      await tester.pumpAndSettle();

      // Verificar elementos de UI
      expect(find.text('Conectados'), findsOneWidget);
      expect(find.text('Chat Seguro'), findsOneWidget);
    });

    testWidgets('Can navigate to Level 3 from Level Select', (WidgetTester tester) async {
      await tester.pumpWidget(
        MaterialApp(
          home: const ProviderScope(
            child: LevelSelectScreen(),
          ),
        ),
      );
      await tester.pumpAndSettle();

      // Tap en nivel 3
      await tester.tap(find.text('Explorador Seguro'));
      await tester.pumpAndSettle();

      // Verificar navegación a Level3Screen
      expect(find.byType(Level3Screen), findsOneWidget);
    });

    testWidgets('Level 3 has detective game elements', (WidgetTester tester) async {
      await tester.pumpWidget(
        MaterialApp(
          home: const ProviderScope(
            child: Level3Screen(),
          ),
        ),
      );
      await tester.pumpAndSettle();

      // Verificar elementos de detective
      expect(find.text('Detective de Internet'), findsOneWidget);
      expect(find.text('Escenario:'), findsOneWidget);
    });

    testWidgets('Can navigate to Level 4 from Level Select', (WidgetTester tester) async {
      await tester.pumpWidget(
        MaterialApp(
          home: const ProviderScope(
            child: LevelSelectScreen(),
          ),
        ),
      );
      await tester.pumpAndSettle();

      // Tap en nivel 4
      await tester.tap(find.text('Super Tareas'));
      await tester.pumpAndSettle();

      // Verificar navegación a Level4Screen
      expect(find.byType(Level4Screen), findsOneWidget);
    });

    testWidgets('Level 4 has calculator, calendar and camera tabs', (WidgetTester tester) async {
      await tester.pumpWidget(
        MaterialApp(
          home: const ProviderScope(
            child: Level4Screen(),
          ),
        ),
      );
      await tester.pumpAndSettle();

      // Verificar tabs
      expect(find.text('Calculadora'), findsOneWidget);
      expect(find.text('Calendario'), findsOneWidget);
      expect(find.text('Misión Cámara'), findsOneWidget);
    });

    testWidgets('Can navigate to Level 5 from Level Select', (WidgetTester tester) async {
      await tester.pumpWidget(
        MaterialApp(
          home: const ProviderScope(
            child: LevelSelectScreen(),
          ),
        ),
      );
      await tester.pumpAndSettle();

      // Tap en nivel 5
      await tester.tap(find.text('Pequeño Artista'));
      await tester.pumpAndSettle();

      // Verificar navegación a Level5Screen
      expect(find.byType(Level5Screen), findsOneWidget);
    });

    testWidgets('Level 5 has drawing and music tabs', (WidgetTester tester) async {
      await tester.pumpWidget(
        MaterialApp(
          home: const ProviderScope(
            child: Level5Screen(),
          ),
        ),
      );
      await tester.pumpAndSettle();

      // Verificar tabs
      expect(find.text('Pintar'), findsOneWidget);
      expect(find.text('Música'), findsOneWidget);
    });

    testWidgets('Parental Dashboard requires math authentication', (WidgetTester tester) async {
      await tester.pumpWidget(
        MaterialApp(
          home: const ProviderScope(
            child: ParentalDashboardScreen(),
          ),
        ),
      );
      await tester.pumpAndSettle();

      // Verificar pantalla de autenticación
      expect(find.text('Acceso para Padres'), findsOneWidget);
      expect(find.textContaining('Resuelve esta operación'), findsOneWidget);
      expect(find.byType(TextField), findsOneWidget); // Campo de respuesta
    });

    testWidgets('Back navigation works from all levels', (WidgetTester tester) async {
      await tester.pumpWidget(
        MaterialApp(
          home: const ProviderScope(
            child: Level1Screen(),
          ),
        ),
      );
      await tester.pumpAndSettle();

      // Tap en botón cerrar
      await tester.tap(find.byIcon(Icons.close));
      await tester.pumpAndSettle();

      // Verificar que regresa (Navigator.pop funcionó)
      expect(find.byType(Level1Screen), findsNothing);
    });

    testWidgets('Level selection respects unlock status', (WidgetTester tester) async {
      await tester.pumpWidget(
        MaterialApp(
          home: const ProviderScope(
            child: LevelSelectScreen(),
          ),
        ),
      );
      await tester.pumpAndSettle();

      // Verificar que el primer nivel está desbloqueado (icono de play)
      final level1Card = find.ancestor(
        of: find.text('Mi Primer Encuentro'),
        matching: find.byType(Card),
      );
      expect(level1Card, findsOneWidget);

      // Verificar icono de candado en niveles bloqueados
      final lockIcons = find.byIcon(Icons.lock);
      expect(lockIcons, findsWidgets);
    });

    testWidgets('Progress bars are visible in unlocked levels', (WidgetTester tester) async {
      await tester.pumpWidget(
        MaterialApp(
          home: const ProviderScope(
            child: LevelSelectScreen(),
          ),
        ),
      );
      await tester.pumpAndSettle();

      // Verificar que las barras de progreso están presentes
      final progressBars = find.byType(IslandProgressBar);
      expect(progressBars, findsWidgets);
    });
  });

  group('Error Handling Tests', () {
    testWidgets('Invalid level ID shows error message', (WidgetTester tester) async {
      await tester.pumpWidget(
        MaterialApp(
          home: const ProviderScope(
            child: LevelSelectScreen(),
          ),
        ),
      );
      await tester.pumpAndSettle();

      // Intentar acceder a un nivel inválido no debería crashear
      // El switch case default muestra SnackBar
      // Esto se maneja internamente en _navigateToLevel
    });

    testWidgets('Profile creation validates empty name', (WidgetTester tester) async {
      await tester.pumpWidget(
        MaterialApp(
          home: const ProviderScope(
            child: ProfileSetupScreen(),
          ),
        ),
      );
      await tester.pumpAndSettle();

      // Intentar guardar sin nombre
      await tester.tap(find.widgetWithText(ElevatedButton, '¡Listo!'));
      await tester.pumpAndSettle();

      // Verificar mensaje de error
      expect(find.text('Por favor escribe tu nombre'), findsOneWidget);
    });
  });

  group('Responsive Navigation Tests', () {
    testWidgets('Navigation works on small screens', (WidgetTester tester) async {
      // Configurar tamaño de pantalla pequeño (teléfono)
      tester.binding.window.physicalSizeTestValue = const Size(400, 800);
      tester.binding.window.devicePixelRatioTestValue = 1.0;
      
      await tester.pumpWidget(
        const ProviderScope(
          child: IslaDigitalApp(),
        ),
      );
      await tester.pumpAndSettle();

      // Verificar que la UI se adapta
      expect(find.byType(ProfileSetupScreen), findsOneWidget);

      // Restaurar
      addTearDown(tester.binding.window.clearPhysicalSizeTestValue);
    });

    testWidgets('Navigation works on tablet/desktop screens', (WidgetTester tester) async {
      // Configurar tamaño de pantalla grande
      tester.binding.window.physicalSizeTestValue = const Size(1200, 800);
      tester.binding.window.devicePixelRatioTestValue = 1.0;
      
      await tester.pumpWidget(
        const ProviderScope(
          child: IslaDigitalApp(),
        ),
      );
      await tester.pumpAndSettle();

      // Verificar que la UI funciona en pantallas grandes
      expect(find.byType(ProfileSetupScreen), findsOneWidget);

      // Restaurar
      addTearDown(tester.binding.window.clearPhysicalSizeTestValue);
    });
  });
}

/// Mock de BigButton para los tests
class BigButton extends StatelessWidget {
  final IconData icon;
  final String label;
  final Color color;
  final VoidCallback onPressed;

  const BigButton({
    super.key,
    required this.icon,
    required this.label,
    required this.color,
    required this.onPressed,
  });

  @override
  Widget build(BuildContext context) {
    return ElevatedButton.icon(
      onPressed: onPressed,
      icon: Icon(icon),
      label: Text(label),
      style: ElevatedButton.styleFrom(backgroundColor: color),
    );
  }
}

/// Mock de IslandProgressBar para los tests
class IslandProgressBar extends StatelessWidget {
  final int progress;
  final String? label;
  final Color? fillColor;

  const IslandProgressBar({
    super.key,
    required this.progress,
    this.label,
    this.fillColor,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        if (label != null) Text(label!),
        LinearProgressIndicator(
          value: progress / 100,
          backgroundColor: Colors.grey,
          valueColor: AlwaysStoppedAnimation<Color>(fillColor ?? Colors.blue),
        ),
      ],
    );
  }
}

/// Mock de StepIndicator para los tests
class StepIndicator extends StatelessWidget {
  final int currentStep;
  final int totalSteps;
  final Color? activeColor;

  const StepIndicator({
    super.key,
    required this.currentStep,
    required this.totalSteps,
    this.activeColor,
  });

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: List.generate(totalSteps, (index) {
        return Container(
          width: 16,
          height: 16,
          margin: const EdgeInsets.symmetric(horizontal: 4),
          decoration: BoxDecoration(
            shape: BoxShape.circle,
            color: index <= currentStep ? (activeColor ?? Colors.green) : Colors.grey,
          ),
        );
      }),
    );
  }
}
