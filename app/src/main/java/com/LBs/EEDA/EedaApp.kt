package com.LBs.EEDA

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Process
import android.util.Log
import com.LBs.EEDA.data.repository.ChildProfileRepositoryImpl
import com.LBs.EEDA.data.repository.HardwareRepositoryImpl
import com.LBs.EEDA.data.repository.MinigameRepositoryImpl
import com.LBs.EEDA.data.repository.SandboxRepositoryImpl
import com.LBs.EEDA.data.repository.AssessmentRepositoryImpl
import com.LBs.EEDA.domain.repository.ChildProfileRepository
import com.LBs.EEDA.domain.repository.HardwareRepository
import kotlinx.serialization.json.Json
import kotlin.system.exitProcess

/**
 * Clase de Aplicación E.E.D.A moderna 2026.
 * Implementa Manual Dependency Injection de forma limpia.
 * Incluye Global Exception Handler para reporte de errores in-app.
 */
class EedaApp : Application() {

    // Contenedor de dependencias manual (Manual DI)
    lateinit var container: AppContainer

    companion object {
        const val VERSION_NAME = "26.4.6"
        const val VERSION_CODE = 260406
    }

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        setupGlobalExceptionHandler()
    }

    /**
     * Configura un manejador global de excepciones no capturadas.
     * Lanza CrashActivity en lugar de cerrar la app abruptamente.
     */
    private fun setupGlobalExceptionHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                Log.e("EedaApp", "Crash detectado en hilo: ${thread.name}", throwable)

                // Preparar datos del crash
                val crashData = CrashReportData(
                    versionName = VERSION_NAME,
                    versionCode = VERSION_CODE,
                    deviceBrand = Build.BRAND ?: "Unknown",
                    deviceModel = Build.MODEL ?: "Unknown",
                    deviceManufacturer = Build.MANUFACTURER ?: "Unknown",
                    deviceProduct = Build.PRODUCT ?: "Unknown",
                    androidVersion = Build.VERSION.RELEASE ?: "Unknown",
                    sdkVersion = Build.VERSION.SDK_INT,
                    architecture = Build.SUPPORTED_ABIS?.firstOrNull() ?: "Unknown",
                    supportedAbis = Build.SUPPORTED_ABIS?.joinToString(", ") ?: "Unknown",
                    threadName = thread.name,
                    exceptionClass = throwable.javaClass.name,
                    exceptionMessage = throwable.message ?: "Sin mensaje",
                    stackTrace = Log.getStackTraceString(throwable),
                    cause = throwable.cause?.let { "${it.javaClass.name}: ${it.message}" },
                    timestamp = System.currentTimeMillis()
                )

                // Guardar el reporte para que CrashActivity lo recupere
                val prefs = getSharedPreferences("eeda_crash_prefs", Context.MODE_PRIVATE)
                prefs.edit().apply {
                    putString("crash_version_name", crashData.versionName)
                    putInt("crash_version_code", crashData.versionCode)
                    putString("crash_device_brand", crashData.deviceBrand)
                    putString("crash_device_model", crashData.deviceModel)
                    putString("crash_device_manufacturer", crashData.deviceManufacturer)
                    putString("crash_device_product", crashData.deviceProduct)
                    putString("crash_android_version", crashData.androidVersion)
                    putInt("crash_sdk_version", crashData.sdkVersion)
                    putString("crash_architecture", crashData.architecture)
                    putString("crash_supported_abis", crashData.supportedAbis)
                    putString("crash_thread_name", crashData.threadName)
                    putString("crash_exception_class", crashData.exceptionClass)
                    putString("crash_exception", crashData.exceptionMessage)
                    putString("crash_stacktrace", crashData.stackTrace)
                    putString("crash_cause", crashData.cause)
                    putLong("crash_timestamp", crashData.timestamp)
                    apply()
                }

                // Lanzar CrashActivity - los datos se leen desde SharedPreferences
                val intent = Intent(this, CrashActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)

                // Esperar un momento para que la actividad se inicie
                Thread.sleep(500)

            } catch (e: Exception) {
                Log.e("EedaApp", "Error al manejar crash", e)
            } finally {
                // Terminar el proceso de forma controlada
                Process.killProcess(Process.myPid())
                exitProcess(1)
            }
        }
    }
}

/**
 * Datos del reporte de crash para serialización.
 */
@kotlinx.serialization.Serializable
data class CrashReportData(
    val versionName: String,
    val versionCode: Int,
    val deviceBrand: String,
    val deviceModel: String,
    val deviceManufacturer: String,
    val deviceProduct: String,
    val androidVersion: String,
    val sdkVersion: Int,
    val architecture: String,
    val supportedAbis: String,
    val threadName: String,
    val exceptionClass: String,
    val exceptionMessage: String,
    val stackTrace: String,
    val cause: String?,
    val timestamp: Long
)

class AppContainer(context: Context) {
    private val prefs = context.getSharedPreferences("eeda_prefs", Context.MODE_PRIVATE)
    
    // Configuración centralizada de Serialización
    private val jsonSerializer = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
    }
    
    val childProfileRepository: ChildProfileRepository by lazy {
        ChildProfileRepositoryImpl(prefs, jsonSerializer)
    }

    val hardwareRepository: HardwareRepository by lazy {
        HardwareRepositoryImpl(context)
    }
    
    // === Repositorios del Sistema Educativo 2026 ===
    val minigameRepository: MinigameRepositoryImpl by lazy {
        MinigameRepositoryImpl(context, jsonSerializer)
    }
    
    val sandboxRepository: SandboxRepositoryImpl by lazy {
        SandboxRepositoryImpl(context, jsonSerializer)
    }
    
    val assessmentRepository: AssessmentRepositoryImpl by lazy {
        AssessmentRepositoryImpl(context, jsonSerializer)
    }
}
