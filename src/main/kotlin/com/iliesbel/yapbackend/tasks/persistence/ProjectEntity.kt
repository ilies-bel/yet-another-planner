package com.iliesbel.yapbackend.tasks.persistence

import jakarta.persistence.*

@Entity
@Table(name = "project")
class ProjectEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val name: String
)