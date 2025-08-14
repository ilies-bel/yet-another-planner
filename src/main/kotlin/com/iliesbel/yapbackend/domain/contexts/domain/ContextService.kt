package com.iliesbel.yapbackend.domain.contexts.domain

import com.iliesbel.yapbackend.domain.contexts.persistence.ContextRepository
import com.iliesbel.yapbackend.infra.userAgent.UserAgent
import org.springframework.stereotype.Service

@Service
class ContextService(private val contextRepository: ContextRepository) {

    fun getCurrentContexts(userAgent: UserAgent): UserContexts {
        val contexts = contextRepository.findByUser()

        return UserContexts(
            deviceContext = getDeviceContext(contexts, userAgent)
        )
    }

    fun getAllContexts(): List<Context> {
        return contextRepository.findByUser()
    }


    private fun getDeviceContext(contexts: List<Context>, userAgent: UserAgent): Context? {
        val deviceContexts = contexts.filter { it.type == ContextType.DEVICE }


        return deviceContexts.find {
            it.deviceIdentifier == userAgent.deviceId
        }
    }

}


data class UserContexts(
    val deviceContext: Context?
)

