package com.iliesbel.yapbackend.domain.tasks.service

import com.iliesbel.yapbackend.domain.contexts.domain.DayPeriod
import com.iliesbel.yapbackend.domain.tasks.service.model.Difficulty
import com.iliesbel.yapbackend.domain.tasks.service.model.TaskStatus
import java.time.LocalDateTime

data class TaskUpdate(
    val name: String?,
    val description: String?,
    val status: TaskStatus?,
    val difficulty: Difficulty?,
    val project: String?,
    val dueDate: LocalDateTime?,
    val contextName: String?,
    val url: String?,
    val timeContext: DayPeriod?
)