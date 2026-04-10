package com.LBs.EEDA.worker

import android.content.Context
import androidx.work.*
import com.LBs.EEDA.domain.repository.ChildProfileRepository
import com.LBs.EEDA.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * Workers de sincronización y mantenimiento en background
 */

/**
 * Worker de sincronización de progreso
 */
class SyncWorker(
    context: Context,
    params: WorkerParameters,
    private val repository: ChildProfileRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            Logger.i(Logger.Category.DATA, "Starting sync worker")

            // Sincronizar datos locales
            withContext(Dispatchers.IO) {
                repository.getProfile().collect { result ->
                    result.onSuccess { profile ->
                        profile?.let {
                            // Actualizar estadísticas
                            Logger.logEvent(Logger.Category.ANALYTICS, "sync_completed",
                                mapOf("level" to it.currentLevel, "xp" to it.totalXp))
                        }
                    }
                }
            }

            Result.success()
        } catch (e: Exception) {
            Logger.e(Logger.Category.DATA, "Sync failed", e)
            Result.retry()
        }
    }
}

/**
 * Worker de limpieza de datos antiguos
 */
class CleanupWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            Logger.i(Logger.Category.DATA, "Starting cleanup worker")

            withContext(Dispatchers.IO) {
                // Limpiar logs antiguos
                val prefs = applicationContext.getSharedPreferences("logs", Context.MODE_PRIVATE)
                // Implementar limpieza
            }

            Result.success()
        } catch (e: Exception) {
            Logger.e(Logger.Category.DATA, "Cleanup failed", e)
            Result.failure()
        }
    }
}

/**
 * Worker de respaldo de datos
 */
class BackupWorker(
    context: Context,
    params: WorkerParameters,
    private val repository: ChildProfileRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            Logger.i(Logger.Category.DATA, "Starting backup worker")

            withContext(Dispatchers.IO) {
                repository.getProfile().collect { result ->
                    result.onSuccess { profile ->
                        profile?.let {
                            // Crear respaldo en SharedPreferences o archivo
                            val backupPrefs = applicationContext.getSharedPreferences(
                                "backup_${System.currentTimeMillis()}",
                                Context.MODE_PRIVATE
                            )
                            // Guardar datos
                        }
                    }
                }
            }

            Result.success()
        } catch (e: Exception) {
            Logger.e(Logger.Category.DATA, "Backup failed", e)
            Result.retry()
        }
    }
}

/**
 * Scheduler de workers
 */
object WorkScheduler {

    fun scheduleSyncWork(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "sync_work",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }

    fun scheduleCleanupWork(context: Context) {
        val cleanupRequest = PeriodicWorkRequestBuilder<CleanupWorker>(24, TimeUnit.HOURS)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "cleanup_work",
            ExistingPeriodicWorkPolicy.KEEP,
            cleanupRequest
        )
    }

    fun scheduleBackupWork(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiresStorageNotLow(true)
            .build()

        val backupRequest = PeriodicWorkRequestBuilder<BackupWorker>(12, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "backup_work",
            ExistingPeriodicWorkPolicy.KEEP,
            backupRequest
        )
    }

    fun cancelAllWork(context: Context) {
        WorkManager.getInstance(context).cancelAllWork()
    }

    fun getWorkStatus(context: Context, workName: String) {
        WorkManager.getInstance(context).getWorkInfosForUniqueWorkLiveData(workName)
    }
}
