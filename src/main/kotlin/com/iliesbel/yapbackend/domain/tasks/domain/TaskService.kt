package com.iliesbel.yapbackend.domain.tasks.domain

import com.iliesbel.yapbackend.domain.tasks.domain.model.Task
import com.iliesbel.yapbackend.domain.tasks.persistence.TaskRepository
import com.iliesbel.yapbackend.domain.tasks.presentation.TaskFilter
import org.springframework.stereotype.Service

@Service
class TaskService(val taskRepository: TaskRepository) {
    fun findAll(filters: TaskFilter): List<Task> {
        return taskRepository.findAll(filters)
    }

    fun create(task: Task) {
        TODO("Not yet implemented")
    }
}
