package com.iliesbel.yapbackend.domain.tags.service

import com.iliesbel.yapbackend.domain.tags.domain.Tag
import com.iliesbel.yapbackend.domain.tags.persistence.TagRepository
import com.iliesbel.yapbackend.domain.tasks.persistence.TaskJpaRepository
import com.iliesbel.yapbackend.infra.exception.ApiException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TaskTagService(
    private val taskJpaRepository: TaskJpaRepository,
    private val tagRepository: TagRepository,
    private val tagService: TagService
) {
    
    companion object {
        const val MAX_TAGS_PER_TASK = 20
    }
    
    @Transactional
    fun assignTagsToTask(userId: Long, taskId: Long, tagIds: List<Long>): List<Tag> {
        val task = taskJpaRepository.findByIdAndUserId(taskId, userId)
            ?: throw ApiException("Task not found", HttpStatus.NOT_FOUND)
        
        // Validate tag limit
        val totalTags = task.tags.size + tagIds.size
        if (totalTags > MAX_TAGS_PER_TASK) {
            throw ApiException("Cannot exceed $MAX_TAGS_PER_TASK tags per task", HttpStatus.BAD_REQUEST)
        }
        
        // Fetch and validate tags
        val tags = tagIds.map { tagId ->
            tagRepository.findByIdAndUserId(tagId, userId)
                ?: throw ApiException("Tag with id $tagId not found", HttpStatus.NOT_FOUND)
        }
        
        // Add tags to task and increment usage count
        tags.forEach { tag ->
            if (!task.tags.contains(tag)) {
                task.tags.add(tag)
                tagRepository.incrementUsageCount(tag.id!!)
            }
        }
        
        taskJpaRepository.save(task)
        
        return task.tags.map { tagService.toTag(it) }
    }
    
    @Transactional
    fun removeTagsFromTask(userId: Long, taskId: Long, tagIds: List<Long>): List<Tag> {
        val task = taskJpaRepository.findByIdAndUserId(taskId, userId)
            ?: throw ApiException("Task not found", HttpStatus.NOT_FOUND)
        
        // Remove tags from task and decrement usage count
        tagIds.forEach { tagId ->
            val tag = task.tags.find { it.id == tagId }
            if (tag != null) {
                task.tags.remove(tag)
                tagRepository.decrementUsageCount(tag.id!!)
            }
        }
        
        taskJpaRepository.save(task)
        
        return task.tags.map { tagService.toTag(it) }
    }
    
    @Transactional
    fun bulkAssignTags(userId: Long, request: BulkTagOperationRequest): BulkTagOperationResponse {
        val tasks = taskJpaRepository.findAllByIdInAndUserId(request.taskIds, userId)
        
        if (tasks.size != request.taskIds.size) {
            throw ApiException("Some tasks were not found", HttpStatus.NOT_FOUND)
        }
        
        // Fetch tags to add
        val tagsToAdd = request.addTagIds?.map { tagId ->
            tagRepository.findByIdAndUserId(tagId, userId)
                ?: throw ApiException("Tag with id $tagId not found", HttpStatus.NOT_FOUND)
        } ?: emptyList()
        
        // Fetch tags to remove
        val tagsToRemove = request.removeTagIds?.map { tagId ->
            tagRepository.findByIdAndUserId(tagId, userId)
                ?: throw ApiException("Tag with id $tagId not found", HttpStatus.NOT_FOUND)
        } ?: emptyList()
        
        var modifiedCount = 0
        
        tasks.forEach { task ->
            var modified = false
            
            // Add tags
            tagsToAdd.forEach { tag ->
                if (!task.tags.contains(tag) && task.tags.size < MAX_TAGS_PER_TASK) {
                    task.tags.add(tag)
                    tagRepository.incrementUsageCount(tag.id!!)
                    modified = true
                }
            }
            
            // Remove tags
            tagsToRemove.forEach { tag ->
                if (task.tags.contains(tag)) {
                    task.tags.remove(tag)
                    tagRepository.decrementUsageCount(tag.id!!)
                    modified = true
                }
            }
            
            if (modified) {
                taskJpaRepository.save(task)
                modifiedCount++
            }
        }
        
        return BulkTagOperationResponse(
            totalTasks = tasks.size,
            modifiedTasks = modifiedCount,
            addedTags = tagsToAdd.map { tagService.toTag(it) },
            removedTags = tagsToRemove.map { tagService.toTag(it) }
        )
    }
    
    @Transactional(readOnly = true)
    fun getTaskTags(userId: Long, taskId: Long): List<Tag> {
        val task = taskJpaRepository.findByIdAndUserId(taskId, userId)
            ?: throw ApiException("Task not found", HttpStatus.NOT_FOUND)
        
        return task.tags.map { tagService.toTag(it) }
    }
}

data class BulkTagOperationRequest(
    val taskIds: List<Long>,
    val addTagIds: List<Long>? = null,
    val removeTagIds: List<Long>? = null
)

data class BulkTagOperationResponse(
    val totalTasks: Int,
    val modifiedTasks: Int,
    val addedTags: List<Tag>,
    val removedTags: List<Tag>
)