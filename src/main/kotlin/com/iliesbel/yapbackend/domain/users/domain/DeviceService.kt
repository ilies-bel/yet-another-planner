package com.iliesbel.yapbackend.domain.users.domain

import com.iliesbel.yapbackend.domain.users.persistence.DeviceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class DeviceService(private val deviceRepository: DeviceRepository) {
    
    @Transactional(readOnly = true)
    fun getCurrentUserDevices(): List<Device> {
        return deviceRepository.findDevicesForCurrentUser()
    }
    
    @Transactional
    fun updateDeviceLastUsedAt(deviceId: UUID) {
        // Check if device exists for current user
        val device = deviceRepository.findDeviceForCurrentUser(deviceId)
        if (device != null) {
            deviceRepository.updateDeviceLastUsedAt(deviceId)
        }
    }
    
    @Transactional
    fun deleteDevice(deviceId: String) {
        val uuid = UUID.fromString(deviceId)
        val device = deviceRepository.findDeviceForCurrentUser(uuid)
        if (device != null) {
            deviceRepository.deleteDevice(uuid)
        }
    }
}