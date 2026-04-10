package com.LBs.EEDA.util

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Utilidades de fecha y tiempo para E.E.D.A
 */
object DateUtils {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    fun getCurrentTimestamp(): Long = System.currentTimeMillis()

    fun formatDate(timestamp: Long): String = dateFormat.format(Date(timestamp))

    fun formatDateTime(timestamp: Long): String = dateTimeFormat.format(Date(timestamp))

    fun formatDisplayDate(timestamp: Long): String = displayDateFormat.format(Date(timestamp))

    fun formatTime(timestamp: Long): String = timeFormat.format(Date(timestamp))

    fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = timestamp1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = timestamp2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    fun isToday(timestamp: Long): Boolean = isSameDay(timestamp, getCurrentTimestamp())

    fun isYesterday(timestamp: Long): Boolean {
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
        return yesterday.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
               yesterday.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR)
    }

    fun getDaysDifference(timestamp1: Long, timestamp2: Long): Int {
        val diff = Math.abs(timestamp2 - timestamp1)
        return TimeUnit.MILLISECONDS.toDays(diff).toInt()
    }

    fun getRelativeTimeString(timestamp: Long): String {
        val now = getCurrentTimestamp()
        val diff = now - timestamp
        val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        val days = TimeUnit.MILLISECONDS.toDays(diff)

        return when {
            seconds < 60 -> "Hace un momento"
            minutes < 60 -> "Hace $minutes minutos"
            hours < 24 -> "Hace $hours horas"
            days == 1L -> "Ayer"
            days < 7 -> "Hace $days días"
            days < 30 -> "Hace ${days / 7} semanas"
            else -> formatDisplayDate(timestamp)
        }
    }

    fun getStreakMessage(streakDays: Int): String {
        return when (streakDays) {
            0 -> "¡Empieza tu racha hoy!"
            1 -> "¡Primer día! Sigue así"
            in 2..6 -> "¡Racha de $streakDays días! 🔥"
            7 -> "¡Semana perfecta! 🌟"
            in 8..29 -> "¡Racha de $streakDays días! Impresionante 🔥🔥"
            30 -> "¡Mes completo! Eres una leyenda 🏆"
            else -> "¡Racha de $streakDays días! Increíble 🔥🔥🔥"
        }
    }

    fun getNextMilestoneDays(currentStreak: Int): Int {
        return when {
            currentStreak < 7 -> 7 - currentStreak
            currentStreak < 30 -> 30 - currentStreak
            currentStreak < 100 -> 100 - currentStreak
            else -> 365 - (currentStreak % 365)
        }
    }

    fun formatDuration(minutes: Int): String {
        return when {
            minutes < 60 -> "$minutes min"
            minutes == 60 -> "1 hora"
            minutes < 120 -> "1 hora ${minutes - 60} min"
            else -> "${minutes / 60} horas"
        }
    }

    fun formatDurationLong(totalMinutes: Int): String {
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return buildString {
            if (hours > 0) append("$hours ${if (hours == 1) "hora" else "horas"}")
            if (minutes > 0) {
                if (hours > 0) append(" ")
                append("$minutes ${if (minutes == 1) "minuto" else "minutos"}")
            }
        }
    }

    fun getStartOfDay(timestamp: Long): Long {
        val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun getEndOfDay(timestamp: Long): Long {
        val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.timeInMillis
    }

    fun addDays(timestamp: Long, days: Int): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            add(Calendar.DAY_OF_YEAR, days)
        }.timeInMillis
    }

    fun getDayOfWeek(timestamp: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
        return when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> "Domingo"
            Calendar.MONDAY -> "Lunes"
            Calendar.TUESDAY -> "Martes"
            Calendar.WEDNESDAY -> "Miércoles"
            Calendar.THURSDAY -> "Jueves"
            Calendar.FRIDAY -> "Viernes"
            Calendar.SATURDAY -> "Sábado"
            else -> ""
        }
    }

    fun getMonthName(timestamp: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
        val months = arrayOf(
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        )
        return months[cal.get(Calendar.MONTH)]
    }

    fun isWeekend(timestamp: Long): Boolean {
        val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
        val day = cal.get(Calendar.DAY_OF_WEEK)
        return day == Calendar.SATURDAY || day == Calendar.SUNDAY
    }

    fun getAgeGroup(age: Int): String {
        return when (age) {
            in 3..7 -> "Infantil (3-7)"
            in 8..14 -> "Pre-Adolescente (8-14)"
            in 15..20 -> "Adolescente (15-20)"
            else -> "Adulto (21+)"
        }
    }
}
