package com.iliesbel.yapbackend.tasks.presentation

import com.iliesbel.yapbackend.tasks.persistence.ProjectEntity
import org.springframework.data.jpa.repository.JpaRepository

class ProjectRepository {

}


interface ProjectJpaRepository : JpaRepository<ProjectEntity, Long> {

}

