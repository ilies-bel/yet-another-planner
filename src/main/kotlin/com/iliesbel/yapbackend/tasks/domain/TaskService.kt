package com.iliesbel.yapbackend.tasks.domain

import com.iliesbel.yapbackend.tasks.persistence.TaskRepository
import org.springframework.stereotype.Service

@Service
class TaskService(val taskRepository: TaskRepository) {
    fun findAll(): List<Task> {
        return taskRepository.findAll()
    }

    fun create(task: Task) {
        TODO("Not yet implemented")
    }
}
