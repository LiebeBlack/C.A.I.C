package com.LBs.EEDA.domain.repository

import com.LBs.EEDA.domain.model.HardwareProfile
import kotlinx.coroutines.flow.StateFlow

interface HardwareRepository {
    val hardwareProfile: StateFlow<HardwareProfile>
    fun refreshProfile()
}
