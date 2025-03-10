package com.iliesbel.yapbackend.domain.users.domain

import com.iliesbel.yapbackend.domain.chatbot.domain.LoveScore
import com.iliesbel.yapbackend.domain.chatbot.domain.PersonalityScore
import com.iliesbel.yapbackend.domain.users.UserToCreate
import com.iliesbel.yapbackend.domain.users.persistence.DeviceEntity
import com.iliesbel.yapbackend.domain.users.persistence.UserRepository
import com.iliesbel.yapbackend.infra.authentication.AuthenticationService
import com.iliesbel.yapbackend.infra.userAgent.UserAgent
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserService(private val userRepository: UserRepository) {

    @Transactional
    fun createOrUpdate(userToCreate: UserToCreate): Long {
        val user = AuthenticationService.getAccountFromContext()

        val foundUser = userRepository.findByEmail(user.email)
            ?: return userRepository.save(
                User(
                    id = null,
                    email = user.email,
                    name = userToCreate.name,
                    personalityScore = PersonalityScore.initial,
                    loveScore = LoveScore.initial,
                )
            ).id!!



        return foundUser.apply {
            name = userToCreate.name
        }.id!!
    }

    @Transactional
    fun addDeviceToUser(userAgent: UserAgent): UUID {
        val account = AuthenticationService.getAccountFromContext()

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
