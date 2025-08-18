package com.iliesbel.yapbackend.domain.reddit.service

import com.iliesbel.yapbackend.domain.reddit.domain.RedditIntegration
import com.iliesbel.yapbackend.domain.reddit.domain.RedditPost
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class RedditApiClient(
    private val restTemplate: RestTemplate = RestTemplate()
) {

    fun fetchSavedPosts(integration: RedditIntegration): List<RedditPost> {
        val allPosts = mutableListOf<RedditPost>()
        var after: String? = null
        
        do {
            val response = fetchSavedPostsPage(integration, after)
            allPosts.addAll(response.posts)
            after = response.after
        } while (after != null && allPosts.size < 1000) // Limit to prevent excessive requests
        
        return allPosts
    }
    
    private fun fetchSavedPostsPage(integration: RedditIntegration, after: String?): SavedPostsResponse {
        val headers = HttpHeaders().apply {
            setBearerAuth(integration.accessToken)
            set("User-Agent", "YetAnotherPlanner/1.0")
        }
        
        val url = buildUrl(integration.redditUsername, after)
        val request = HttpEntity<String>(headers)
        
        val response = try {
            restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                RedditApiResponse::class.java
            )
        } catch (e: Exception) {
            throw RuntimeException("Failed to fetch saved posts from Reddit: ${e.message}", e)
        }
        
        val apiResponse = response.body ?: throw RuntimeException("Failed to fetch saved posts")
        return parseSavedPosts(apiResponse)
    }
    
    private fun buildUrl(username: String?, after: String?): String {
        // Use username if available, fallback to "me"
        val user = username ?: "me"
        var url = "https://oauth.reddit.com/user/$user/saved?limit=100&raw_json=1"
        if (after != null) {
            url += "&after=$after"
        }
        return url
    }
    
    private fun parseSavedPosts(apiResponse: RedditApiResponse): SavedPostsResponse {
        val posts = apiResponse.data.children
            .filter { it.kind == "t3" } // Only submissions (posts), not comments
            .map { child ->
                val post = child.data
                RedditPost(
                    id = post.id,
                    title = post.title,
                    url = post.url,
                    permalink = "https://reddit.com${post.permalink}",
                    subreddit = post.subreddit,
                    author = post.author,
                    selftext = post.selftext?.takeIf { it.isNotBlank() },
                    createdUtc = post.created_utc.toLong(),
                    isVideo = post.is_video ?: false,
                    isSelf = post.is_self
                )
            }
        
        return SavedPostsResponse(
            posts = posts,
            after = apiResponse.data.after
        )
    }
}

data class SavedPostsResponse(
    val posts: List<RedditPost>,
    val after: String?
)

// Reddit API response structure
data class RedditApiResponse(
    val kind: String,
    val data: RedditListingData
)

data class RedditListingData(
    val after: String?,
    val before: String?,
    val children: List<RedditChild>
)

data class RedditChild(
    val kind: String,
    val data: RedditPostData
)

data class RedditPostData(
    val id: String,
    val title: String,
    val url: String,
    val permalink: String,
    val subreddit: String,
    val author: String,
    val selftext: String?,
    val created_utc: Double,
    val is_self: Boolean,
    val is_video: Boolean?
)