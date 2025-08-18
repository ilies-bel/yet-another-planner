package com.iliesbel.yapbackend.domain.reddit.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
interface RedditIntegrationJpaRepository : JpaRepository<RedditIntegrationEntity, UUID> {
    fun findByUserEmail(userEmail: String): RedditIntegrationEntity?
    
    @Modifying
    @Transactional
    fun deleteByUserEmail(userEmail: String)
}