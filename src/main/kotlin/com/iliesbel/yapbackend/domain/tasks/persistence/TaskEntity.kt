package com.iliesbel.yapbackend.domain.tasks.persistence

import com.iliesbel.yapbackend.domain.tasks.domain.TaskStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "task")
class TaskEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    var name: String,
    var description: String,

    @Enumerated(EnumType.STRING)
    var status: TaskStatus,
    var difficulty: Int,
    var dueDate: LocalDateTime,

    @ManyToOne
    val context: ContextEntity,

    @ManyToOne
    val project: ProjectEntity
    )

