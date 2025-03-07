package com.iliesbel.yapbackend.infra.authentication

import com.iliesbel.yapbackend.infra.authentication.persistence.Role
import com.iliesbel.yapbackend.infra.authentication.persistence.UserEntity
import com.iliesbel.yapbackend.infra.authentication.persistence.UserRepository
import com.iliesbel.yapbackend.infra.authentication.presentation.LoginUserDto
import com.iliesbel.yapbackend.infra.authentication.presentation.UserToCreate
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
                email = input.email,
                role = Role.USER,
                hashedPassword = passwordEncoder.encode(input.password),
                name = null,
            )
        ).id
    }

    fun authenticate(input: LoginUserDto): UserEntity {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                input.email,
                input.password,
            )
        )

        return userRepository.findByEmail(input.email)
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

