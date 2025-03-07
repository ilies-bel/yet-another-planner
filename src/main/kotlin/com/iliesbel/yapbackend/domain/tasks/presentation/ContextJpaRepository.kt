package com.iliesbel.yapbackend.domain.tasks.presentation

import com.iliesbel.yapbackend.domain.tasks.persistence.ContextEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ContextJpaRepository : JpaRepository<ContextEntity, Long> {
    fun findByName(contextName: String): ContextEntity
}