package com.iliesbel.yapbackend.domain.contexts.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface ContextJpaRepository : JpaRepository<ContextEntity, Long> {
    fun findByName(contextName: String): ContextEntity
    fun findByUserEmail(email: String): List<ContextEntity>
}