package com.iliesbel.yapbackend.tasks.presentation

import com.iliesbel.yapbackend.tasks.persistence.ContextEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ContextRepository : JpaRepository<ContextEntity, Long> {}
