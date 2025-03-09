package com.iliesbel.yapbackend.tasks.presentation

import com.iliesbel.yapbackend.domain.contexts.persistence.ContextEntity
import com.iliesbel.yapbackend.domain.contexts.persistence.ContextJpaRepository
import com.iliesbel.yapbackend.domain.tasks.domain.model.TaskStatus
import com.iliesbel.yapbackend.domain.tasks.persistence.ProjectEntity
import com.iliesbel.yapbackend.domain.tasks.persistence.TaskEntity
import com.iliesbel.yapbackend.domain.tasks.persistence.TaskJpaRepository
import com.iliesbel.yapbackend.domain.tasks.presentation.ProjectJpaRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDateTime

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskRepositoryIntegrationTest {

    @Autowired
    private lateinit var taskRepository: TaskJpaRepository

    @Autowired
    private lateinit var contextRepository: ContextJpaRepository

    @Autowired
    private lateinit var projectRepository: ProjectJpaRepository

    companion object {
        @Container
        private val postgresContainer = PostgreSQLContainer("postgres:16-alpine").apply {
            withDatabaseName("testdb")
            withUsername("test")
            withPassword("test")
            waitingFor(Wait.forListeningPort())
            withStartupTimeout(java.time.Duration.ofSeconds(60))
            start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgresContainer::getJdbcUrl)
            registry.add("spring.datasource.username", postgresContainer::getUsername)
            registry.add("spring.datasource.password", postgresContainer::getPassword)
            // Specific to PostgreSQL
            registry.add(
                "spring.jpa.properties.hibernate.dialect",
                { "org.hibernate.dialect.PostgreSQLDialect" })
        }
    }

    private lateinit var context: ContextEntity
    private lateinit var project: ProjectEntity

    @BeforeEach
    fun setup() {
        // Create test context
        context = contextRepository.save(
            ContextEntity(
                name = "Test Context"
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
                difficulty = 1,
                dueDate = LocalDateTime.now().plusDays(1),
                context = context,
                project = project
            ),
            TaskEntity(
                name = "Task 2",
                description = "Description 2",
                status = TaskStatus.IN_PROGRESS,
                difficulty = 2,
                dueDate = LocalDateTime.now().plusDays(2),
                context = context,
                project = project
            ),
            TaskEntity(
                name = "Task 3",
                description = "Description 3",
                status = TaskStatus.DONE,
                difficulty = 3,
                dueDate = LocalDateTime.now().plusDays(3),
                context = context,
                project = project
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
            assertThat(task.context.id).isEqualTo(context.id)
            assertThat(task.project.id).isEqualTo(project.id)
        }
    }

    @Test
    fun `should find all tasks with correct relationships`() {
        // When
        val foundTasks = taskRepository.findAll()

        // Then
        foundTasks.forEach { task ->
            assertThat(task.context).isNotNull
            assertThat(task.context.name).isEqualTo("Test Context")
            assertThat(task.project).isNotNull
            assertThat(task.project.name).isEqualTo("Test Project")
        }
    }
}

