package com.iliesbel.yapbackend.domain.users.persistence

import com.iliesbel.yapbackend.domain.users.domain.Device
import com.iliesbel.yapbackend.infra.authentication.AuthenticationService
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
class DeviceRepository(private val deviceJpaRepository: DeviceJpaRepository) {

    fun findDevicesForCurrentUser(): List<Device> {
        val account = AuthenticationService.getAccountFromContext()
        return deviceJpaRepository.findByUserEmail(account.email)
            .map(DeviceEntity::toDomain)
    }
    
    fun updateDeviceLastUsedAt(deviceId: UUID) {
        deviceJpaRepository.updateLastUsedAt(deviceId, LocalDateTime.now())
    }
    
    fun findDeviceForCurrentUser(deviceId: UUID): DeviceEntity? {
        val account = AuthenticationService.getAccountFromContext()
        return deviceJpaRepository.findByIdAndUserEmail(deviceId, account.email)
    }
}

fun DeviceEntity.toDomain(): Device {
    return Device(
        id = id,
        name = name,
        type = type,
        platform = platform,
        lastPlatformVersion = lastPlatformVersion,
        browser = browser,
        lastUsedAt = lastUsedAt
    )
}