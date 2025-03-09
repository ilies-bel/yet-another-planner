package com.iliesbel.yapbackend.domain.tasks.domain

import com.iliesbel.yapbackend.domain.tasks.domain.model.Task
import com.iliesbel.yapbackend.domain.tasks.persistence.TaskRepository
import com.iliesbel.yapbackend.domain.tasks.presentation.TaskFilter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TaskService(
    val taskRepository: TaskRepository,
) {
    @Transactional(readOnly = true)
    fun findAll(filters: TaskFilter): List<Task> {
        return taskRepository.findAll(filters)
    }

    @Transactional
    fun create(task: TaskCreation) {
        taskRepository.save(task)
    }
}
