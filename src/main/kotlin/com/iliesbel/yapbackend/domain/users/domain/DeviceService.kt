package com.iliesbel.yapbackend.domain.users.domain

import com.iliesbel.yapbackend.domain.users.persistence.DeviceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeviceService(private val deviceRepository: DeviceRepository) {
    
    @Transactional(readOnly = true)
    fun getCurrentUserDevices(): List<Device> {
        return deviceRepository.findDevicesForCurrentUser()
    }
}