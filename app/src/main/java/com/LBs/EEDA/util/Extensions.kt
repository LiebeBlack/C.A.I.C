package com.LBs.EEDA.util

import androidx.compose.ui.graphics.Color
import kotlin.math.roundToInt

/**
 * Extensiones útiles de Kotlin para E.E.D.A
 */

// ═══════════════════════════════════════════════════════════════
// Extensiones de Color
// ═══════════════════════════════════════════════════════════════

fun Color.Companion.parse(hex: String): Color {
    val cleanHex = hex.removePrefix("#")
    return when (cleanHex.length) {
        6 -> Color(
            red = cleanHex.substring(0, 2).toInt(16) / 255f,
            green = cleanHex.substring(2, 4).toInt(16) / 255f,
            blue = cleanHex.substring(4, 6).toInt(16) / 255f
        )
        8 -> Color(
            alpha = cleanHex.substring(0, 2).toInt(16) / 255f,
            red = cleanHex.substring(2, 4).toInt(16) / 255f,
            green = cleanHex.substring(4, 6).toInt(16) / 255f,
            blue = cleanHex.substring(6, 8).toInt(16) / 255f
        )
        else -> Color.Black
    }
}

fun Color.toHex(): String {
    val red = (red * 255).roundToInt()
    val green = (green * 255).roundToInt()
    val blue = (blue * 255).roundToInt()
    return String.format("#%02X%02X%02X", red, green, blue)
}

fun Color.lighten(factor: Float): Color {
    return Color(
        red = (red + (1 - red) * factor).coerceIn(0f, 1f),
        green = (green + (1 - green) * factor).coerceIn(0f, 1f),
        blue = (blue + (1 - blue) * factor).coerceIn(0f, 1f),
        alpha = alpha
    )
}

fun Color.darken(factor: Float): Color {
    return Color(
        red = (red * (1 - factor)).coerceIn(0f, 1f),
        green = (green * (1 - factor)).coerceIn(0f, 1f),
        blue = (blue * (1 - factor)).coerceIn(0f, 1f),
        alpha = alpha
    )
}

// ═══════════════════════════════════════════════════════════════
// Extensiones de String
// ═══════════════════════════════════════════════════════════════

fun String.capitalizeWords(): String {
    return split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
}

fun String.truncate(maxLength: Int, suffix: String = "..."): String {
    return if (length > maxLength) take(maxLength - suffix.length) + suffix else this
}

fun String.toSlug(): String {
    return lowercase()
        .replace(Regex("[^a-z0-9\\s-]"), "")
        .replace(Regex("\\s+"), "-")
        .replace(Regex("-+$"), "")
}

fun String.isValidEmail(): Boolean {
    return matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
}

fun String.maskEmail(): String {
    val parts = split("@")
    if (parts.size != 2) return this
    val local = parts[0]
    val domain = parts[1]
    val maskedLocal = if (local.length > 2) {
        local.take(2) + "*".repeat(local.length - 2)
    } else {
        "*".repeat(local.length)
    }
    return "$maskedLocal@$domain"
}

// ═══════════════════════════════════════════════════════════════
// Extensiones de Number
// ═══════════════════════════════════════════════════════════════

fun Int.toFormattedString(): String {
    return String.format("%,d", this)
}

fun Long.toFormattedString(): String {
    return String.format("%,d", this)
}

fun Float.roundTo(decimals: Int): Float {
    val factor = Math.pow(10.0, decimals.toDouble()).toFloat()
    return (this * factor).roundToInt() / factor
}

fun Double.roundTo(decimals: Int): Double {
    val factor = Math.pow(10.0, decimals.toDouble())
    return (this * factor).roundToInt() / factor
}

fun Int.percentageOf(total: Int): Int {
    return if (total > 0) ((this.toFloat() / total) * 100).roundToInt() else 0
}

fun Long.toTimeString(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val seconds = this % 60
    return when {
        hours > 0 -> String.format("%d:%02d:%02d", hours, minutes, seconds)
        else -> String.format("%02d:%02d", minutes, seconds)
    }
}

// ═══════════════════════════════════════════════════════════════
// Extensiones de List
// ═══════════════════════════════════════════════════════════════

fun <T> List<T>.shuffle(): List<T> = this.shuffled()

fun <T> List<T>.takeRandom(count: Int): List<T> = shuffled().take(count)

fun <T> List<T>.chunkedSafe(size: Int): List<List<T>> {
    return if (size > 0) chunked(size) else listOf(this)
}

inline fun <T, R> List<T>.mapIndexedNotNull(transform: (Int, T) -> R?): List<R> {
    return mapIndexedNotNullTo(ArrayList(), transform)
}

fun <T> List<T>.toCsv(): String {
    return joinToString(",") { it.toString() }
}

// ═══════════════════════════════════════════════════════════════
// Extensiones de Map
// ═══════════════════════════════════════════════════════════════

fun <K, V> Map<K, V>.getOrDefault(key: K, default: V): V {
    return get(key) ?: default
}

inline fun <K, V> Map<K, V>.mapValuesNotNull(transform: (Map.Entry<K, V>) -> V?): Map<K, V> {
    return mapNotNull { (key, value) ->
        transform(entries.find { it.key == key }!!)?.let { key to it }
    }.toMap()
}

// ═══════════════════════════════════════════════════════════════
// Extensiones de Result
// ═══════════════════════════════════════════════════════════════

inline fun <T, R> Result<T>.flatMap(transform: (T) -> Result<R>): Result<R> {
    return fold(
        onSuccess = { transform(it) },
        onFailure = { Result.failure(it) }
    )
}

inline fun <T> Result<T>.onFailureReturn(action: (Throwable) -> T): Result<T> {
    return fold(
        onSuccess = { Result.success(it) },
        onFailure = { Result.success(action(it)) }
    )
}

// ═══════════════════════════════════════════════════════════════
// Extensiones de Throwable
// ═══════════════════════════════════════════════════════════════

fun Throwable.getRootCause(): Throwable {
    var cause = this
    while (cause.cause != null && cause.cause != cause) {
        cause = cause.cause!!
    }
    return cause
}

fun Throwable.toUserMessage(): String {
    return when (this) {
        is java.net.UnknownHostException -> "No hay conexión a internet"
        is java.net.SocketTimeoutException -> "La conexión tardó demasiado"
        is java.io.IOException -> "Error de red. Intenta de nuevo"
        is IllegalStateException -> "Error interno de la aplicación"
        is IllegalArgumentException -> "Datos inválidos proporcionados"
        else -> message ?: "Ha ocurrido un error inesperado"
    }
}

// ═══════════════════════════════════════════════════════════════
// Safe Operations
// ═══════════════════════════════════════════════════════════════

inline fun <T> safeCall(default: T, block: () -> T): T {
    return try {
        block()
    } catch (e: Exception) {
        default
    }
}

inline fun <T> safeNullableCall(block: () -> T?): T? {
    return try {
        block()
    } catch (e: Exception) {
        null
    }
}

inline fun safeUnitCall(block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        // Ignorar
    }
}

// ═══════════════════════════════════════════════════════════════
// Scope Functions útiles
// ═══════════════════════════════════════════════════════════════

inline fun <T> T?.ifNull(block: () -> T): T = this ?: block()

inline fun <T> T.ifTrue(condition: Boolean, block: T.() -> T): T {
    return if (condition) block() else this
}

inline fun <T> T.letIf(condition: Boolean, block: (T) -> T): T {
    return if (condition) block(this) else this
}
