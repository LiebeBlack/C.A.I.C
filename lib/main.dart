import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import 'core/providers/app_providers.dart';
import 'core/services/services.dart';
import 'core/theme/app_theme.dart';
import 'core/utils/page_transitions.dart';
import 'ui/screens/home_screen.dart';
import 'ui/screens/level_select_screen.dart';
import 'ui/screens/parental_dashboard_screen.dart';
import 'ui/screens/profile_setup_screen.dart';
import 'ui/screens/showcase_screen.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // Inicializar almacenamiento local
  await LocalStorageService.initialize();
  await BackgroundMusicService.initialize();

  runApp(
    const ProviderScope(
      child: IslaDigitalApp(),
    ),
  );
}

class IslaDigitalApp extends ConsumerWidget {
  const IslaDigitalApp({super.key});

  /// Mapa centralizado de rutas → widgets para reducir verbosidad.
  static final Map<String, Widget Function()> _routes = {
    '/home': () => const HomeScreen(),
    '/profile': () => const ProfileSetupScreen(),
    '/levels': () => const LevelSelectScreen(),
    '/parental': () => const ParentalDashboardScreen(),
    '/showcase': () => const ShowcaseScreen(),
  };

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final profile = ref.watch(currentProfileProvider);

    return MaterialApp(
      title: 'Isla Digital',
      debugShowCheckedModeBanner: false,
      theme: IslaThemes.lightTheme,
      initialRoute: profile == null ? '/profile' : '/home',
      onGenerateRoute: (settings) {
        final builder = _routes[settings.name];
        if (builder != null) {
          return FadeSlideRoute(page: builder());
        }
        // Fallback: ruta no encontrada
        return MaterialPageRoute(
          builder: (_) => const Scaffold(
            body: Center(child: Text('Ruta no encontrada')),
          ),
        );
      },
    );
  }
}
