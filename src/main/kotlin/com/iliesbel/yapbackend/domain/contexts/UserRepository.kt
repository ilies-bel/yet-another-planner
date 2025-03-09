package com.iliesbel.yapbackend.domain.contexts

import com.iliesbel.yapbackend.domain.users.UserEntity
import org.springframework.stereotype.Repository

@Repository
class UserRepository(private val userJpaRepository: UserJpaRepository) {
    fun getByEmail(email: String): UserEntity {
        return userJpaRepository.getByEmail(email)
    }

    fun findByEmail(email: String): UserEntity? {
        return userJpaRepository.findByEmail(email)
    }


    fun save(user: UserEntity): UserEntity {
        return userJpaRepository.save(user)
    }
}