package com.iliesbel.yapbackend.domain.tasks.service.model

enum class TaskStatus(val sequence: Int) {
    CAPTURED(1),      // Initial capture, needs refinement
    TO_REFINE(2),     // Needs clarification or more details
    SOMEDAY(3),       // Will do eventually, not prioritized
    TODO(4),          // Ready to be worked on
    IN_PROGRESS(5),   // Currently being worked on
    DONE(6);          // Completed
    
    companion object {
        fun getByName(name: String): TaskStatus? = values().find { it.name == name }
    }
}
