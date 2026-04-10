package com.LBs.EEDA.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.LBs.EEDA.domain.model.HardwareProfile
import com.LBs.EEDA.domain.model.PerformanceTier

val LocalHardwareProfile = staticCompositionLocalOf<HardwareProfile> {
    HardwareProfile(
        tier = PerformanceTier.MEDIUM,
        maxRefreshRate = 60f,
        targetFps = 60,
        lowPowerMode = false,
        canHandleHighGraphics = true
    )
}

@Composable
fun HardwareSensingProvider(
    profile: HardwareProfile,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalHardwareProfile provides profile) {
        content()
    }
}
