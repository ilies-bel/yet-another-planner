package com.iliesbel.yapbackend.domain.tasks.presentation

import com.iliesbel.yapbackend.domain.tasks.domain.Task
import com.iliesbel.yapbackend.domain.tasks.domain.TaskService
import org.springframework.web.bind.annotation.*

@RestController
class TaskController(private val taskService: TaskService) {

    @GetMapping("/tasks")
    fun getTasksForCurrentUser(): List<Task> {
        return taskService.findAll()
    }




    @PostMapping("/tasks")
    fun addTask(@RequestBody task: Task) {
        return taskService.create(task)
    }

    @PatchMapping("/tasks")
    fun updateTask(@RequestBody task: Task) {

    }
}