package com.iliesbel.yapbackend.domain.tasks.presentation

import com.iliesbel.yapbackend.domain.tasks.domain.TaskService
import com.iliesbel.yapbackend.domain.tasks.domain.model.Task
import com.iliesbel.yapbackend.domain.tasks.domain.model.TaskStatus
import com.iliesbel.yapbackend.infra.authentication.AuthenticationService
import org.springframework.web.bind.annotation.*

@RestController
class TaskController(private val taskService: TaskService) {
    @GetMapping("/tasks")
    fun getTasksForCurrentUser(
        filters: TaskFilter
    ): List<Task> {
        return taskService.findAll(filters)
    }

    @PostMapping("/tasks")
    fun addTask(@RequestBody task: TaskCreationDto) {
        val currentUser = AuthenticationService.getAccountFromContext()

        return taskService.create(task.toTaskCreation(currentUser))
    }

    @PatchMapping("/tasks")
    fun updateTask(@RequestBody task: Task) {
        TODO()
    }
}

data class TaskFilter(
    val status: List<TaskStatus>?,
)