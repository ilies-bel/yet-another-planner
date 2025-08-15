package com.iliesbel.yapbackend.domain.tasks.externalTaskRetriever

import com.iliesbel.yapbackend.domain.tasks.persistence.TaskRepository
import com.iliesbel.yapbackend.domain.tasks.service.TaskCreation
import com.iliesbel.yapbackend.domain.tasks.service.model.Difficulty
import com.iliesbel.yapbackend.infra.authentication.AuthenticationService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class ExternalTaskService(
    private val externalTaskRepository: ExternalTaskRepository,
    private val taskRepository: TaskRepository,
) {

    private val logger = LoggerFactory.getLogger(ExternalTaskService::class.java)

    fun save(externalTasks: List<ExternalTask>) {
        val foundTasks = externalTaskRepository.findByNaturalIdIn(externalTasks.map { it.naturalId }).map {
            ExternalTask(
                name = it.name,
                naturalId = it.naturalId,
                tags = null,
                description = null,
                url = null,
                img64 = null,
            )
        }

        val taskToSave = externalTasks.subtract(foundTasks.toSet())

        externalTaskRepository.saveAll(
            taskToSave.map {
                ExternalTaskEntity(
                    id = UUID.randomUUID(),
                    name = it.name,
                    naturalId = it.naturalId,
                )
            }
        )


        taskRepository.saveAll(
            taskToSave.map {
                TaskCreation(
                    name = it.name,
                    description = it.description,
                    userEmail = AuthenticationService.getAccountFromContext().email,
                    difficulty = Difficulty.DEFAULT,
                    url = it.url,
                    project = null,
                    dueDate = null,
                    contextName = null,
                    timeContext = null,
                )
            }
        )

        logger.info("Saved ${taskToSave.size}")
    }


}
