package com.iliesbel.yapbackend.domain.contexts.domain

import java.util.*

data class Context(
    val id: Long,
    val name: String,
    val type: ContextType,
    val deviceIdentifier: UUID,
)