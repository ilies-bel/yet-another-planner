package com.iliesbel.yapbackend.domain.reddit.persistence

import com.iliesbel.yapbackend.domain.reddit.domain.RedditIntegration
import com.iliesbel.yapbackend.infra.authentication.AuthenticationService
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Repository
class RedditIntegrationRepository(
    private val jpaRepository: RedditIntegrationJpaRepository
) {

    fun findByCurrentUser(): RedditIntegration? {
        val account = AuthenticationService.getAccountFromContext()
        return jpaRepository.findByUserEmail(account.email)?.toDomain()
    }

    fun save(integration: RedditIntegration): RedditIntegration {
        // Check if integration already exists for this user
        val existingEntity = jpaRepository.findByUserEmail(integration.userEmail)
        
        val entity = if (existingEntity != null) {
            // Update existing integration
            existingEntity.apply {
                redditUsername = integration.redditUsername
                accessToken = integration.accessToken
                refreshToken = integration.refreshToken
                tokenExpiresAt = integration.tokenExpiresAt
                updatedAt = LocalDateTime.now()
            }
        } else {
            // Create new integration (don't set ID - let Hibernate generate it)
            RedditIntegrationEntity(
                userEmail = integration.userEmail,
                redditUsername = integration.redditUsername,
                accessToken = integration.accessToken,
                refreshToken = integration.refreshToken,
                tokenExpiresAt = integration.tokenExpiresAt,
                createdAt = integration.createdAt,
                updatedAt = LocalDateTime.now()
            )
        }
        
        return jpaRepository.save(entity).toDomain()
    }

    @Transactional
    fun deleteByCurrentUser() {
        val account = AuthenticationService.getAccountFromContext()
        jpaRepository.deleteByUserEmail(account.email)
    }
}

fun RedditIntegrationEntity.toDomain(): RedditIntegration {
    return RedditIntegration(
        id = id!!,
        userEmail = userEmail,
        redditUsername = redditUsername,
        accessToken = accessToken,
        refreshToken = refreshToken,
        tokenExpiresAt = tokenExpiresAt,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}