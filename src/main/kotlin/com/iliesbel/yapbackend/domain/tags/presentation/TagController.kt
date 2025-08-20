package com.iliesbel.yapbackend.domain.tags.presentation

import com.iliesbel.yapbackend.domain.tags.domain.Tag
import com.iliesbel.yapbackend.domain.tags.service.*
import com.iliesbel.yapbackend.domain.users.persistence.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tags")
@CrossOrigin(origins = ["http://localhost:3000"])
class TagController(
    private val tagService: TagService,
    private val taskTagService: TaskTagService,
    private val userRepository: UserRepository
) {

    @PostMapping
    fun createTag(
        @RequestBody request: CreateTagRequest,
        authentication: Authentication
    ): ResponseEntity<TagResponse> {
        val currentUser = userRepository.getCurrentUser()
        val userId = currentUser.id!!
        val tag = tagService.createTag(userId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(TagResponse.from(tag))
    }

    @GetMapping
    fun getUserTags(
        @RequestParam(defaultValue = "") search: String?,
        @RequestParam(defaultValue = "false") archived: Boolean,
        @RequestParam(defaultValue = "name") sort: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        authentication: Authentication
    ): ResponseEntity<Page<TagResponse>> {
        val currentUser = userRepository.getCurrentUser()
        val userId = currentUser.id!!
        val sortOption = when (sort.lowercase()) {
            "usage" -> TagSortOption.USAGE
            "created" -> TagSortOption.CREATED
            else -> TagSortOption.NAME
        }

        val pageable = PageRequest.of(page, size)
        val tags = tagService.getUserTags(userId, search, archived, sortOption, pageable)

        return ResponseEntity.ok(tags.map { TagResponse.from(it) })
    }

    @GetMapping("/{tagId}")
    fun getTag(
        @PathVariable tagId: Long,
        authentication: Authentication
    ): ResponseEntity<TagResponse> {
        val currentUser = userRepository.getCurrentUser()
        val userId = currentUser.id!!
        val tag = tagService.getTag(userId, tagId)
        return ResponseEntity.ok(TagResponse.from(tag))
    }

    @GetMapping("/popular")
    fun getMostUsedTags(
        @RequestParam(defaultValue = "10") limit: Int,
        authentication: Authentication
    ): ResponseEntity<List<TagResponse>> {
        val currentUser = userRepository.getCurrentUser()
        val userId = currentUser.id!!
        val tags = tagService.getMostUsedTags(userId, limit)
        return ResponseEntity.ok(tags.map { TagResponse.from(it) })
    }

    @PutMapping("/{tagId}")
    fun updateTag(
        @PathVariable tagId: Long,
        @RequestBody request: UpdateTagRequest,
        authentication: Authentication
    ): ResponseEntity<TagResponse> {
        val currentUser = userRepository.getCurrentUser()
        val userId = currentUser.id!!
        val tag = tagService.updateTag(userId, tagId, request)
        return ResponseEntity.ok(TagResponse.from(tag))
    }

    @PutMapping("/{tagId}/archive")
    fun archiveTag(
        @PathVariable tagId: Long,
        authentication: Authentication
    ): ResponseEntity<TagResponse> {
        val currentUser = userRepository.getCurrentUser()
        val userId = currentUser.id!!
        val tag = tagService.archiveTag(userId, tagId)
        return ResponseEntity.ok(TagResponse.from(tag))
    }

    @PutMapping("/{tagId}/unarchive")
    fun unarchiveTag(
        @PathVariable tagId: Long,
        authentication: Authentication
    ): ResponseEntity<TagResponse> {
        val currentUser = userRepository.getCurrentUser()
        val userId = currentUser.id!!
        val tag = tagService.unarchiveTag(userId, tagId)
        return ResponseEntity.ok(TagResponse.from(tag))
    }

    @DeleteMapping("/{tagId}")
    fun deleteTag(
        @PathVariable tagId: Long,
        @RequestParam(defaultValue = "true") removeFromTasks: Boolean,
        authentication: Authentication
    ): ResponseEntity<Void> {
        val currentUser = userRepository.getCurrentUser()
        val userId = currentUser.id!!
        tagService.deleteTag(userId, tagId, removeFromTasks)
        return ResponseEntity.noContent().build()
    }

    // Task-Tag assignment endpoints
    @PostMapping("/tasks/{taskId}/tags")
    fun assignTagsToTask(
        @PathVariable taskId: Long,
        @RequestBody request: AssignTagsRequest,
        authentication: Authentication
    ): ResponseEntity<List<TagResponse>> {
        val currentUser = userRepository.getCurrentUser()
        val userId = currentUser.id!!
        val tags = taskTagService.assignTagsToTask(userId, taskId, request.tagIds)
        return ResponseEntity.ok(tags.map { TagResponse.from(it) })
    }

    @DeleteMapping("/tasks/{taskId}/tags")
    fun removeTagsFromTask(
        @PathVariable taskId: Long,
        @RequestBody request: RemoveTagsRequest,
        authentication: Authentication
    ): ResponseEntity<List<TagResponse>> {
        val currentUser = userRepository.getCurrentUser()
        val userId = currentUser.id!!
        val tags = taskTagService.removeTagsFromTask(userId, taskId, request.tagIds)
        return ResponseEntity.ok(tags.map { TagResponse.from(it) })
    }

    @GetMapping("/tasks/{taskId}/tags")
    fun getTaskTags(
        @PathVariable taskId: Long,
        authentication: Authentication
    ): ResponseEntity<List<TagResponse>> {
        val currentUser = userRepository.getCurrentUser()
        val userId = currentUser.id!!
        val tags = taskTagService.getTaskTags(userId, taskId)
        return ResponseEntity.ok(tags.map { TagResponse.from(it) })
    }

    @PostMapping("/tasks/bulk/tags")
    fun bulkAssignTags(
        @RequestBody request: BulkTagOperationRequest,
        authentication: Authentication
    ): ResponseEntity<BulkTagOperationResponse> {
        val currentUser = userRepository.getCurrentUser()
        val userId = currentUser.id!!
        val response = taskTagService.bulkAssignTags(userId, request)
        return ResponseEntity.ok(response)
    }
}

data class TagResponse(
    val id: Long?,
    val name: String,
    val color: String,
    val description: String?,
    val usageCount: Int,
    val createdAt: String,
    val updatedAt: String,
    val isArchived: Boolean
) {
    companion object {
        fun from(tag: Tag): TagResponse {
            return TagResponse(
                id = tag.id,
                name = tag.name,
                color = tag.color,
                description = tag.description,
                usageCount = tag.usageCount,
                createdAt = tag.createdAt.toString(),
                updatedAt = tag.updatedAt.toString(),
                isArchived = tag.isArchived
            )
        }
    }
}

data class AssignTagsRequest(
    val tagIds: List<Long>
)

data class RemoveTagsRequest(
    val tagIds: List<Long>
)