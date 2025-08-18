package com.iliesbel.yapbackend.domain.tasks.service

import com.iliesbel.yapbackend.domain.tasks.persistence.TaskRepository
import com.iliesbel.yapbackend.domain.tasks.presentation.TaskPageFilter
import com.iliesbel.yapbackend.domain.tasks.service.model.Task
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TaskService(
    val taskRepository: TaskRepository,
) {
    @Transactional(readOnly = true)
    fun findAll(filters: TaskPageFilter): Page<Task> {
        return taskRepository.findAll(filters)
    }

    @Transactional
    fun create(task: TaskCreation) {
        taskRepository.save(task)
    }

    @Transactional
    fun update(id: Long, taskUpdate: TaskUpdate): Task {
        return taskRepository.update(id, taskUpdate)
    }
    
    @Transactional(readOnly = true)
    fun findBySourceUrl(sourceUrl: String): Task? {
        return taskRepository.findBySourceUrl(sourceUrl)
    }
}
