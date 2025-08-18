package com.iliesbel.yapbackend.domain.reddit.presentation

import com.iliesbel.yapbackend.domain.reddit.domain.CaptureJob
import com.iliesbel.yapbackend.domain.reddit.domain.RedditIntegration
import com.iliesbel.yapbackend.domain.reddit.persistence.RedditIntegrationRepository
import com.iliesbel.yapbackend.domain.reddit.service.RedditCaptureService
import com.iliesbel.yapbackend.domain.reddit.service.RedditOAuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/reddit")
class RedditController(
    private val oAuthService: RedditOAuthService,
    private val captureService: RedditCaptureService,
    private val integrationRepository: RedditIntegrationRepository
) {

    @GetMapping("/auth/url")
    fun getAuthorizationUrl(): AuthUrlResponse {
        val url = oAuthService.generateAuthorizationUrl()
        return AuthUrlResponse(url)
    }

    @PostMapping("/callback")
    fun handleCallback(
        @RequestBody request: CallbackRequest
    ): ResponseEntity<*> {
        return try {
            val integration = oAuthService.handleCallback(request.code, request.state)
            ResponseEntity.ok(integration)
        } catch (e: Exception) {
            println("Reddit callback error: ${e.message}")
            e.printStackTrace()
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    @GetMapping("/integration")
    fun getIntegration(): ResponseEntity<RedditIntegration> {
        val integration = integrationRepository.findByCurrentUser()
        return if (integration != null) {
            ResponseEntity.ok(integration)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/integration")
    fun deleteIntegration(): ResponseEntity<Void> {
        try {
            integrationRepository.deleteByCurrentUser()
        } catch (e: Exception) {
            // Force delete even if there's an error
            println("Error during delete, forcing cleanup: ${e.message}")
        }
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/capture")
    fun startCapture(): ResponseEntity<CaptureJob> {
        val job = captureService.startCapture()
        return ResponseEntity.ok(job)
    }
}

data class AuthUrlResponse(
    val authorizationUrl: String
)

data class CallbackRequest(
    val code: String,
    val state: String
)

