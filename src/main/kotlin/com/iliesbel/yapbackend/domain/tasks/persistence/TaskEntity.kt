package com.iliesbel.yapbackend.domain.tasks.persistence

import com.iliesbel.yapbackend.domain.tasks.domain.model.Difficulty
import com.iliesbel.yapbackend.domain.tasks.domain.model.TaskStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "task")
class TaskEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,


    var name: String,
    var description: String?,

    @Enumerated(EnumType.STRING)
    var status: TaskStatus,

    @Enumerated(EnumType.STRING)
    var difficulty: Difficulty,

    var dueDate: LocalDateTime?,

    @ManyToOne
    val context: ContextEntity?,

    @ManyToOne
    val project: ProjectEntity?,

    val creationDate: LocalDateTime,
)

