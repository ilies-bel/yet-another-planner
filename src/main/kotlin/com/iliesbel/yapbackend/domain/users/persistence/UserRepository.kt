package com.iliesbel.yapbackend.domain.users.persistence

import com.iliesbel.yapbackend.domain.chatbot.domain.LoveScore
import com.iliesbel.yapbackend.domain.chatbot.domain.PersonalityScore
import com.iliesbel.yapbackend.domain.users.domain.User
import com.iliesbel.yapbackend.infra.authentication.AuthenticationService
import org.springframework.stereotype.Repository

@Repository
class UserRepository(private val userJpaRepository: UserJpaRepository) {
    fun getByEmail(email: String): UserEntity {
        return userJpaRepository.getByEmail(email)
    }

    fun findByEmail(email: String): UserEntity? {
        return userJpaRepository.findByEmail(email)
    }

    fun getCurrentUser(): User {
        return findCurrentUser() ?: throw NoSuchElementException("User not found")
    }

    fun findCurrentUser(): User? {
        val account = AuthenticationService.getAccountFromContext()

        val foundUser = userJpaRepository.findByEmail(account.email)

        return foundUser?.toDomain()
    }

    fun save(user: User): UserEntity {
        return userJpaRepository.save(user.toEntity())
    }
}

private fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        name = name,
        email = email,
        chatPersonalityScore = personalityScore.score,
        chatLoveScore = loveScore.score
    )
}

fun UserEntity.toDomain(): User {
    return User(
        id = id!!,
        name = name,
        email = email,
        personalityScore = PersonalityScore(chatPersonalityScore),
        loveScore = LoveScore(chatLoveScore)
    )
}
