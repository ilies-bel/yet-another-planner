package com.iliesbel.yapbackend.domain.tasks.service

class TaskNotFoundException(taskId: Long) : RuntimeException("Task with id $taskId not found")