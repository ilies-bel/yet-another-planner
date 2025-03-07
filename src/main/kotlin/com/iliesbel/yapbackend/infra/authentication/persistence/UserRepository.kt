package com.iliesbel.yapbackend.infra.authentication.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface UserRepository : JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {
    fun findByName(name: String): UserEntity?

    fun findByEmail(name: String): UserEntity?
}


