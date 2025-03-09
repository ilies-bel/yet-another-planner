package com.iliesbel.yapbackend.domain.users

import jakarta.persistence.*
import org.hibernate.annotations.NaturalId


@Entity(name = "user_detail")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    var name: String?,

    @NaturalId
    val email: String,

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "user")
    val devices: MutableList<DeviceEntity> = mutableListOf()
)

