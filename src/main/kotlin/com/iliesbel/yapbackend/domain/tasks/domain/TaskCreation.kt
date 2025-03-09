package com.iliesbel.yapbackend.domain.tasks.domain

import com.iliesbel.yapbackend.domain.tasks.domain.model.Difficulty
import java.time.LocalDateTime

data class TaskCreation(
    val name: String,
    val difficulty: Difficulty,
    val description: String?,
    val project: String?,
    val dueDate: LocalDateTime?,
    val contextName: String?,
    val userEmail: String,
)