import 'package:flutter/material.dart';
import 'package:lottie/lottie.dart';
import 'package:audioplayers/audioplayers.dart';

import '../../core/theme/app_theme.dart';

/// Pantalla "Galería Secreta" que muestra TODOS los assets del proyecto.
/// Organizada en pestañas para una navegación clara.
class ShowcaseScreen extends StatefulWidget {
  const ShowcaseScreen({super.key});

  @override
  State<ShowcaseScreen> createState() => _ShowcaseScreenState();
}

class _ShowcaseScreenState extends State<ShowcaseScreen>
    with SingleTickerProviderStateMixin {
  final AudioPlayer _audioPlayer = AudioPlayer();
  bool _isPlaying = false;
  late TabController _tabController;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 4, vsync: this);
  }

  @override
  void dispose() {
    _audioPlayer.dispose();
    _tabController.dispose();
    super.dispose();
  }

  void _toggleAudio() async {
    if (_isPlaying) {
      await _audioPlayer.pause();
    } else {
      await _audioPlayer.play(AssetSource('audio/music/01.mp3'));
    }
    setState(() => _isPlaying = !_isPlaying);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text(
          'Galería Secreta',
          style: TextStyle(fontFamily: 'Nunito', fontWeight: FontWeight.bold),
        ),
        backgroundColor: IslaColors.oceanBlue,
        foregroundColor: IslaColors.white,
        bottom: TabBar(
          controller: _tabController,
          indicatorColor: IslaColors.sunYellow,
          labelColor: IslaColors.white,
          unselectedLabelColor: IslaColors.white.withOpacity(0.6),
          tabs: const [
            Tab(icon: Icon(Icons.animation), text: 'Animaciones'),
            Tab(icon: Icon(Icons.music_note), text: 'Audio'),
            Tab(icon: Icon(Icons.image), text: 'Fondos'),
            Tab(icon: Icon(Icons.apps), text: 'Iconos'),
          ],
        ),
      ),
      body: TabBarView(
        controller: _tabController,
        children: [
          _buildAnimationsTab(),
          _buildAudioTab(),
          _buildBackgroundsTab(),
          _buildIconsTab(),
        ],
      ),
    );
  }

  Widget _buildAnimationsTab() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _sectionTitle('Animaciones UI (Lottie)'),
          const SizedBox(height: 12),
          _animationCard(
              'Kaleidoscope', 'assets/animations/ui/Kaleidoscope.json'),
          const SizedBox(height: 16),
          _animationCard(
              'Wave Loading', 'assets/animations/ui/wave_loading.json'),
          const SizedBox(height: 16),
          _animationCard('Animation UI',
              'assets/animations/ui/Animation - 1737558657648.json'),
          const SizedBox(height: 24),
          _sectionTitle('Animaciones de Personajes'),
          const SizedBox(height: 12),
          _animationCard('Meditating Giraffe',
              'assets/animations/characters/Meditating Giraffe.json'),
          const SizedBox(height: 16),
          _animationCard(
              'Cat Movement', 'assets/animations/characters/Cat Movement.json'),
        ],
      ),
    );
  }

  Widget _animationCard(String name, String path) {
    return Card(
      elevation: 4,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: Padding(
        padding: const EdgeInsets.all(12),
        child: Column(
          children: [
            SizedBox(
              height: 150,
              child: Lottie.asset(path, fit: BoxFit.contain),
            ),
            const SizedBox(height: 8),
            Text(name, style: const TextStyle(fontWeight: FontWeight.bold)),
            Text(path.split('/').last,
                style: const TextStyle(fontSize: 10, color: IslaColors.grey)),
          ],
        ),
      ),
    );
  }

  Widget _buildAudioTab() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(
            _isPlaying ? Icons.pause_circle_filled : Icons.play_circle_filled,
            size: 120,
            color: IslaColors.oceanBlue,
          ),
          const SizedBox(height: 24),
          Text(
            _isPlaying ? 'Reproduciendo...' : 'Música de fondo',
            style: const TextStyle(
                fontSize: 20,
                fontWeight: FontWeight.bold,
                fontFamily: 'Nunito'),
          ),
          const SizedBox(height: 8),
          const Text(
            'assets/audio/music/01.mp3',
            style: const TextStyle(fontSize: 12, color: IslaColors.grey),
          ),
          const SizedBox(height: 32),
          ElevatedButton.icon(
            onPressed: _toggleAudio,
            icon: Icon(_isPlaying ? Icons.pause : Icons.play_arrow),
            label: Text(_isPlaying ? 'Pausar' : 'Reproducir'),
            style: ElevatedButton.styleFrom(
              padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 16),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildBackgroundsTab() {
    final backgrounds = [
      {
        'name': 'Playa Tropical (Día)',
        'path': 'assets/images/backgrounds/bg_beach_tropical_day.jpg',
      },
      {
        'name': 'Menú Principal (Desenfocado)',
        'path': 'assets/images/backgrounds/bg_main_menu_blurred.png',
      },
      {
        'name': 'Cielo Nocturno',
        'path': 'assets/images/backgrounds/bg_sky_night_stars.webp',
      },
      {
        'name': 'Atardecer Cálido',
        'path': 'assets/images/backgrounds/bg_sunset_warm.png',
      },
    ];

    return ListView.builder(
      padding: const EdgeInsets.all(16),
      itemCount: backgrounds.length,
      itemBuilder: (context, index) {
        final bg = backgrounds[index];
        return Card(
          margin: const EdgeInsets.only(bottom: 16),
          elevation: 4,
          shape:
              RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
          clipBehavior: Clip.antiAlias,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              SizedBox(
                height: 180,
                width: double.infinity,
                child: Image.asset(bg['path']!, fit: BoxFit.cover),
              ),
              Padding(
                padding: const EdgeInsets.all(12),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(bg['name']!,
                        style: const TextStyle(fontWeight: FontWeight.bold)),
                    Text(bg['path']!,
                        style: const TextStyle(
                            fontSize: 10, color: IslaColors.grey)),
                  ],
                ),
              ),
            ],
          ),
        );
      },
    );
  }

  Widget _buildIconsTab() {
    final icons = [
      {'name': 'App Icon', 'path': 'assets/icons/app_icon.png'},
      {
        'name': 'App Icon Background',
        'path': 'assets/icons/app_icon_background.png'
      },
      {
        'name': 'App Icon Foreground',
        'path': 'assets/icons/app_icon_foreground.png'
      },
      {'name': 'Splash Logo', 'path': 'assets/icons/splash_logo.png'},
      {'name': 'Splash Branding', 'path': 'assets/icons/splash_branding.png'},
    ];

    return GridView.builder(
      padding: const EdgeInsets.all(16),
      gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
        crossAxisCount: 2,
        crossAxisSpacing: 16,
        mainAxisSpacing: 16,
        childAspectRatio: 0.85,
      ),
      itemCount: icons.length,
      itemBuilder: (context, index) {
        final icon = icons[index];
        return Card(
          elevation: 4,
          shape:
              RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
          child: Padding(
            padding: const EdgeInsets.all(12),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Expanded(
                  child: Image.asset(icon['path']!, fit: BoxFit.contain),
                ),
                const SizedBox(height: 8),
                Text(
                  icon['name']!,
                  style: const TextStyle(
                      fontWeight: FontWeight.bold, fontSize: 12),
                  textAlign: TextAlign.center,
                ),
              ],
            ),
          ),
        );
      },
    );
  }

  Widget _sectionTitle(String title) {
    return Text(
      title,
      style: const TextStyle(
        fontSize: 18,
        fontWeight: FontWeight.bold,
        fontFamily: 'Nunito',
        color: IslaColors.oceanDark,
      ),
    );
  }
}
