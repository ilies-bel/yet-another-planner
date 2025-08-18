package com.iliesbel.yapbackend.domain.reddit.service

import com.iliesbel.yapbackend.domain.reddit.domain.RedditIntegration
import com.iliesbel.yapbackend.domain.reddit.domain.RedditPost
import com.iliesbel.yapbackend.domain.reddit.domain.CaptureJob
import com.iliesbel.yapbackend.domain.reddit.domain.CaptureStatus
import com.iliesbel.yapbackend.domain.reddit.domain.CaptureProgress
import com.iliesbel.yapbackend.domain.reddit.persistence.RedditIntegrationRepository
import com.iliesbel.yapbackend.domain.tasks.service.model.TaskStatus
import com.iliesbel.yapbackend.domain.tasks.service.TaskService
import com.iliesbel.yapbackend.domain.tasks.service.TaskCreation
import com.iliesbel.yapbackend.domain.tasks.service.model.Difficulty
import com.iliesbel.yapbackend.infra.authentication.AuthenticationService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class RedditCaptureService(
    private val redditApiClient: RedditApiClient,
    private val redditOAuthService: RedditOAuthService,
    private val integrationRepository: RedditIntegrationRepository,
    private val taskService: TaskService
) {

    @Transactional
    fun startCapture(): CaptureJob {
        val userEmail = AuthenticationService.getAccountFromContext().email
        val integration = integrationRepository.findByCurrentUser()
            ?: throw RuntimeException("No Reddit integration found for current user")
        
        // Refresh token if needed
        val validIntegration = if (isTokenExpired(integration)) {
            redditOAuthService.refreshToken(integration)
        } else {
            integration
        }
        
        return try {
            val posts = redditApiClient.fetchSavedPosts(validIntegration)
            val result = createTasksFromPosts(posts, userEmail)
            
            CaptureJob(
                id = UUID.randomUUID(),
                userEmail = userEmail,
                status = CaptureStatus.COMPLETED,
                progress = CaptureProgress(
                    totalPosts = posts.size,
                    processed = posts.size,
                    createdTasks = result.created,
                    duplicatesSkipped = result.skipped
                )
            )
        } catch (e: Exception) {
            CaptureJob(
                id = UUID.randomUUID(),
                userEmail = userEmail,
                status = CaptureStatus.FAILED,
                progress = CaptureProgress(),
                errorMessage = e.message
            )
        }
    }
    
    private fun isTokenExpired(integration: RedditIntegration): Boolean {
        return integration.tokenExpiresAt?.isBefore(java.time.LocalDateTime.now()) ?: false
    }
    
    private fun createTasksFromPosts(posts: List<RedditPost>, userEmail: String): CaptureResult {
        var created = 0
        var skipped = 0
        
        posts.forEach { post ->
            val existingTask = taskService.findBySourceUrl(post.permalink)
            
            if (existingTask == null) {
                val description = buildTaskDescription(post)
                
                val taskCreation = TaskCreation(
                    name = post.title,
                    difficulty = Difficulty.MEDIUM, // Default difficulty for Reddit posts
                    status = TaskStatus.CAPTURED,
                    description = description,
                    project = null,
                    dueDate = null,
                    contextName = null,
                    userEmail = userEmail,
                    url = post.url,
                    timeContext = null,
                    sourceUrl = post.permalink,
                    sourceType = "reddit"
                )
                
                taskService.create(taskCreation)
                created++
            } else {
                skipped++
            }
        }
        
        return CaptureResult(created, skipped)
    }
    
    private fun buildTaskDescription(post: RedditPost): String {
        val parts = mutableListOf<String>()
        
        // Add subreddit and author info
        parts.add("**Source:** r/${post.subreddit} by u/${post.author}")
        
        // Add self text if available
        if (!post.selftext.isNullOrBlank()) {
            parts.add("**Content:**")
            parts.add(post.selftext)
        }
        
        // Add original URL if not a self post
        if (!post.isSelf) {
            parts.add("**Original URL:** ${post.url}")
        }
        
        return parts.joinToString("\n\n")
    }
}

data class CaptureResult(
    val created: Int,
    val skipped: Int
)