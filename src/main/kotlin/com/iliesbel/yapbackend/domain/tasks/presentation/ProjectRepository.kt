package com.iliesbel.yapbackend.domain.tasks.presentation

import com.iliesbel.yapbackend.domain.tasks.persistence.ProjectEntity
import org.springframework.data.jpa.repository.JpaRepository

class ProjectRepository {

}


interface ProjectJpaRepository : JpaRepository<ProjectEntity, Long> {

}

