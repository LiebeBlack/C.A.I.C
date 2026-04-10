package com.LBs.EEDA.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.LBs.EEDA.MainActivity
import com.LBs.EEDA.R

/**
 * Sistema de Notificaciones E.E.D.A
 * Maneja recordatorios, logros y actualizaciones
 */
class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_REMINDERS = "eeda_reminders"
        const val CHANNEL_ACHIEVEMENTS = "eeda_achievements"
        const val CHANNEL_STREAKS = "eeda_streaks"
        const val CHANNEL_PARENTAL = "eeda_parental"

        const val NOTIFICATION_ID_DAILY = 1001
        const val NOTIFICATION_ID_STREAK = 1002
        const val NOTIFICATION_ID_ACHIEVEMENT = 1003
        const val NOTIFICATION_ID_PARENT = 1004
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    CHANNEL_REMINDERS,
                    "Recordatorios de Estudio",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Recordatorios para mantener tu racha de aprendizaje"
                },
                NotificationChannel(
                    CHANNEL_ACHIEVEMENTS,
                    "Logros y Recompensas",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Notificaciones cuando desbloqueas logros"
                },
                NotificationChannel(
                    CHANNEL_STREAKS,
                    "Racha de Aprendizaje",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Alertas importantes sobre tu racha diaria"
                },
                NotificationChannel(
                    CHANNEL_PARENTAL,
                    "Modo Acompañante",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Resúmenes y alertas para padres/tutores"
                }
            )

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannels(channels)
        }
    }

    /**
     * Notificación diaria de recordatorio
     */
    fun showDailyReminder(streakDays: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("destination", "lessons")
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_REMINDERS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("¡Hora de aprender! 🎓")
            .setContentText(DateUtils.getStreakMessage(streakDays))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(
                R.drawable.ic_launcher_foreground,
                "Comenzar",
                pendingIntent
            )
            .build()

        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID_DAILY, notification)
        }
    }

    /**
     * Notificación de racha en riesgo
     */
    fun showStreakAtRisk(hoursRemaining: Int) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_STREAKS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("⚠️ ¡Tu racha está en riesgo!")
            .setContentText("Quedan $hoursRemaining horas para mantener tu racha de aprendizaje")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()

        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID_STREAK, notification)
        }
    }

    /**
     * Notificación de logro desbloqueado
     */
    fun showAchievementUnlocked(achievementName: String, xpReward: Long) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ACHIEVEMENTS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🏆 ¡Logro Desbloqueado!")
            .setContentText("$achievementName - +$xpReward XP")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .build()

        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID_ACHIEVEMENT + achievementName.hashCode(), notification)
        }
    }

    /**
     * Notificación de nivel subido
     */
    fun showLevelUp(newLevel: Int, tier: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ACHIEVEMENTS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("⭐ ¡Subiste de Nivel!")
            .setContentText("Nivel $newLevel - $tier")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID_ACHIEVEMENT + newLevel, notification)
        }
    }

    /**
     * Notificación semanal para padres
     */
    fun showWeeklyParentReport(childName: String, stats: ParentWeeklyStats) {
        val notification = NotificationCompat.Builder(context, CHANNEL_PARENTAL)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Resumen Semanal - $childName")
            .setContentText("${stats.lessonsCompleted} lecciones, ${stats.timeSpentMinutes} minutos")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("""
                    Lecciones completadas: ${stats.lessonsCompleted}
                    Tiempo de aprendizaje: ${DateUtils.formatDurationLong(stats.timeSpentMinutes)}
                    Habilidades desbloqueadas: ${stats.skillsUnlocked}
                    Evaluaciones aprobadas: ${stats.assessmentsPassed}
                """.trimIndent()))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID_PARENT, notification)
        }
    }

    /**
     * Notificación de certificado obtenido
     */
    fun showCertificationEarned(certificationName: String, tier: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ACHIEVEMENTS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🎓 ¡Certificación Obtenida!")
            .setContentText("$certificationName ($tier)")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID_ACHIEVEMENT + certificationName.hashCode(), notification)
        }
    }

    /**
     * Cancelar todas las notificaciones
     */
    fun cancelAllNotifications() {
        NotificationManagerCompat.from(context).cancelAll()
    }

    /**
     * Cancelar notificación específica
     */
    fun cancelNotification(id: Int) {
        NotificationManagerCompat.from(context).cancel(id)
    }
}

data class ParentWeeklyStats(
    val lessonsCompleted: Int,
    val timeSpentMinutes: Int,
    val skillsUnlocked: Int,
    val assessmentsPassed: Int,
    val averageScore: Float,
    val streakDays: Int
)
