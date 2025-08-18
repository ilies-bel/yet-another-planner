package com.iliesbel.yapbackend.domain.tasks.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface TaskJpaRepository : JpaRepository<TaskEntity, Long> {
    fun findBySourceUrl(sourceUrl: String): TaskEntity?
}