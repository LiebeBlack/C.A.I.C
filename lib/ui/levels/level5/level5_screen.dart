import 'package:flutter/material.dart';
import 'dart:math';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/theme/app_theme.dart';
import '../../../core/providers/app_providers.dart';
import '../../../core/models/badge.dart';
import '../../widgets/island_background.dart';
import '../../widgets/progress_widgets.dart';

/// Nivel 5: Pequeño Artista
/// Lienzo de dibujo y Secuenciador de ritmos musicales
class Level5Screen extends ConsumerStatefulWidget {
  const Level5Screen({super.key});

  @override
  ConsumerState<Level5Screen> createState() => _Level5ScreenState();
}

class _Level5ScreenState extends ConsumerState<Level5Screen> {
  int currentTab = 0;
  int totalProgress = 0;
  bool showCelebration = false;
  String celebrationMessage = '';

  final List<String> tabs = ['Pintar', 'Música'];

  void _addProgress(int points) {
    setState(() {
      totalProgress += points;
    });
    ref.read(levelProgressProvider('level_5').notifier).setProgress(totalProgress);
    ref.read(currentProfileProvider.notifier).addProgress('level_5', points);

    if (totalProgress >= 100) {
      _completeLevel();
    }
  }

  void _completeLevel() {
    setState(() {
      showCelebration = true;
      celebrationMessage = '¡Pintor de Atardeceres!';
    });

    final badge = IslaBadges.getById('artista_isla');
    if (badge != null) {
      ref.read(currentProfileProvider.notifier).addBadge(badge.id);
    }

    final musicianBadge = IslaBadges.getById('musician_tropical');
    if (musicianBadge != null) {
      ref.read(currentProfileProvider.notifier).addBadge(musicianBadge.id);
    }

    Future.delayed(const Duration(seconds: 3), () {
      if (mounted) {
        setState(() => showCelebration = false);
        Navigator.pop(context);
      }
    });
  }

  void _showSuccessFeedback(String message) {
    setState(() {
      showCelebration = true;
      celebrationMessage = message;
    });
    Future.delayed(const Duration(seconds: 1), () {
      if (mounted) setState(() => showCelebration = false);
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Stack(
        children: [
          IslandBackground(
            child: SafeArea(
              child: Column(
                children: [
                  _buildHeader(),
                  const SizedBox(height: 16),
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 24),
                    child: IslandProgressBar(
                      progress: totalProgress.clamp(0, 100),
                      label: 'Progreso de Artista',
                      fillColor: IslaColors.sunsetPink,
                    ),
                  ),
                  const SizedBox(height: 16),
                  _buildTabBar(),
                  const SizedBox(height: 16),
                  Expanded(
                    child: IndexedStack(
                      index: currentTab,
                      children: [
                        DrawingCanvas(onProgress: _addProgress, onSuccess: _showSuccessFeedback),
                        MusicSequencer(onProgress: _addProgress, onSuccess: _showSuccessFeedback),
                      ],
                    ),
                  ),
                ],
              ),
            ),
          ),
          CelebrationOverlay(
            isVisible: showCelebration,
            message: celebrationMessage,
          ),
        ],
      ),
    );
  }

  Widget _buildHeader() {
    return Padding(
      padding: const EdgeInsets.all(16),
      child: Row(
        children: [
          IconButton(
            onPressed: () => Navigator.pop(context),
            icon: const Icon(Icons.close),
            color: IslaColors.sunsetPink,
          ),
          const SizedBox(width: 8),
          Expanded(
            child: Text(
              'Pequeño Artista',
              style: Theme.of(context).textTheme.titleLarge?.copyWith(
                    color: IslaColors.sunsetPink,
                    fontWeight: FontWeight.bold,
                  ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildTabBar() {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 16),
      padding: const EdgeInsets.all(4),
      decoration: BoxDecoration(
        color: IslaColors.greyLight,
        borderRadius: BorderRadius.circular(16),
      ),
      child: Row(
        children: List.generate(tabs.length, (index) {
          final isSelected = currentTab == index;
          return Expanded(
            child: GestureDetector(
              onTap: () => setState(() => currentTab = index),
              child: Container(
                padding: const EdgeInsets.symmetric(vertical: 12),
                decoration: BoxDecoration(
                  color: isSelected ? IslaColors.sunsetPink : Colors.transparent,
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Icon(
                      index == 0 ? Icons.brush : Icons.music_note,
                      size: 20,
                      color: isSelected ? IslaColors.white : IslaColors.greyDark,
                    ),
                    const SizedBox(width: 4),
                    Text(
                      tabs[index],
                      style: Theme.of(context).textTheme.titleSmall?.copyWith(
                            color: isSelected ? IslaColors.white : IslaColors.greyDark,
                            fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
                          ),
                    ),
                  ],
                ),
              ),
            ),
          );
        }),
      ),
    );
  }
}

/// Lienzo de dibujo con colores caribeños
class DrawingCanvas extends StatefulWidget {
  final Function(int) onProgress;
  final Function(String) onSuccess;

  const DrawingCanvas({super.key, required this.onProgress, required this.onSuccess});

  @override
  State<DrawingCanvas> createState() => _DrawingCanvasState();
}

class _DrawingCanvasState extends State<DrawingCanvas> {
  List<DrawPoint> points = [];
  Color selectedColor = IslaColors.oceanBlue;
  double strokeWidth = 8;
  int strokesCount = 0;

  final List<Color> caribbeanColors = [
    IslaColors.oceanBlue,
    IslaColors.oceanLight,
    IslaColors.sunYellow,
    IslaColors.sunOrange,
    IslaColors.palmGreen,
    IslaColors.sunsetPink,
    IslaColors.sunsetCoral,
    IslaColors.sand,
    IslaColors.white,
    IslaColors.black,
  ];

  void _onPanStart(DragStartDetails details) {
    setState(() {
      points.add(DrawPoint(
        offset: details.localPosition,
        paint: Paint()
          ..color = selectedColor
          ..strokeWidth = strokeWidth
          ..strokeCap = StrokeCap.round
          ..strokeJoin = StrokeJoin.round,
      ));
    });
  }

  void _onPanUpdate(DragUpdateDetails details) {
    setState(() {
      points.add(DrawPoint(
        offset: details.localPosition,
        paint: Paint()
          ..color = selectedColor
          ..strokeWidth = strokeWidth
          ..strokeCap = StrokeCap.round
          ..strokeJoin = StrokeJoin.round,
      ));
    });
  }

  void _onPanEnd(DragEndDetails details) {
    setState(() {
      points.add(DrawPoint(offset: null, paint: null));
      strokesCount++;
      if (strokesCount >= 5) {
        widget.onSuccess('¡Gran obra de arte!');
        widget.onProgress(20);
        strokesCount = 0;
      }
    });
  }

  void _clearCanvas() {
    setState(() {
      points.clear();
      strokesCount = 0;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          child: Column(
            children: [
              _buildColorPalette(),
              const SizedBox(height: 8),
              _buildBrushSize(),
            ],
          ),
        ),
        const SizedBox(height: 16),
        Expanded(
          child: Container(
            margin: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              color: IslaColors.white,
              borderRadius: BorderRadius.circular(20),
              boxShadow: [
                BoxShadow(
                  color: IslaColors.black.withOpacity(0.1),
                  blurRadius: 10,
                  offset: const Offset(0, 4),
                ),
              ],
            ),
            child: ClipRRect(
              borderRadius: BorderRadius.circular(20),
              child: GestureDetector(
                onPanStart: _onPanStart,
                onPanUpdate: _onPanUpdate,
                onPanEnd: _onPanEnd,
                child: CustomPaint(
                  painter: DrawingPainter(points: points),
                  size: Size.infinite,
                ),
              ),
            ),
          ),
        ),
        Padding(
          padding: const EdgeInsets.all(16),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              ElevatedButton.icon(
                onPressed: _clearCanvas,
                icon: const Icon(Icons.delete),
                label: const Text('Borrar'),
                style: ElevatedButton.styleFrom(
                  backgroundColor: IslaColors.error,
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildColorPalette() {
    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: IslaColors.greyLight,
        borderRadius: BorderRadius.circular(16),
      ),
      child: Wrap(
        spacing: 8,
        runSpacing: 8,
        alignment: WrapAlignment.center,
        children: caribbeanColors.map((color) {
          final isSelected = selectedColor == color;
          return GestureDetector(
            onTap: () => setState(() => selectedColor = color),
            child: AnimatedContainer(
              duration: const Duration(milliseconds: 200),
              width: isSelected ? 44 : 36,
              height: isSelected ? 44 : 36,
              decoration: BoxDecoration(
                color: color,
                shape: BoxShape.circle,
                border: Border.all(
                  color: isSelected ? IslaColors.sunYellow : IslaColors.white,
                  width: isSelected ? 4 : 2,
                ),
                boxShadow: isSelected
                    ? [
                        BoxShadow(
                          color: color.withOpacity(0.5),
                          blurRadius: 8,
                          spreadRadius: 2,
                        ),
                      ]
                    : null,
              ),
            ),
          );
        }).toList(),
      ),
    );
  }

  Widget _buildBrushSize() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        const Icon(Icons.circle, size: 8),
        Expanded(
          child: Slider(
            value: strokeWidth,
            min: 4,
            max: 24,
            onChanged: (value) => setState(() => strokeWidth = value),
          ),
        ),
        const Icon(Icons.circle, size: 24),
      ],
    );
  }
}

class DrawPoint {
  final Offset? offset;
  final Paint? paint;

  DrawPoint({this.offset, this.paint});
}

class DrawingPainter extends CustomPainter {
  final List<DrawPoint> points;

  DrawingPainter({required this.points});

  @override
  void paint(Canvas canvas, Size size) {
    for (int i = 0; i < points.length - 1; i++) {
      if (points[i].offset != null && points[i + 1].offset != null) {
        canvas.drawLine(
          points[i].offset!,
          points[i + 1].offset!,
          points[i].paint!,
        );
      } else if (points[i].offset != null && points[i + 1].offset == null) {
        canvas.drawCircle(
          points[i].offset!,
          points[i].paint!.strokeWidth / 2,
          points[i].paint!,
        );
      }
    }
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => true;
}

/// Secuenciador de ritmos musicales tipo tambor y maracas
class MusicSequencer extends StatefulWidget {
  final Function(int) onProgress;
  final Function(String) onSuccess;

  const MusicSequencer({super.key, required this.onProgress, required this.onSuccess});

  @override
  State<MusicSequencer> createState() => _MusicSequencerState();
}

class _MusicSequencerState extends State<MusicSequencer> {
  final int beats = 8;
  final List<List<bool>> grid = [];
  int currentBeat = -1;
  bool isPlaying = false;
  int playCount = 0;

  final List<Map<String, dynamic>> instruments = [
    {'name': 'Tambor', 'icon': Icons.notifications, 'color': IslaColors.sunOrange, 'emoji': '🥁'},
    {'name': 'Maracas', 'icon': Icons.grain, 'color': IslaColors.palmGreen, 'emoji': '🎵'},
    {'name': 'Campana', 'icon': Icons.notifications_active, 'color': IslaColors.sunYellow, 'emoji': '🔔'},
    {'name': 'Concha', 'icon': Icons.surfing, 'color': IslaColors.oceanLight, 'emoji': '🐚'},
  ];

  @override
  void initState() {
    super.initState();
    for (int i = 0; i < instruments.length; i++) {
      grid.add(List.generate(beats, (_) => false));
    }
  }

  void _toggleBeat(int instrument, int beat) {
    setState(() {
      grid[instrument][beat] = !grid[instrument][beat];
    });
  }

  void _playSequence() async {
    if (isPlaying) return;
    setState(() => isPlaying = true);

    for (int beat = 0; beat < beats; beat++) {
      setState(() => currentBeat = beat);
      await Future.delayed(const Duration(milliseconds: 400));
    }

    setState(() {
      currentBeat = -1;
      isPlaying = false;
      playCount++;
    });

    if (playCount >= 3) {
      widget.onSuccess('¡Ritmo perfecto!');
      widget.onProgress(20);
      playCount = 0;
    }
  }

  void _clearGrid() {
    setState(() {
      for (var row in grid) {
        for (int i = 0; i < row.length; i++) {
          row[i] = false;
        }
      }
    });
  }

  void _loadPreset(String type) {
    _clearGrid();
    setState(() {
      if (type == 'tambor') {
        grid[0] = [true, false, true, false, true, false, true, false];
      } else if (type == 'merengue') {
        grid[0] = [true, false, true, false, true, false, true, false];
        grid[1] = [false, true, false, true, false, true, false, true];
        grid[2] = [true, false, false, false, true, false, false, false];
      } else if (type == 'gaita') {
        grid[0] = [true, true, false, true, true, false, true, true];
        grid[1] = [false, false, true, false, false, true, false, false];
        grid[3] = [true, false, false, false, true, false, false, false];
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(16),
      child: Column(
        children: [
          _buildPresets(),
          const SizedBox(height: 16),
          Expanded(
            child: Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: IslaColors.greyLight,
                borderRadius: BorderRadius.circular(20),
              ),
              child: Column(
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: List.generate(beats, (beat) {
                      return Container(
                        width: 32,
                        height: 32,
                        decoration: BoxDecoration(
                          color: currentBeat == beat
                              ? IslaColors.sunsetPink
                              : IslaColors.grey.withOpacity(0.3),
                          shape: BoxShape.circle,
                        ),
                        child: Center(
                          child: Text(
                            '${beat + 1}',
                            style: TextStyle(
                              color: currentBeat == beat ? IslaColors.white : IslaColors.grey,
                              fontWeight: FontWeight.bold,
                              fontSize: 12,
                            ),
                          ),
                        ),
                      );
                    }),
                  ),
                  const SizedBox(height: 16),
                  Expanded(
                    child: ListView.builder(
                      itemCount: instruments.length,
                      itemBuilder: (context, instrumentIndex) {
                        return _buildInstrumentRow(instrumentIndex);
                      },
                    ),
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 16),
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              ElevatedButton.icon(
                onPressed: isPlaying ? null : _playSequence,
                icon: Icon(isPlaying ? Icons.stop : Icons.play_arrow),
                label: Text(isPlaying ? 'Reproduciendo...' : '¡Tocar!'),
                style: ElevatedButton.styleFrom(
                  backgroundColor: IslaColors.palmGreen,
                  padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 16),
                ),
              ),
              const SizedBox(width: 16),
              ElevatedButton.icon(
                onPressed: _clearGrid,
                icon: const Icon(Icons.delete),
                label: const Text('Borrar'),
                style: ElevatedButton.styleFrom(
                  backgroundColor: IslaColors.error,
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildPresets() {
    return Wrap(
      spacing: 8,
      runSpacing: 8,
      alignment: WrapAlignment.center,
      children: [
        _buildPresetChip('Tambor básico', 'tambor'),
        _buildPresetChip('Merengue', 'merengue'),
        _buildPresetChip('Gaita', 'gaita'),
      ],
    );
  }

  Widget _buildPresetChip(String label, String preset) {
    return ActionChip(
      avatar: const Icon(Icons.music_note, size: 18),
      label: Text(label),
      onPressed: () => _loadPreset(preset),
      backgroundColor: IslaColors.sunYellow.withOpacity(0.3),
    );
  }

  Widget _buildInstrumentRow(int instrumentIndex) {
    final instrument = instruments[instrumentIndex];

    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        children: [
          Container(
            width: 80,
            child: Row(
              children: [
                Text(
                  instrument['emoji'] as String,
                  style: const TextStyle(fontSize: 24),
                ),
                const SizedBox(width: 4),
                Expanded(
                  child: Text(
                    instrument['name'] as String,
                    style: Theme.of(context).textTheme.bodySmall?.copyWith(
                          fontWeight: FontWeight.bold,
                        ),
                    overflow: TextOverflow.ellipsis,
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(width: 8),
          Expanded(
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: List.generate(beats, (beat) {
                final isActive = grid[instrumentIndex][beat];
                return GestureDetector(
                  onTap: () => _toggleBeat(instrumentIndex, beat),
                  child: AnimatedContainer(
                    duration: const Duration(milliseconds: 100),
                    width: 32,
                    height: 40,
                    decoration: BoxDecoration(
                      color: isActive
                          ? (instrument['color'] as Color)
                          : IslaColors.white,
                      borderRadius: BorderRadius.circular(8),
                      border: Border.all(
                        color: isActive
                            ? (instrument['color'] as Color)
                            : IslaColors.grey,
                        width: 2,
                      ),
                    ),
                    child: isActive
                        ? const Icon(Icons.music_note, color: IslaColors.white, size: 16)
                        : null,
                  ),
                );
              }),
            ),
          ),
        ],
      ),
    );
  }
}
