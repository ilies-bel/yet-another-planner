package com.iliesbel.yapbackend.domain.users.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface DeviceJpaRepository : JpaRepository<DeviceEntity, UUID> {
    fun findByUserEmail(email: String): List<DeviceEntity>
}