package com.LBs.EEDA.data.manager

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.util.concurrent.ConcurrentHashMap

/**
 * [EedaAssetProvider]
 * Principio SOLID: Inversión de Dependencias (D).
 * Capa de Abstracción en Dominio para aislar a la UI de las complejidades del sistema de archivos.
 */
interface EedaAssetProvider {
    /**
     * Extrae un recurso mediante un patrón de Referencia Segura.
     * Retorna [Result] conteniendo el [InputStream] si existe, o 'null' controlado si está pendiente.
     */
    suspend fun fetchResource(path: String): Result<InputStream?>
}

/**
 * [VirtualAssetManager] - Tolerancia a Fallos para E.E.D.A.
 * 
 * Esta clase resiliente mapea los recursos declarados en AssetsRegistry.md contra
 * el almacenamiento físico en assets/. Si el archivo aún no se ha subido al código,
 * intercepta la excepción subyacente y emite un null controlado silenciado, 
 * preservando el rendimiento gráfico completo (120 FPS) y asegurando cero bloqueos en UI.
 */
class VirtualAssetManager(
    private val context: Context,
    // Delegamos la lectura de disco (I/O) fuera del Main Thread por defecto (SOLID - S)
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO 
) : EedaAssetProvider {

    private val TAG = "EEDA-VirtualAssetMgr"

    // Optimizacion 2026: Las excepciones son enormemente pesadas de instanciar en la JVM debido
    // al Stack Trace. Usamos esto para memorizar que un asset falta y evitar generar 
    // la excepcion constantemente a 120 FPS.
    private val missingRegistry = ConcurrentHashMap.newKeySet<String>()

    override suspend fun fetchResource(path: String): Result<InputStream?> = withContext(ioDispatcher) {
        // Fast-Path: Si ya sabemos que no existe, no castigamos al Garbage Collector.
        if (missingRegistry.contains(path)) {
            return@withContext Result.success(null)
        }

        try {
            // Intento organico de abrir el archivo real si el desarrollador ya lo arrastro a la carpeta
            val inputStream = context.assets.open(path)
            
            Log.d(TAG, "[E.E.D.A. - OK] Activo grafico o logico montado con exito: \$path")
            Result.success(inputStream)
        } catch (e: Exception) {
            // Manejo silencioso optimizado: Registramos permanentemente como 'faltante' en esta sesion
            missingRegistry.add(path)
            
            Log.w(TAG, "[E.E.D.A. - MISSING_ASSET] El recurso '\$path' no existe aun en src/main/assets/. Rutina de prevencion activada.")
            Result.success(null)
        }
    }
}
