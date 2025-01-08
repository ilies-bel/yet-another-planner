package com.iliesbel.yapbackend.tasks.persistence

import com.iliesbel.yapbackend.tasks.domain.Difficulty
import com.iliesbel.yapbackend.tasks.domain.Task
import com.iliesbel.yapbackend.tasks.domain.TaskContext
import org.springframework.stereotype.Repository


@Repository
class TaskRepository (private val taskJpaRepository: TaskJpaRepository) {

    fun findAll(): List<Task> {
        return taskJpaRepository.findAll().map {
            Task(
                name = it.name,
                description = it.description,
                status = it.status,
                difficulty = Difficulty(it.difficulty),
                context =  TaskContext(it.context.name),
                dueDate = it.dueDate,
                projectName = it.project.name
            )
        }
    }

}

