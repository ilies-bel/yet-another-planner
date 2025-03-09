package com.iliesbel.yapbackend.infra.authentication.persistence

import com.iliesbel.yapbackend.domain.users.UserEntity
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "account")
class AccountEntity(
    @Id
    var id: Long = -1,

    val email: String,

    @Column(name = "password_hash")
    var hashedPassword: String,

    @Enumerated(EnumType.STRING)
    var role: Role,

    @OneToOne
    val user: UserEntity? = null,

    ) : UserDetails {

    override fun getUsername(): String {
        return this.email
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(GrantedAuthority { role.name })
    }

    override fun getPassword(): String {
        return hashedPassword
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AccountEntity) return false

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