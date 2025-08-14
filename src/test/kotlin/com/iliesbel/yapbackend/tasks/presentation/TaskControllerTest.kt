package com.iliesbel.yapbackend.tasks.presentation

import com.iliesbel.yapbackend.domain.contexts.domain.ContextType
import com.iliesbel.yapbackend.domain.contexts.persistence.ContextEntity
import com.iliesbel.yapbackend.domain.contexts.persistence.ContextJpaRepository
import com.iliesbel.yapbackend.domain.tasks.persistence.ProjectEntity
import com.iliesbel.yapbackend.domain.tasks.persistence.TaskEntity
import com.iliesbel.yapbackend.domain.tasks.persistence.TaskJpaRepository
import com.iliesbel.yapbackend.domain.tasks.presentation.ProjectJpaRepository
import com.iliesbel.yapbackend.domain.tasks.service.model.Difficulty
import com.iliesbel.yapbackend.domain.tasks.service.model.TaskStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.test.context.TestPropertySource
import java.time.LocalDateTime
import java.util.*

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = [
    "spring.flyway.enabled=false", 
    "spring.jpa.hibernate.ddl-auto=create-drop"
])
class TaskRepositoryIntegrationTest {

    @Autowired
    private lateinit var taskRepository: TaskJpaRepository

    @Autowired
    private lateinit var contextRepository: ContextJpaRepository

    @Autowired
    private lateinit var projectRepository: ProjectJpaRepository


    private lateinit var context: ContextEntity
    private lateinit var project: ProjectEntity

    @BeforeEach
    fun setup() {
        // Create test context
        context = contextRepository.save(
            ContextEntity(
                name = "Test Context",
                userEmail = "test@example.com",
                type = ContextType.DEVICE,
                deviceIdentifier = UUID.randomUUID()
            )
        )

        // Create test project
        project = projectRepository.save(
            ProjectEntity(
                name = "Test Project"
            )
        )

        // Create sample tasks
        val tasks = listOf(
            TaskEntity(
                name = "Task 1",
                description = "Description 1",
                status = TaskStatus.TODO,
                difficulty = Difficulty.EASY,
                dueDate = LocalDateTime.now().plusDays(1),
                context = context,
                project = project,
                url = null,
                creationDate = LocalDateTime.now()
            ),
            TaskEntity(
                name = "Task 2",
                description = "Description 2",
                status = TaskStatus.IN_PROGRESS,
                difficulty = Difficulty.MEDIUM,
                dueDate = LocalDateTime.now().plusDays(2),
                context = context,
                project = project,
                url = null,
                creationDate = LocalDateTime.now()
            ),
            TaskEntity(
                name = "Task 3",
                description = "Description 3",
                status = TaskStatus.DONE,
                difficulty = Difficulty.HARD,
                dueDate = LocalDateTime.now().plusDays(3),
                context = context,
                project = project,
                url = null,
                creationDate = LocalDateTime.now()
            )
        )

        taskRepository.saveAll(tasks)
    }

    @Test
    fun `should find all tasks`() {
        // When
        val foundTasks = taskRepository.findAll()

        // Then
        assertThat(foundTasks).isNotNull
        assertThat(foundTasks).hasSize(3)

        // Verify tasks content
        assertThat(foundTasks).anySatisfy { task ->
            assertThat(task.name).isEqualTo("Task 1")
            assertThat(task.status).isEqualTo(TaskStatus.TODO)
            assertThat(task.context?.id).isEqualTo(context.id)
            assertThat(task.project?.id).isEqualTo(project.id)
        }
    }

    @Test
    fun `should find all tasks with correct relationships`() {
        // When
        val foundTasks = taskRepository.findAll()

        // Then
        foundTasks.forEach { task ->
            assertThat(task.context).isNotNull
            assertThat(task.context?.name).isEqualTo("Test Context")
            assertThat(task.project).isNotNull
            assertThat(task.project?.name).isEqualTo("Test Project")
        }
    }
}

