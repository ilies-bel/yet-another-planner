package com.iliesbel.yapbackend.domain.tags.persistence

import com.iliesbel.yapbackend.domain.tasks.persistence.TaskEntity
import com.iliesbel.yapbackend.domain.users.persistence.UserEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "tags")
class TagEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,

    @Column(nullable = false, length = 50)
    var name: String,

    @Column(length = 7)
    var color: String = "#808080",

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    @Column(name = "usage_count")
    var usageCount: Int = 0,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "is_archived")
    var isArchived: Boolean = false,

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    val tasks: MutableSet<TaskEntity> = mutableSetOf()
)