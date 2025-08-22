package com.iliesbel.yapbackend.domain.tasks.service.model

import com.iliesbel.yapbackend.domain.contexts.domain.DayPeriod
import com.iliesbel.yapbackend.domain.tags.domain.Tag
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
    val timeContext: DayPeriod?,
    val url: String? = null,
    val sourceUrl: String? = null,
    val createdAt: LocalDateTime? = null,
    val tags: List<Tag> = emptyList()
)

