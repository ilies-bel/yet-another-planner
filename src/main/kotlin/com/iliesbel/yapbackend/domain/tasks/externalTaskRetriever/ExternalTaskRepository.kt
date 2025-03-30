package com.iliesbel.yapbackend.domain.tasks.externalTaskRetriever

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.NaturalId
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ExternalTaskRepository : JpaRepository<ExternalTaskEntity, String> {
    fun findByNaturalIdIn(naturalIds: Collection<String>): Set<ExternalTaskEntity>
}

@Entity
@Table(name = "external_task")
class ExternalTaskEntity(
    @Id
    val id: UUID,

    @NaturalId
    val naturalId: String,

    val name: String
)