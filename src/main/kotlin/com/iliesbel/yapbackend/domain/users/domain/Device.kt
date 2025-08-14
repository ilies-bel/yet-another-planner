package com.iliesbel.yapbackend.domain.users.domain

import java.time.LocalDateTime
import java.util.*

data class Device(
    val id: UUID,
    val name: String,
    val type: String,
    val platform: String,
    val lastPlatformVersion: String,
    val browser: String,
    val lastUsedAt: LocalDateTime,
)