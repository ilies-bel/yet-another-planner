package com.iliesbel.yapbackend.domain.tasks.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface TaskJpaRepository : JpaRepository<TaskEntity, Long> {
    fun findBySourceUrl(sourceUrl: String): TaskEntity?
    
    @Query("SELECT t FROM TaskEntity t WHERE t.sourceType = :sourceType")
    fun findBySourceType(sourceType: String): List<TaskEntity>
}