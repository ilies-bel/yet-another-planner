package com.iliesbel.yapbackend.domain.contexts.persistence

import com.iliesbel.yapbackend.domain.contexts.Context
import com.iliesbel.yapbackend.infra.authentication.AuthenticationService
import org.springframework.stereotype.Repository

@Repository
class ContextRepository(
    private val contextJpaRepository: ContextJpaRepository
) {
    fun findByUser(): List<Context> {
        val authentication = AuthenticationService.getUserFromContext()

        return contextJpaRepository.findByUserEmail(authentication.email).map {
            Context(
                id = it.id!!,
                name = it.name,
                type = it.type,
                deviceIdentifier = it.deviceIdentifier
            )
        }
    }

    fun save(context: ContextEntity): ContextEntity = contextJpaRepository.save(context)

}


