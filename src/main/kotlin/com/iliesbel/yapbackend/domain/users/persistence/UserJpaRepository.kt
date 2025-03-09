package com.iliesbel.yapbackend.domain.users.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository : JpaRepository<UserEntity, Long> {
    fun getByEmail(email: String): UserEntity

    fun findByEmail(email: String): UserEntity?
}