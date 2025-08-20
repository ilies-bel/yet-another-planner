package com.iliesbel.yapbackend.domain.tasks.presentation

import com.iliesbel.yapbackend.domain.contexts.domain.DayPeriod
import com.iliesbel.yapbackend.domain.tasks.service.TaskService
import com.iliesbel.yapbackend.domain.tasks.service.TaskUpdate
import com.iliesbel.yapbackend.domain.tasks.service.model.Task
import com.iliesbel.yapbackend.domain.tasks.service.model.TaskStatus
import com.iliesbel.yapbackend.infra.authentication.AuthenticationService
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*

@RestController
class TaskController(private val taskService: TaskService) {
    @GetMapping("/tasks")
    fun getTasksForCurrentUser(
        filters: TaskPageFilter
    ): Page<Task> {
        return taskService.findAll(filters)
    }

    @PostMapping("/tasks")
    fun addTask(@RequestBody task: TaskCreationDto) {
        val currentUser = AuthenticationService.getAccountFromContext()

        return taskService.create(task.toTaskCreation(currentUser))
    }

    @PatchMapping("/tasks/{id}")
    fun updateTask(
        @PathVariable id: Long,
        @RequestBody taskUpdate: TaskUpdate
    ): Task {
        return taskService.update(id, taskUpdate)
    }
}

data class TaskPageFilter(
    val status: List<TaskStatus>?,
    val page: Int = 0,
    val size: Int = 20,
    val contextId: Long?,
    val timeContext: DayPeriod?,
    val tagIds: List<Long>? = null,
    val tagMode: String? = "any" // "any" or "all"
)