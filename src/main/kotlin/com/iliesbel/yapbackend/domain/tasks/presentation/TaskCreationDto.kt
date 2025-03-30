package com.iliesbel.yapbackend.domain.tasks.presentation

import com.iliesbel.yapbackend.domain.tasks.service.TaskCreation
import com.iliesbel.yapbackend.domain.tasks.service.model.Difficulty
import com.iliesbel.yapbackend.domain.tasks.service.model.TaskContext
import com.iliesbel.yapbackend.infra.authentication.persistence.AccountEntity
import java.time.LocalDateTime

data class TaskCreationDto(
    val name: String,
    val description: String?,
    val difficultyScore: Long,
    val context: TaskContext?,
    val projectName: String?,
    val dueDate: LocalDateTime?,
    val url: String?,
) {
    fun toTaskCreation(currentUser: AccountEntity): TaskCreation {
        return TaskCreation(
            name = name,
            description = description,
            difficulty = Difficulty.fromScore(difficultyScore),
            contextName = context?.name,
            project = projectName,
            dueDate = dueDate,
            userEmail = currentUser.email,
            url = url,
        )
    }
}