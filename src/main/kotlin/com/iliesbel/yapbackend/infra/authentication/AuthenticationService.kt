package com.iliesbel.yapbackend.infra.authentication

import com.iliesbel.yapbackend.infra.authentication.user.Role
import com.iliesbel.yapbackend.infra.authentication.user.UserEntity
import com.iliesbel.yapbackend.infra.authentication.user.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class AuthenticationService(
    private val userRepository: UserRepository,
    private val authenticationManager: AuthenticationManager,
    private val passwordEncoder: PasswordEncoder,
) {

    fun signup(input: UserToCreate): Long {
        return userRepository.save(
            UserEntity(
                name = input.username,
                role = Role.USER,
                hashedPassword = passwordEncoder.encode(input.password),
            )
        ).id
    }

    fun authenticate(input: LoginUserDto): UserEntity {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                input.username,
                input.password,
            )
        )

        return userRepository.findByName(input.username)
            ?: throw IllegalArgumentException("User not found")
    }

    companion object {
        fun getUserFromContext(): UserEntity {
            val user = SecurityContextHolder.getContext().authentication.principal
            if (user == "anonymousUser") {
                throw UsernameNotFoundException("User not found")
            }

            return user as UserEntity
        }
    }
}

data class AuthenticatedUser(
    val id: Long,
    val username: String,
    val role: Role,
)


data class UserToCreate(
    val username: String,
    val password: String,
)

