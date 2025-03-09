package com.iliesbel.yapbackend.domain.contexts

import com.iliesbel.yapbackend.domain.contexts.domain.*
import com.iliesbel.yapbackend.infra.userAgent.UserAgent
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ContextController(
    private val contextService: ContextService,
    private val contextCreationService: ContextCreationService
) {

    @GetMapping("/contexts")
    fun getAllContexts(): List<Context> {
        TODO()
    }

    @GetMapping("/contexts/current")
    fun getCurrent(userAgent: UserAgent): UserContexts {
        return contextService.getCurrentContexts(userAgent)
    }


    @PostMapping("/contexts")
    fun createContext(contextCreation: ContextCreation, userAgent: UserAgent): Long {
        return contextCreationService.create(contextCreation, userAgent)
    }
}

data class ContextCreation(
    val type: ContextType,
    val name: String,
)


