package com.iliesbel.yapbackend.tasks.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface TaskJpaRepository : JpaRepository<TaskEntity, Long> {}