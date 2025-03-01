package com.iliesbel.yapbackend.domain.tasks.domain.model

import java.time.LocalDateTime

data class Task(
    val name: String,
    val description: String,
    val status: TaskStatus,
    val difficulty: Difficulty,
    val context: TaskContext,
    val projectName: String,
    val dueDate: LocalDateTime?,
    )

class TaskContext (
    val name: String,
)
