package com.iliesbel.yapbackend.domain.tasks.presentation

import com.iliesbel.yapbackend.domain.tasks.domain.TaskService
import com.iliesbel.yapbackend.domain.tasks.domain.model.Task
import com.iliesbel.yapbackend.domain.tasks.domain.model.TaskStatus
import org.springframework.web.bind.annotation.*

@RestController
class TaskController(private val taskService: TaskService) {

    @GetMapping("/tasks")
    fun getTasksForCurrentUser(
        filters : TaskFilter
    ): List<Task> {
        return taskService.findAll(filters)
    }


    @PostMapping("/tasks")
    fun addTask(@RequestBody task: Task) {
        return taskService.create(task)
    }

    @PatchMapping("/tasks")
    fun updateTask(@RequestBody task: Task) {

    }
}

data class TaskFilter (
    val status: List<TaskStatus>?,
)