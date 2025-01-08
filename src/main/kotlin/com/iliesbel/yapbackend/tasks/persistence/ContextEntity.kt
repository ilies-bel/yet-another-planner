package com.iliesbel.yapbackend.tasks.persistence

import jakarta.persistence.*

@Entity
@Table(name = "context")
class ContextEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    var name: String,
)