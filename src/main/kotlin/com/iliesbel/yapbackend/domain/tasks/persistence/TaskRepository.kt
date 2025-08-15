package com.iliesbel.yapbackend.domain.tasks.persistence

import com.iliesbel.jooq.generated.tables.references.CONTEXT
import com.iliesbel.jooq.generated.tables.references.PROJECT
import com.iliesbel.jooq.generated.tables.references.TASK
import com.iliesbel.yapbackend.domain.contexts.persistence.ContextJpaRepository
import com.iliesbel.yapbackend.domain.tasks.presentation.ProjectJpaRepository
import com.iliesbel.yapbackend.domain.tasks.presentation.TaskPageFilter
import com.iliesbel.yapbackend.domain.tasks.service.TaskCreation
import com.iliesbel.yapbackend.domain.tasks.service.TaskUpdate
import com.iliesbel.yapbackend.domain.tasks.service.model.Task
import com.iliesbel.yapbackend.domain.tasks.service.model.TaskContext
import com.iliesbel.yapbackend.domain.tasks.service.model.TaskStatus
import org.jooq.DSLContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository
import java.time.LocalDateTime


@Repository
class TaskRepository(
    private val dslContext: DSLContext,
    private val taskJpaRepository: TaskJpaRepository,
    private val contextRepository: ContextJpaRepository,
    private val projectJpaRepository: ProjectJpaRepository,
) {

    fun findAll(filters: TaskPageFilter): Page<Task> {
        val pageable = PageRequest.of(filters.page, filters.size)

        // Build the base query
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

        // Add status filter if provided
        var whereConditions = mutableListOf<org.jooq.Condition>()

        if (filters.status != null) {
            whereConditions.add(TASK.STATUS.`in`(filters.status))
        }

        // Add context filter: allow tasks with current context OR no context
        if (filters.contextId != null) {
            whereConditions.add(
                TASK.CONTEXT_ID.eq(filters.contextId)
                    .or(TASK.CONTEXT_ID.isNull)
            )
        }
        
        // Add time context filter: allow tasks with current time context OR no time context
        if (filters.timeContext != null) {
            whereConditions.add(
                TASK.TIME_CONTEXT.eq(filters.timeContext.name)
                    .or(TASK.TIME_CONTEXT.isNull)
            )
        }

        if (whereConditions.isNotEmpty()) {
            selectQuery.where(whereConditions.reduce { acc, condition -> acc.and(condition) })
        }

        // Get total count for pagination
        val countQuery = dslContext
            .selectCount()
            .from(TASK)
            .leftJoin(CONTEXT).on(TASK.CONTEXT_ID.eq(CONTEXT.ID))
            .leftJoin(PROJECT).on(TASK.PROJECT_ID.eq(PROJECT.ID))

        // Apply same filtering logic to count query
        if (whereConditions.isNotEmpty()) {
            countQuery.where(whereConditions.reduce { acc, condition -> acc.and(condition) })
        }

        val totalCount = countQuery.fetchOne(0, Long::class.java) ?: 0L

        // Apply pagination
        val result = selectQuery
            .limit(filters.size)
            .offset(filters.page * filters.size)
            .fetch()
            .map { record ->

                Task(
                    id = record.get(TASK.ID)!!,
                    name = record.get(TASK.NAME)!!,
                    description = record.get(TASK.DESCRIPTION),
                    status = TaskStatus.valueOf(record.get(TASK.STATUS)!!),
                    difficulty = record.get(TASK.DIFFICULTY),
                    context = record.get(CONTEXT.NAME)?.let {
                        TaskContext(
                            it,
                            "TIME"
                        )
                    },
                    dueDate = record.get(TASK.DUE_DATE),
                    projectName = record.get(PROJECT.NAME),
                    timeContext = null // Will be populated from TaskEntity when we migrate to JPA-only queries
                )
            }

        return PageImpl(result, pageable, totalCount)
    }

    fun saveAll(taskCreations: List<TaskCreation>) {
        val taskToSave = taskCreations.map { taskCreation ->
            val context = taskCreation.contextName?.let(contextRepository::findByName) // TODO n+1 a corriger
            val project = taskCreation.project?.let(projectJpaRepository::findByName)

            TaskEntity(
                name = taskCreation.name,
                difficulty = taskCreation.difficulty,
                status = TaskStatus.TO_REFINE,
                context = context,
                description = taskCreation.description,
                project = project,
                dueDate = taskCreation.dueDate,
                creationDate = LocalDateTime.now(),
                url = taskCreation.url,
                timeContext = taskCreation.timeContext,
            )
        }

        taskJpaRepository.saveAll(taskToSave)
    }

    fun save(taskCreation: TaskCreation) {
        saveAll(listOf(taskCreation))
    }

    fun update(id: Long, taskUpdate: TaskUpdate): Task {
        val existingTask =
            taskJpaRepository.findById(id).orElseThrow { NoSuchElementException("The task with id $id does not exist") }

        // Update all fields since they are now mutable (var)
        taskUpdate.name?.let { existingTask.name = it }
        taskUpdate.description?.let { existingTask.description = it }
        taskUpdate.status?.let { existingTask.status = it }
        taskUpdate.difficulty?.let { existingTask.difficulty = it }
        taskUpdate.dueDate?.let { existingTask.dueDate = it }
        taskUpdate.url?.let { existingTask.url = it }
        taskUpdate.timeContext?.let { existingTask.timeContext = it }

        // Handle context update
        taskUpdate.contextName?.let { contextName ->
            existingTask.context = contextRepository.findByName(contextName)
        }

        // Handle project update
        taskUpdate.project?.let { projectName ->
            existingTask.project = projectJpaRepository.findByName(projectName)
        }

        val savedTask = taskJpaRepository.save(existingTask)

        // Convert back to Task model
        return Task(
            id = savedTask.id!!,
            name = savedTask.name,
            description = savedTask.description,
            status = savedTask.status,
            difficulty = savedTask.difficulty.name,
            context = savedTask.context?.let {
                TaskContext(
                    name = it.name,
                    type = it.type.name,
                )
            },
            projectName = savedTask.project?.name,
            dueDate = savedTask.dueDate,
            timeContext = savedTask.timeContext
        )
    }
}