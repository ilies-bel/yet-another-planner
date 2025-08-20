package com.iliesbel.yapbackend.domain.tasks.persistence

import com.iliesbel.yapbackend.domain.contexts.domain.DayPeriod
import com.iliesbel.yapbackend.domain.contexts.persistence.ContextEntity
import com.iliesbel.yapbackend.domain.tags.persistence.TagEntity
import com.iliesbel.yapbackend.domain.tasks.service.model.Difficulty
import com.iliesbel.yapbackend.domain.tasks.service.model.TaskStatus
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
    var context: ContextEntity?,

    @ManyToOne
    var project: ProjectEntity?,

    var url: String?,

    val creationDate: LocalDateTime,

    @Enumerated(EnumType.STRING)
    var timeContext: DayPeriod?,
    
    var sourceUrl: String?,
    
    var sourceType: String?,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "task_tags",
        joinColumns = [JoinColumn(name = "task_id")],
        inverseJoinColumns = [JoinColumn(name = "tag_id")]
    )
    val tags: MutableSet<TagEntity> = mutableSetOf()
)

