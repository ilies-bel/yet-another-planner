package com.iliesbel.yapbackend.domain.reddit.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RedditIntegrationJpaRepository : JpaRepository<RedditIntegrationEntity, UUID> {
    fun findByUserEmail(userEmail: String): RedditIntegrationEntity?
    fun deleteByUserEmail(userEmail: String)
}