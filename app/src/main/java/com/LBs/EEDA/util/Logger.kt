package com.LBs.EEDA.util

import android.util.Log
import com.LBs.EEDA.BuildConfig

/**
 * Sistema de Logging centralizado E.E.D.A
 * Niveles: VERBOSE, DEBUG, INFO, WARN, ERROR
 */
object Logger {

    private const val TAG_PREFIX = "EEDA"
    private var isDebugMode = BuildConfig.DEBUG

    // Categorías de log
    object Category {
        const val UI = "UI"
        const val DATA = "DATA"
        const val NETWORK = "NET"
        const val SECURITY = "SEC"
        const val PERFORMANCE = "PERF"
        const val ANALYTICS = "ANALYTICS"
        const val GAMEPLAY = "GAME"
        const val LIFECYCLE = "LIFE"
    }

    @JvmStatic
    fun v(category: String, message: String, throwable: Throwable? = null) {
        if (isDebugMode) {
            Log.v("$TAG_PREFIX.$category", message, throwable)
        }
    }

    @JvmStatic
    fun d(category: String, message: String, throwable: Throwable? = null) {
        if (isDebugMode) {
            Log.d("$TAG_PREFIX.$category", message, throwable)
        }
    }

    @JvmStatic
    fun i(category: String, message: String, throwable: Throwable? = null) {
        Log.i("$TAG_PREFIX.$category", message, throwable)
    }

    @JvmStatic
    fun w(category: String, message: String, throwable: Throwable? = null) {
        Log.w("$TAG_PREFIX.$category", message, throwable)
    }

    @JvmStatic
    fun e(category: String, message: String, throwable: Throwable? = null) {
        Log.e("$TAG_PREFIX.$category", message, throwable)
    }

    /**
     * Log de eventos de usuario para analytics
     */
    @JvmStatic
    fun logEvent(category: String, eventName: String, params: Map<String, Any> = emptyMap()) {
        val paramString = params.entries.joinToString(", ") { "${it.key}=${it.value}" }
        i(Category.ANALYTICS, "EVENT: $eventName [$paramString]")
    }

    /**
     * Log de rendimiento
     */
    @JvmStatic
    fun logPerformance(operation: String, durationMs: Long) {
        val level = when {
            durationMs < 16 -> "EXCELLENT" // 60fps
            durationMs < 100 -> "GOOD"
            durationMs < 500 -> "WARNING"
            else -> "CRITICAL"
        }
        d(Category.PERFORMANCE, "$operation: ${durationMs}ms [$level]")
    }

    /**
     * Log de tiempo de ejecución con auto-cálculo
     */
    inline fun <T> measureTime(category: String, operation: String, block: () -> T): T {
        val start = System.currentTimeMillis()
        return try {
            block().also {
                logPerformance(operation, System.currentTimeMillis() - start)
            }
        } catch (e: Exception) {
            e(Category.PERFORMANCE, "$operation failed after ${System.currentTimeMillis() - start}ms", e)
            throw e
        }
    }

    /**
     * Log de errores críticos que requieren atención
     */
    @JvmStatic
    fun logCriticalError(error: Throwable, context: Map<String, String> = emptyMap()) {
        val contextString = context.entries.joinToString("\n") { "  ${it.key}: ${it.value}" }
        e(Category.SECURITY, """
            CRITICAL ERROR:
            Type: ${error.javaClass.simpleName}
            Message: ${error.message}
            Context:
            $contextString
            Stack trace:
            ${error.stackTraceToString()}
        """.trimIndent())
    }

    /**
     * Log de cambios de estado del usuario
     */
    @JvmStatic
    fun logUserStateChange(fromState: String, toState: String, reason: String? = null) {
        i(Category.LIFECYCLE, "State: $fromState -> $toState" + if (reason != null) " ($reason)" else "")
    }

    /**
     * Log de gameplay para debugging
     */
    @JvmStatic
    fun logGameplay(action: String, details: String) {
        d(Category.GAMEPLAY, "$action: $details")
    }

    /**
     * Log de database operations
     */
    @JvmStatic
    fun logDatabase(operation: String, entity: String, id: String? = null) {
        val idString = if (id != null) " [id=$id]" else ""
        d(Category.DATA, "$operation $entity$idString")
    }

    /**
     * Log de seguridad (intentos de acceso, etc.)
     */
    @JvmStatic
    fun logSecurity(event: String, userId: String? = null, success: Boolean) {
        val userString = if (userId != null) " user=$userId" else ""
        val result = if (success) "SUCCESS" else "FAILED"
        i(Category.SECURITY, "$event$userString - $result")
    }

    /**
     * Configurar modo debug
     */
    fun setDebugMode(enabled: Boolean) {
        isDebugMode = enabled
    }

    /**
     * Limpia logs antiguos (conceptual - implementar con archivo si es necesario)
     */
    fun clearOldLogs() {
        // Implementar si se usa persistencia de logs
        d(Category.DATA, "Logs cleared")
    }
}
