package com.iliesbel.yapbackend.domain.users.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime
import java.util.*

interface DeviceJpaRepository : JpaRepository<DeviceEntity, UUID> {
    fun findByUserEmail(email: String): List<DeviceEntity>
    
    fun findByIdAndUserEmail(id: UUID, email: String): DeviceEntity?
    
    @Modifying
    @Query("UPDATE device d SET d.lastUsedAt = :lastUsedAt WHERE d.id = :deviceId")
    fun updateLastUsedAt(@Param("deviceId") deviceId: UUID, @Param("lastUsedAt") lastUsedAt: LocalDateTime)
}