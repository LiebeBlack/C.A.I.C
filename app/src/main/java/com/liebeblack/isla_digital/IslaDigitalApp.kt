package com.liebeblack.isla_digital

import android.app.Application
import android.content.Context
import com.liebeblack.isla_digital.data.repository.ChildProfileRepositoryImpl
import com.liebeblack.isla_digital.domain.repository.ChildProfileRepository
import kotlinx.serialization.json.Json

/**
 * Clase de Aplicación moderna 2026.
 * Implementa Manual Dependency Injection de forma limpia.
 */
class IslaDigitalApp : Application() {
    
    // Contenedor de dependencias manual (Manual DI)
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

class AppContainer(context: Context) {
    private val prefs = context.getSharedPreferences("isla_digital_prefs", Context.MODE_PRIVATE)
    
    // Configuración centralizada de Serialización
    private val jsonSerializer = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
    }
    
    val childProfileRepository: ChildProfileRepository by lazy {
        ChildProfileRepositoryImpl(prefs, jsonSerializer)
    }
}
