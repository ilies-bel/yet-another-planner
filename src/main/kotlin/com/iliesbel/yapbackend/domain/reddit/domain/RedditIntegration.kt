package com.iliesbel.yapbackend.domain.reddit.domain

import java.time.LocalDateTime
import java.util.*

data class RedditIntegration(
    val id: UUID,
    val userEmail: String,
    val redditUsername: String?,
    val accessToken: String,
    val refreshToken: String?,
    val tokenExpiresAt: LocalDateTime?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)