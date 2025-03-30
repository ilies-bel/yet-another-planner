package com.iliesbel.yapbackend.domain.tasks.service

import com.iliesbel.yapbackend.domain.tasks.service.model.Difficulty
import java.time.LocalDateTime

data class TaskCreation(
    val name: String,
    val difficulty: Difficulty,
    val description: String?,
    val project: String?,
    val dueDate: LocalDateTime?,
    val contextName: String?,
    val userEmail: String,
    val url: String?,
)