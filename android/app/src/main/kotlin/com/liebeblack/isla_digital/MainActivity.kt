package com.liebeblack.isla_digital

import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.android.FlutterActivityLaunchConfigs.BackgroundMode
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugins.GeneratedPluginRegistrant
import android.os.Bundle

class MainActivity: FlutterActivity() {

    // 1. Optimización de inicio: Renderizado transparente para evitar el "flash" blanco
    override fun getBackgroundMode(): BackgroundMode {
        return BackgroundMode.transparent
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Aquí podrías añadir lógica de pantalla completa para juegos si fuera necesario
    }

    // 2. Registro de plugins optimizado
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        // Esto asegura que todos los plugins (web_view, video_player) se registren correctamente
        GeneratedPluginRegistrant.registerWith(flutterEngine)
    }
}
