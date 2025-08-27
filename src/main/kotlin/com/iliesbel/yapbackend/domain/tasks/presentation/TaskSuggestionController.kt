package com.iliesbel.yapbackend.domain.tasks.presentation

import com.iliesbel.yapbackend.domain.tasks.service.TaskService
import com.iliesbel.yapbackend.domain.tasks.service.model.Task
import com.iliesbel.yapbackend.domain.tasks.service.model.TaskStatus
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.*

data class TaskSuggestion(
    val task: Task,
    val matchScore: Int
)

data class SuggestionRequest(
    val contextIds: List<Long> = emptyList(),
    val contextFilters: Map<String, String> = emptyMap()
)

@RestController
@RequestMapping("/tasks/suggestions")
class TaskSuggestionController(
    private val taskService: TaskService
) {
    
    @PostMapping("/best-matches")
    fun getBestMatches(@RequestBody request: SuggestionRequest): List<TaskSuggestion> {
        // Filter tasks based on context and status
        val filter = TaskPageFilter(
            status = listOf(TaskStatus.TODO),
            page = 0,
            size = 20, // Get more tasks to have better selection for scoring
            contextId = request.contextIds.firstOrNull(),
            timeContext = null,
            tagIds = null,
            tagMode = null
        )
        
        val allTasks = taskService.findAll(filter).content
        
        // Score tasks based on context filters
        val scoredTasks = allTasks.map { task ->
            val score = calculateContextMatchScore(task, request.contextFilters)
            TaskSuggestion(task = task, matchScore = score)
        }
        
        // Sort by score descending and take top 3
        return scoredTasks
            .sortedByDescending { it.matchScore }
            .take(3)
    }
    
    private fun calculateContextMatchScore(task: Task, contextFilters: Map<String, String>): Int {
        if (contextFilters.isEmpty()) {
            return (70..85).random() // Default score when no specific context is set
        }
        
        var baseScore = 50
        var matches = 0
        val totalFilters = contextFilters.size
        
        // Simple scoring based on context matches
        contextFilters.forEach { (filterType, filterValue) ->
            when (filterType) {
                "energy" -> {
                    // For now, assume all tasks can match any energy level
                    matches++
                    baseScore += 15
                }
                "location" -> {
                    // Tasks with no specific location requirement match any location
                    matches++
                    baseScore += when (filterValue) {
                        "home" -> 20
                        "office" -> 15  
                        "anywhere" -> 25
                        else -> 10
                    }
                }
                "device" -> {
                    // Most tasks assume computer availability
                    matches++
                    baseScore += when (filterValue) {
                        "computer" -> 20
                        "phone" -> 10
                        else -> 5
                    }
                }
                "mood" -> {
                    // Mood-based scoring
                    matches++
                    baseScore += when (filterValue) {
                        "focused" -> 15
                        "creative" -> 10
                        "social" -> 8
                        "admin" -> 12
                        else -> 5
                    }
                }
            }
        }
        
        // Add bonus for multiple context matches
        if (matches > 1) {
            baseScore += (matches - 1) * 5
        }
        
        // Add some randomness to make it feel more dynamic
        val randomVariation = (-5..5).random()
        
        return (baseScore + randomVariation).coerceIn(40, 100)
    }
}