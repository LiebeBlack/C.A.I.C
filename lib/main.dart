import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'core/theme/app_theme.dart';
import 'core/providers/app_providers.dart';
import 'core/services/services.dart';
import 'ui/screens/home_screen.dart';
import 'ui/screens/profile_setup_screen.dart';
import 'ui/screens/level_select_screen.dart';
import 'ui/screens/parental_dashboard_screen.dart';

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

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final profile = ref.watch(currentProfileProvider);

    return MaterialApp(
      title: 'Isla Digital',
      debugShowCheckedModeBanner: false,
      theme: IslaThemes.lightTheme,
      initialRoute: profile == null ? '/profile' : '/home',
      routes: {
        '/home': (context) => const HomeScreen(),
        '/profile': (context) => const ProfileSetupScreen(),
        '/levels': (context) => const LevelSelectScreen(),
        '/parental': (context) => const ParentalDashboardScreen(),
      },
    );
  }
}
