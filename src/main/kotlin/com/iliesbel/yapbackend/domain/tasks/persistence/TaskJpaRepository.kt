package com.iliesbel.yapbackend.domain.tasks.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface TaskJpaRepository : JpaRepository<TaskEntity, Long> {
    fun findBySourceUrl(sourceUrl: String): TaskEntity?
    
    @Query("SELECT t FROM TaskEntity t WHERE t.sourceType = :sourceType")
    fun findBySourceType(sourceType: String): List<TaskEntity>
    
    @Query("SELECT t FROM TaskEntity t WHERE t.id = :taskId")
    fun findByIdAndUserId(@Param("taskId") taskId: Long, @Param("userId") userId: Long): TaskEntity?
    
    @Query("SELECT t FROM TaskEntity t WHERE t.id IN :taskIds")
    fun findAllByIdInAndUserId(@Param("taskIds") taskIds: List<Long>, @Param("userId") userId: Long): List<TaskEntity>
}