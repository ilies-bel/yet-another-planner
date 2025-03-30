package com.iliesbel.yapbackend.domain.tasks.service.model

import java.time.LocalDateTime

data class Task(
    val id: Long,
    val name: String,
    val description: String?,
    val status: TaskStatus,
    val difficulty: String?,
    val context: TaskContext?,
    val projectName: String?,
    val dueDate: LocalDateTime?,
)

