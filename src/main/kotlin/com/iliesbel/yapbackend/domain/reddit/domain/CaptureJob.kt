package com.iliesbel.yapbackend.domain.reddit.domain

import java.util.*

data class CaptureJob(
    val id: UUID,
    val userEmail: String,
    val status: CaptureStatus,
    val progress: CaptureProgress,
    val errorMessage: String? = null
)

enum class CaptureStatus {
    IN_PROGRESS,
    COMPLETED,
    FAILED
}

data class CaptureProgress(
    val totalPosts: Int = 0,
    val processed: Int = 0,
    val createdTasks: Int = 0,
    val duplicatesSkipped: Int = 0
)