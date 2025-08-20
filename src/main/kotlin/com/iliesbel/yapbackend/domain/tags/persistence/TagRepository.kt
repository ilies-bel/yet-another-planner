package com.iliesbel.yapbackend.domain.tags.persistence

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface TagRepository : JpaRepository<TagEntity, Long> {

    fun findByUserIdAndIsArchivedFalse(userId: Long): List<TagEntity>

    fun findByUserIdAndIsArchived(userId: Long, isArchived: Boolean, pageable: Pageable): Page<TagEntity>

    fun findByUserIdAndNameIgnoreCase(userId: Long, name: String): TagEntity?

    fun findByIdAndUserId(id: Long, userId: Long): TagEntity?

    @Query("SELECT t FROM TagEntity t WHERE t.user.id = :userId AND LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%')) AND t.isArchived = :archived")
    fun searchByNameAndArchived(
        @Param("userId") userId: Long,
        @Param("search") search: String,
        @Param("archived") archived: Boolean,
        pageable: Pageable
    ): Page<TagEntity>

    @Query("SELECT t FROM TagEntity t WHERE t.user.id = :userId ORDER BY t.usageCount DESC")
    fun findMostUsedTags(@Param("userId") userId: Long, pageable: Pageable): List<TagEntity>

    @Modifying
    @Query("UPDATE TagEntity t SET t.usageCount = t.usageCount + 1 WHERE t.id = :tagId")
    fun incrementUsageCount(@Param("tagId") tagId: Long)

    @Modifying
    @Query("UPDATE TagEntity t SET t.usageCount = t.usageCount - 1 WHERE t.id = :tagId AND t.usageCount > 0")
    fun decrementUsageCount(@Param("tagId") tagId: Long)

    fun countByUserId(userId: Long): Long

    fun deleteByIdAndUserId(id: Long, userId: Long)
}