package com.iliesbel.yapbackend.domain.contexts.domain

import com.iliesbel.yapbackend.domain.contexts.ContextCreation
import com.iliesbel.yapbackend.domain.contexts.persistence.ContextEntity
import com.iliesbel.yapbackend.domain.contexts.persistence.ContextRepository
import com.iliesbel.yapbackend.infra.authentication.AuthenticationService
import com.iliesbel.yapbackend.infra.userAgent.UserAgent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ContextCreationService(
    private val contextRepository: ContextRepository,
) {
    fun create(contextCreation: ContextCreation, userAgent: UserAgent): Long {
        return when (contextCreation.type) {
            ContextType.DEVICE -> {
                contextRepository.save(
                    ContextEntity(
                        name = contextCreation.name,
                        type = ContextType.DEVICE,
                        userEmail = AuthenticationService.getUserFromContext().email,
                        deviceIdentifier = userAgent.deviceId!!
                    )
                ).id!!
            }

            else -> {
                TODO()
            }
        }
    }
}

data class DeviceIdentifier(
    val deviceId: String,
    val operatingSystem: String,
    val deviceType: Platform,
)

enum class Platform {
    MAC,
    WINDOWS,
    LINUX,
    ANDROID,
    IOS,
    UNKNOWN,
    ;


    companion object {
        private val logger: Logger = LoggerFactory.getLogger(Platform::class.java)


        fun fromString(value: String): Platform {
            return when (value) {
                "MAC" -> MAC
                "WINDOWS" -> WINDOWS
                "LINUX" -> LINUX
                "ANDROID" -> ANDROID
                "IOS" -> IOS
                else -> {
                    logger.warn("Unknown platform $value")
                    UNKNOWN
                }
            }
        }
    }
}
