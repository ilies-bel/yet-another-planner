package com.iliesbel.yapbackend.domain.reddit.persistence

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "reddit_integration")
class RedditIntegrationEntity(
    @Id
    @GeneratedValue
    var id: UUID? = null,

    @Column(name = "user_email", nullable = false)
    var userEmail: String,

    @Column(name = "reddit_username")
    var redditUsername: String? = null,

    @Column(name = "access_token", nullable = false)
    var accessToken: String,

    @Column(name = "refresh_token")
    var refreshToken: String? = null,

    @Column(name = "token_expires_at")
    var tokenExpiresAt: LocalDateTime? = null,

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)