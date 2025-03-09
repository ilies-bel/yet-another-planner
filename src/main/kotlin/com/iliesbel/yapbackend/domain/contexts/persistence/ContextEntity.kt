package com.iliesbel.yapbackend.domain.contexts.persistence

import com.iliesbel.yapbackend.domain.contexts.ContextType
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "context")
class ContextEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    var name: String,
    val userEmail: String,

    @Enumerated(EnumType.STRING)
    val type: ContextType,

    val deviceIdentifier: UUID,
)