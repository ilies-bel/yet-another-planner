package com.iliesbel.yapbackend.domain.tasks.persistence

import com.iliesbel.jooq.generated.tables.references.CONTEXT
import com.iliesbel.jooq.generated.tables.references.PROJECT
import com.iliesbel.jooq.generated.tables.references.TASK
import com.iliesbel.yapbackend.domain.contexts.persistence.ContextJpaRepository
import com.iliesbel.yapbackend.domain.tasks.domain.TaskCreation
import com.iliesbel.yapbackend.domain.tasks.domain.model.Task
import com.iliesbel.yapbackend.domain.tasks.domain.model.TaskContext
import com.iliesbel.yapbackend.domain.tasks.domain.model.TaskStatus
import com.iliesbel.yapbackend.domain.tasks.presentation.ProjectJpaRepository
import com.iliesbel.yapbackend.domain.tasks.presentation.TaskFilter
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.LocalDateTime


@Repository
class TaskRepository(
    private val dslContext: DSLContext,
    private val taskJpaRepository: TaskJpaRepository,
    private val contextRepository: ContextJpaRepository,
    private val projectJpaRepository: ProjectJpaRepository,
) {

    fun findAll(filters: TaskFilter): List<Task> {

        val selectQuery = dslContext
            .select(
                TASK.ID,
                TASK.NAME,
                TASK.DESCRIPTION,
                TASK.STATUS,
                TASK.DIFFICULTY,
                CONTEXT.NAME,
                TASK.DUE_DATE,
                PROJECT.NAME,
            )
            .from(TASK)
            .leftJoin(CONTEXT).on(TASK.CONTEXT_ID.eq(CONTEXT.ID))
            .leftJoin(PROJECT).on(TASK.PROJECT_ID.eq(PROJECT.ID))

        if (filters.status != null) {
            selectQuery.where(TASK.STATUS.`in`(filters.status))
        }

        val result = selectQuery
            .fetch()
            .map { record ->
                Task(
                    id = record.get(TASK.ID)!!,
                    name = record.get(TASK.NAME)!!,
                    description = record.get(TASK.DESCRIPTION),
                    status = TaskStatus.valueOf(record.get(TASK.STATUS)!!),
                    difficulty = record.get(TASK.DIFFICULTY),
                    context = record.get(CONTEXT.NAME)?.let(::TaskContext),
                    dueDate = record.get(TASK.DUE_DATE),
                    projectName = record.get(PROJECT.NAME)
                )
            }


        return result
    }


    fun save(taskCreation: TaskCreation) {
        val context = taskCreation.contextName?.let(contextRepository::findByName)
        val project = taskCreation.project?.let(projectJpaRepository::findByName)

        val entity = TaskEntity(
            name = taskCreation.name,
            difficulty = taskCreation.difficulty,
            status = TaskStatus.TO_REFINE,
            context = context,
            description = taskCreation.description,
            project = project,
            dueDate = taskCreation.dueDate,
            creationDate = LocalDateTime.now(),
        )

        taskJpaRepository.save(entity)
    }
}