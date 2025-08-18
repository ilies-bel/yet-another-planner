package com.iliesbel.yapbackend.domain.reddit.service

import com.iliesbel.yapbackend.domain.reddit.domain.RedditIntegration
import com.iliesbel.yapbackend.domain.reddit.persistence.RedditIntegrationRepository
import com.iliesbel.yapbackend.infra.authentication.AuthenticationService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate
import java.time.LocalDateTime
import java.util.*

@Service
class RedditOAuthService(
    private val integrationRepository: RedditIntegrationRepository,
    private val restTemplate: RestTemplate = RestTemplate(),
    @Value("\${reddit.client-id:}") private val clientId: String,
    @Value("\${reddit.client-secret:}") private val clientSecret: String,
    @Value("\${reddit.redirect-uri:http://localhost:3000/auth/reddit/callback}") private val redirectUri: String
) {
    
    // In production, use Redis or database for state storage
    private val stateStorage = mutableMapOf<String, String>()

    fun generateAuthorizationUrl(): String {
        val userEmail = AuthenticationService.getAccountFromContext().email
        val state = UUID.randomUUID().toString()
        
        // Store the state with user email for later retrieval in callback
        stateStorage[state] = userEmail
        
        val params = mapOf(
            "client_id" to clientId,
            "response_type" to "code",
            "state" to state,
            "redirect_uri" to redirectUri,
            "duration" to "permanent",
            "scope" to "history"
        )
        
        val queryString = params.entries.joinToString("&") { "${it.key}=${it.value}" }
        return "https://www.reddit.com/api/v1/authorize?$queryString"
    }

    fun handleCallback(code: String, state: String): RedditIntegration {
        // Get current authenticated user's email
        val userEmail = try {
            AuthenticationService.getAccountFromContext().email
        } catch (e: Exception) {
            // Fallback: try to get from state storage
            stateStorage.remove(state) 
                ?: throw RuntimeException("User not authenticated and state not found")
        }
        
        val tokenResponse = exchangeCodeForToken(code)
        
        val integration = RedditIntegration(
            id = UUID.randomUUID(),
            userEmail = userEmail,
            redditUsername = null, // Will be fetched later
            accessToken = tokenResponse.accessToken,
            refreshToken = tokenResponse.refreshToken,
            tokenExpiresAt = LocalDateTime.now().plusSeconds(tokenResponse.expiresIn),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        return integrationRepository.save(integration)
    }

    private fun exchangeCodeForToken(code: String): TokenResponse {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_FORM_URLENCODED
            setBasicAuth(clientId, clientSecret)
            set("User-Agent", "YetAnotherPlanner/1.0")
        }
        
        val body = LinkedMultiValueMap<String, String>().apply {
            add("grant_type", "authorization_code")
            add("code", code)
            add("redirect_uri", redirectUri)
        }
        
        val request = HttpEntity(body, headers)
        val response = restTemplate.exchange(
            "https://www.reddit.com/api/v1/access_token",
            HttpMethod.POST,
            request,
            TokenResponse::class.java
        )
        
        return response.body ?: throw RuntimeException("Failed to get token response")
    }

    fun refreshToken(integration: RedditIntegration): RedditIntegration {
        if (integration.refreshToken == null) {
            throw RuntimeException("No refresh token available")
        }
        
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_FORM_URLENCODED
            setBasicAuth(clientId, clientSecret)
            set("User-Agent", "YetAnotherPlanner/1.0")
        }
        
        val body = LinkedMultiValueMap<String, String>().apply {
            add("grant_type", "refresh_token")
            add("refresh_token", integration.refreshToken)
        }
        
        val request = HttpEntity(body, headers)
        val response = restTemplate.exchange(
            "https://www.reddit.com/api/v1/access_token",
            HttpMethod.POST,
            request,
            TokenResponse::class.java
        )
        
        val tokenResponse = response.body ?: throw RuntimeException("Failed to refresh token")
        
        val updatedIntegration = integration.copy(
            accessToken = tokenResponse.accessToken,
            refreshToken = tokenResponse.refreshToken ?: integration.refreshToken,
            tokenExpiresAt = LocalDateTime.now().plusSeconds(tokenResponse.expiresIn),
            updatedAt = LocalDateTime.now()
        )
        
        return integrationRepository.save(updatedIntegration)
    }
}

data class TokenResponse(
    @com.fasterxml.jackson.annotation.JsonProperty("access_token")
    val accessToken: String,
    
    @com.fasterxml.jackson.annotation.JsonProperty("token_type")
    val tokenType: String,
    
    @com.fasterxml.jackson.annotation.JsonProperty("expires_in")
    val expiresIn: Long,
    
    @com.fasterxml.jackson.annotation.JsonProperty("refresh_token")
    val refreshToken: String?,
    
    @com.fasterxml.jackson.annotation.JsonProperty("scope")
    val scope: String
)