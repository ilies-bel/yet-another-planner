package com.iliesbel.yapbackend.domain.reddit.domain

data class RedditPost(
    val id: String,
    val title: String,
    val selftext: String?,
    val url: String,
    val permalink: String,
    val subreddit: String,
    val author: String,
    val createdUtc: Long,
    val isVideo: Boolean,
    val isSelf: Boolean
)

data class RedditSavedResponse(
    val data: RedditSavedData
)

data class RedditSavedData(
    val children: List<RedditPostWrapper>,
    val after: String?,
    val before: String?
)

data class RedditPostWrapper(
    val data: RedditPost
)