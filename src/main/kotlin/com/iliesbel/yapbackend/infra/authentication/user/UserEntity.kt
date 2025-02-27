package com.iliesbel.yapbackend.infra.authentication.user

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "people")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = -1,

    var name: String,


    @Column(name = "password_hash")
    var hashedPassword: String,

    @Enumerated(EnumType.STRING)
    var role: Role,

    ) : UserDetails
{

    override fun getUsername(): String {
        return this.name
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {

        return listOf(GrantedAuthority { role.name })
    }

    override fun getPassword(): String? {
        return hashedPassword
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserEntity) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}


enum class Role {
USER, ADMIN
}