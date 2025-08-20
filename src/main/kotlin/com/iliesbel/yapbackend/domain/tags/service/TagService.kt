package com.iliesbel.yapbackend.domain.tags.service

import com.iliesbel.yapbackend.domain.tags.domain.Tag
import com.iliesbel.yapbackend.domain.tags.persistence.TagEntity
import com.iliesbel.yapbackend.domain.tags.persistence.TagRepository
import com.iliesbel.yapbackend.domain.users.persistence.UserJpaRepository
import com.iliesbel.yapbackend.infra.exception.ApiException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TagService(
    private val tagRepository: TagRepository,
    private val userJpaRepository: UserJpaRepository
) {

    companion object {
        const val MAX_TAGS_PER_USER = 500
        const val MAX_TAG_NAME_LENGTH = 50
        const val DEFAULT_TAG_COLOR = "#808080"
        val COLOR_REGEX = Regex("^#[0-9A-Fa-f]{6}$")
    }

    @Transactional
    fun createTag(userId: Long, request: CreateTagRequest): Tag {
        validateTagRequest(request)

        val user = userJpaRepository.findById(userId)
            .orElseThrow { ApiException("User not found", HttpStatus.NOT_FOUND) }

        // Check if user has reached the tag limit
        val tagCount = tagRepository.countByUserId(userId)
        if (tagCount >= MAX_TAGS_PER_USER) {
            throw ApiException("Maximum tag limit reached ($MAX_TAGS_PER_USER tags)", HttpStatus.BAD_REQUEST)
        }

        // Check if tag name already exists for this user
        tagRepository.findByUserIdAndNameIgnoreCase(userId, request.name)?.let {
            throw ApiException("Tag with name '${request.name}' already exists", HttpStatus.CONFLICT)
        }

        val tagEntity = TagEntity(
            user = user,
            name = request.name.trim(),
            color = request.color ?: DEFAULT_TAG_COLOR,
            description = request.description?.trim()
        )

        val savedTag = tagRepository.save(tagEntity)
        return toTag(savedTag)
    }

    @Transactional(readOnly = true)
    fun getTag(userId: Long, tagId: Long): Tag {
        val tag = tagRepository.findByIdAndUserId(tagId, userId)
            ?: throw ApiException("Tag not found", HttpStatus.NOT_FOUND)
        return toTag(tag)
    }

    @Transactional(readOnly = true)
    fun getUserTags(
        userId: Long,
        search: String?,
        archived: Boolean,
        sort: TagSortOption,
        pageable: Pageable
    ): Page<Tag> {
        val sortedPageable = when (sort) {
            TagSortOption.NAME -> PageRequest.of(pageable.pageNumber, pageable.pageSize, Sort.by("name").ascending())
            TagSortOption.USAGE -> PageRequest.of(
                pageable.pageNumber,
                pageable.pageSize,
                Sort.by("usageCount").descending()
            )

            TagSortOption.CREATED -> PageRequest.of(
                pageable.pageNumber,
                pageable.pageSize,
                Sort.by("createdAt").descending()
            )
        }

        val tags = if (!search.isNullOrBlank()) {
            tagRepository.searchByNameAndArchived(userId, search, archived, sortedPageable)
        } else {
            tagRepository.findByUserIdAndIsArchived(userId, archived, sortedPageable)
        }

        return tags.map { toTag(it) }
    }

    @Transactional(readOnly = true)
    fun getMostUsedTags(userId: Long, limit: Int = 10): List<Tag> {
        val pageable = PageRequest.of(0, limit)
        return tagRepository.findMostUsedTags(userId, pageable).map { toTag(it) }
    }

    @Transactional
    fun updateTag(userId: Long, tagId: Long, request: UpdateTagRequest): Tag {
        validateTagRequest(
            CreateTagRequest(
                name = request.name,
                color = request.color,
                description = request.description
            )
        )

        val tag = tagRepository.findByIdAndUserId(tagId, userId)
            ?: throw ApiException("Tag not found", HttpStatus.NOT_FOUND)

        // Check if new name conflicts with another tag
        if (request.name != tag.name) {
            tagRepository.findByUserIdAndNameIgnoreCase(userId, request.name)?.let {
                if (it.id != tagId) {
                    throw ApiException("Tag with name '${request.name}' already exists", HttpStatus.CONFLICT)
                }
            }
        }

        tag.name = request.name.trim()
        tag.color = request.color ?: tag.color
        tag.description = request.description?.trim()

        val updatedTag = tagRepository.save(tag)
        return toTag(updatedTag)
    }

    @Transactional
    fun archiveTag(userId: Long, tagId: Long): Tag {
        val tag = tagRepository.findByIdAndUserId(tagId, userId)
            ?: throw ApiException("Tag not found", HttpStatus.NOT_FOUND)

        tag.isArchived = true
        val archivedTag = tagRepository.save(tag)
        return toTag(archivedTag)
    }

    @Transactional
    fun unarchiveTag(userId: Long, tagId: Long): Tag {
        val tag = tagRepository.findByIdAndUserId(tagId, userId)
            ?: throw ApiException("Tag not found", HttpStatus.NOT_FOUND)

        tag.isArchived = false
        val unarchivedTag = tagRepository.save(tag)
        return toTag(unarchivedTag)
    }

    @Transactional
    fun deleteTag(userId: Long, tagId: Long, removeFromTasks: Boolean = true) {
        val tag = tagRepository.findByIdAndUserId(tagId, userId)
            ?: throw ApiException("Tag not found", HttpStatus.NOT_FOUND)

        if (removeFromTasks) {
            // The cascade delete will handle removing the tag from all tasks
            tagRepository.delete(tag)
        } else {
            // If we don't want to remove from tasks, we should archive instead
            archiveTag(userId, tagId)
        }
    }

    private fun validateTagRequest(request: CreateTagRequest) {
        if (request.name.isBlank()) {
            throw ApiException("Tag name cannot be empty", HttpStatus.BAD_REQUEST)
        }

        if (request.name.length > MAX_TAG_NAME_LENGTH) {
            throw ApiException("Tag name cannot exceed $MAX_TAG_NAME_LENGTH characters", HttpStatus.BAD_REQUEST)
        }

        request.color?.let { color ->
            if (!COLOR_REGEX.matches(color)) {
                throw ApiException("Invalid color format. Use hex format like #FF0000", HttpStatus.BAD_REQUEST)
            }
        }

        request.description?.let { description ->
            if (description.length > 500) {
                throw ApiException("Tag description cannot exceed 500 characters", HttpStatus.BAD_REQUEST)
            }
        }
    }

    fun toTag(entity: TagEntity): Tag {
        return Tag(
            id = entity.id,
            userId = entity.user.id!!,
            name = entity.name,
            color = entity.color,
            description = entity.description,
            usageCount = entity.usageCount,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            isArchived = entity.isArchived
        )
    }
}

data class CreateTagRequest(
    val name: String,
    val color: String? = null,
    val description: String? = null
)

data class UpdateTagRequest(
    val name: String,
    val color: String? = null,
    val description: String? = null
)

enum class TagSortOption {
    NAME,
    USAGE,
    CREATED
}