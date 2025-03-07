package com.iliesbel.yapbackend.domain.tasks.presentation

import com.iliesbel.yapbackend.domain.tasks.domain.model.Difficulty
import com.iliesbel.yapbackend.domain.tasks.domain.model.TaskContext
import com.iliesbel.yapbackend.infra.authentication.persistence.UserEntity
import java.time.LocalDateTime

data class TaskCreationDto(
    val name: String,
    val description: String?,
    val difficultyScore: Long,
    val context: TaskContext?,
    val projectName: String?,
    val dueDate: LocalDateTime?,
) {
    fun toTaskCreation(currentUser: UserEntity): TaskCreation {
        return TaskCreation(
            name = name,
            description = description,
            difficulty = Difficulty.fromScore(difficultyScore),
            contextName = context?.name,
            project = projectName,
            dueDate = dueDate,
            userEmail = currentUser.email
        )
    }
}