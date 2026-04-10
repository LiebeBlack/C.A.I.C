package com.LBs.EEDA.data.repository

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.view.WindowManager
import com.LBs.EEDA.domain.model.HardwareProfile
import com.LBs.EEDA.domain.model.PerformanceTier
import com.LBs.EEDA.domain.repository.HardwareRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HardwareRepositoryImpl(private val context: Context) : HardwareRepository {

    private val _hardwareProfile = MutableStateFlow(detectHardware())
    override val hardwareProfile: StateFlow<HardwareProfile> = _hardwareProfile.asStateFlow()

    override fun refreshProfile() {
        _hardwareProfile.value = detectHardware()
    }

    private fun detectHardware(): HardwareProfile {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        // NO usar context.display - requiere Activity context en API 30+
        // Usar valores por defecto seguros basados en RAM y API level
        val refreshRate = detectRefreshRateSafely()
        val totalRamGb = memoryInfo.totalMem / (1024 * 1024 * 1024.0)
        val isLowRam = memoryInfo.lowMemory || totalRamGb < 4

        val tier = when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q || isLowRam -> PerformanceTier.LOW
            refreshRate > 60f -> PerformanceTier.HIGH
            else -> PerformanceTier.MEDIUM
        }

        val targetFps = when (tier) {
            PerformanceTier.LOW -> 30
            PerformanceTier.MEDIUM -> 60
            PerformanceTier.HIGH -> refreshRate.toInt()
        }

        return HardwareProfile(
            tier = tier,
            maxRefreshRate = refreshRate,
            targetFps = targetFps,
            lowPowerMode = isLowRam,
            canHandleHighGraphics = tier != PerformanceTier.LOW
        )
    }

    private fun detectRefreshRateSafely(): Float {
        return try {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // En API 30+, windowManager.defaultDisplay está deprecado
                // y context.display falla con Application context
                // Usar valor por defecto basado en API level
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> 120f // Android 12+ soporta high refresh
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> 90f  // Android 11
                    else -> 60f
                }
            } else {
                @Suppress("DEPRECATION")
                windowManager.defaultDisplay?.refreshRate ?: 60f
            }
        } catch (e: Exception) {
            // Fallback seguro
            60f
        }
    }
}
