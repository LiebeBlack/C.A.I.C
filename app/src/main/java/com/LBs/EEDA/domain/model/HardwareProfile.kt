package com.LBs.EEDA.domain.model

enum class PerformanceTier {
    LOW,    // Legacy (Android 7/8, low RAM) -> 30 FPS
    MEDIUM, // Standard -> 60 FPS V-Sync
    HIGH    // Pro (LTPO/120Hz+) -> Max Refresh Rate
}

data class HardwareProfile(
    val tier: PerformanceTier,
    val maxRefreshRate: Float,
    val targetFps: Int,
    val lowPowerMode: Boolean,
    val canHandleHighGraphics: Boolean
)
