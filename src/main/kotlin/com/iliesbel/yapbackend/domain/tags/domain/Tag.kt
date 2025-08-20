package com.iliesbel.yapbackend.domain.tags.domain

import java.time.LocalDateTime

data class Tag(
    val id: Long? = null,
    val userId: Long,
    val name: String,
    val color: String = "#808080",
    val description: String? = null,
    val usageCount: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val isArchived: Boolean = false
)