package com.iliesbel.yapbackend.domain.users

import com.iliesbel.yapbackend.domain.users.persistence.DeviceEntity
import com.iliesbel.yapbackend.domain.users.persistence.UserEntity
import com.iliesbel.yapbackend.domain.users.persistence.UserRepository
import com.iliesbel.yapbackend.infra.authentication.AuthenticationService
import com.iliesbel.yapbackend.infra.userAgent.UserAgent
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserService(private val userRepository: UserRepository) {

    @Transactional
    fun saveUser(userToCreate: UserToCreate): Long {
        val user = AuthenticationService.getUserFromContext()

        val foundUser = userRepository.findByEmail(user.email)

        return userRepository.save(
            UserEntity(
                id = foundUser?.id,
                email = user.email,
                name = userToCreate.name,
            )
        ).id!!
    }

    @Transactional
    fun addDeviceToUser(userAgent: UserAgent): UUID {
        val account = AuthenticationService.getUserFromContext()

        val user = userRepository.getByEmail(account.email)

        if (userAgent.deviceId == null) {
            throw IllegalArgumentException("Device id is required in the header")
        }

        user.devices.add(
            DeviceEntity(
                id = userAgent.deviceId,
                name = null,
                type = userAgent.deviceType,
                platform = userAgent.platform,
                lastPlatformVersion = userAgent.platformVersion,
                browser = userAgent.browser,
                user = user
            )
        )

        return userAgent.deviceId
    }
}
