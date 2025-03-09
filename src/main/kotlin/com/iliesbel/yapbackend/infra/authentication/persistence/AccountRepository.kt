package com.iliesbel.yapbackend.infra.authentication.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface AccountRepository : JpaRepository<AccountEntity, Long>, JpaSpecificationExecutor<AccountEntity> {
    fun findByEmail(name: String): AccountEntity?
}


