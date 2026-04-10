package com.LBs.EEDA.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.AlarmManagerCompat
import com.LBs.EEDA.domain.model.DigitalPhase
import com.LBs.EEDA.domain.repository.ChildProfileRepository
import com.LBs.EEDA.util.DateUtils
import com.LBs.EEDA.util.Logger
import com.LBs.EEDA.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * Servicio de recordatorios inteligentes de aprendizaje E.E.D.A
 * Maneja alarmas, rachas y notificaciones contextuales
 */
class LearningReminderService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var repository: ChildProfileRepository
    private lateinit var notificationHelper: NotificationHelper

    override fun onCreate() {
        super.onCreate()
        Logger.i(Logger.Category.LIFECYCLE, "LearningReminderService creado")
        notificationHelper = NotificationHelper(this)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_CHECK_STREAK -> checkAndNotifyStreak()
            ACTION_DAILY_REMINDER -> sendDailyReminder()
            ACTION_WEEKLY_REPORT -> sendWeeklyReport()
            ACTION_SCHEDULE_ALARMS -> scheduleAllAlarms()
        }
        return START_STICKY
    }

    /**
     * Verifica la racha y envía notificaciones si es necesario
     */
    private fun checkAndNotifyStreak() {
        serviceScope.launch {
            try {
                val profile = repository.getChildProfile().firstOrNull()
                profile?.let {
                    val lastActive = DateUtils.getCurrentTimestamp() - (it.streakDays * 86400000)
                    val hoursSinceActive = (DateUtils.getCurrentTimestamp() - lastActive) / 3600000

                    when {
                        // Racha a punto de perderse (menos de 3 horas)
                        hoursSinceActive >= 21 -> {
                            notificationHelper.showStreakAtRisk((24 - hoursSinceActive).toInt())
                            Logger.i(Logger.Category.ANALYTICS, "Streak at risk notification sent")
                        }
                        // Recordatorio de mantener racha (mediodía)
                        hoursSinceActive >= 12 && hoursSinceActive < 13 -> {
                            notificationHelper.showDailyReminder(it.streakDays)
                        }
                    }
                }
            } catch (e: Exception) {
                Logger.e(Logger.Category.DATA, "Error checking streak", e)
            }
        }
    }

    /**
     * Envía recordatorio diario personalizado
     */
    private fun sendDailyReminder() {
        serviceScope.launch {
            try {
                val profile = repository.getChildProfile().firstOrNull()
                profile?.let {
                    // Personalizar según fase
                    val message = when (DigitalPhase.fromAge(it.age)) {
                        DigitalPhase.SENSORIAL -> "¡Hora de jugar y aprender algo nuevo! 🎨"
                        DigitalPhase.CREATIVE -> "¿Listo para tu próximo desafío digital? 🚀"
                        DigitalPhase.PROFESSIONAL -> "Tu sesión de aprendizaje te espera 📚"
                        DigitalPhase.INNOVATOR -> "Nuevos conceptos de vanguardia disponibles 🧠"
                    }

                    notificationHelper.showDailyReminder(it.streakDays)
                    Logger.logEvent(Logger.Category.ANALYTICS, "daily_reminder_sent",
                        mapOf("streak" to it.streakDays, "age" to it.age))
                }
            } catch (e: Exception) {
                Logger.e(Logger.Category.DATA, "Error sending daily reminder", e)
            }
        }
    }

    /**
     * Envía reporte semanal
     */
    private fun sendWeeklyReport() {
        // Implementar lógica de reporte semanal
        Logger.i(Logger.Category.ANALYTICS, "Weekly report triggered")
    }

    /**
     * Programa todas las alarmas recurrentes
     */
    private fun scheduleAllAlarms() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Alarma de racha (cada hora después de las 6 PM)
        scheduleRepeatingAlarm(alarmManager, ACTION_CHECK_STREAK, 18, 0, 3600000)

        // Alarma diaria (9 AM)
        scheduleDailyAlarm(alarmManager, ACTION_DAILY_REMINDER, 9, 0)

        // Alarma semanal (Domingos 8 PM)
        scheduleWeeklyAlarm(alarmManager, ACTION_WEEKLY_REPORT, Calendar.SUNDAY, 20, 0)

        Logger.i(Logger.Category.LIFECYCLE, "All alarms scheduled")
    }

    private fun scheduleRepeatingAlarm(
        alarmManager: AlarmManager,
        action: String,
        hour: Int,
        minute: Int,
        interval: Long
    ) {
        val intent = Intent(this, ReminderBroadcastReceiver::class.java).apply {
            this.action = action
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this, action.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    private fun scheduleDailyAlarm(alarmManager: AlarmManager, action: String, hour: Int, minute: Int) {
        val intent = Intent(this, ReminderBroadcastReceiver::class.java).apply {
            this.action = action
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this, action.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun scheduleWeeklyAlarm(
        alarmManager: AlarmManager,
        action: String,
        dayOfWeek: Int,
        hour: Int,
        minute: Int
    ) {
        val intent = Intent(this, ReminderBroadcastReceiver::class.java).apply {
            this.action = action
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this, action.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, dayOfWeek)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.WEEK_OF_YEAR, 1)
            }
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY * 7,
            pendingIntent
        )
    }

    companion object {
        const val ACTION_CHECK_STREAK = "com.LBs.EEDA.CHECK_STREAK"
        const val ACTION_DAILY_REMINDER = "com.LBs.EEDA.DAILY_REMINDER"
        const val ACTION_WEEKLY_REPORT = "com.LBs.EEDA.WEEKLY_REPORT"
        const val ACTION_SCHEDULE_ALARMS = "com.LBs.EEDA.SCHEDULE_ALARMS"

        fun startService(context: Context) {
            val intent = Intent(context, LearningReminderService::class.java).apply {
                action = ACTION_SCHEDULE_ALARMS
            }
            context.startService(intent)
        }
    }
}

/**
 * BroadcastReceiver para recibir alarmas
 */
class ReminderBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val serviceIntent = Intent(context, LearningReminderService::class.java).apply {
            action = intent.action
        }
        context.startService(serviceIntent)
    }
}
