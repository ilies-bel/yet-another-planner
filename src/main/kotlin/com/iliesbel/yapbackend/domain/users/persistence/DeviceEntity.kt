package com.iliesbel.yapbackend.domain.users.persistence

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import java.time.LocalDateTime
import java.util.*

@Entity(name = "device")
class DeviceEntity(
    @Id
    val id: UUID,
    val name: String,
    val type: String,
    val platform: String,
    val lastPlatformVersion: String,
    val browser: String,
    @ManyToOne
    val user: UserEntity,
    var lastUsedAt: LocalDateTime = LocalDateTime.now()
)