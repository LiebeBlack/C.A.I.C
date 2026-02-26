import 'package:audioplayers/audioplayers.dart';

class BackgroundMusicService {
  static final AudioPlayer _player = AudioPlayer();
  static bool _isPlaying = false;
  static bool _isMuted = false;

  static Future<void> initialize() async {
    try {
      await _player.setReleaseMode(ReleaseMode.loop);
      await startMusic();
    } catch (e) {
      print('Error initializing background music: $e');
    }
  }

  static Future<void> startMusic() async {
    if (_isPlaying || _isMuted) return;
    try {
      await _player.play(AssetSource('audio/music/01.mp3'));
      _isPlaying = true;
    } catch (e) {
      print('Error playing background music: $e');
    }
  }

  static Future<void> stopMusic() async {
    if (!_isPlaying) return;
    try {
      await _player.stop();
      _isPlaying = false;
    } catch (e) {
      print('Error stopping background music: $e');
    }
  }

  static Future<void> pauseMusic() async {
    if (!_isPlaying) return;
    try {
      await _player.pause();
      _isPlaying = false;
    } catch (e) {
      print('Error pausing background music: $e');
    }
  }

  static Future<void> resumeMusic() async {
    if (_isPlaying || _isMuted) return;
    try {
      await _player.resume();
      _isPlaying = true;
    } catch (e) {
      print('Error resuming background music: $e');
    }
  }

  static Future<void> toggleMute() async {
    _isMuted = !_isMuted;
    if (_isMuted) {
      await _player.setVolume(0);
    } else {
      await _player.setVolume(1);
    }
  }

  static bool get isMuted => _isMuted;
}
