package com.iliesbel.yapbackend.infra.authentication

import com.iliesbel.yapbackend.infra.authentication.persistence.AccountEntity
import com.iliesbel.yapbackend.infra.authentication.persistence.AccountRepository
import com.iliesbel.yapbackend.infra.authentication.persistence.Role
import com.iliesbel.yapbackend.infra.authentication.presentation.AccountToCreate
import com.iliesbel.yapbackend.infra.authentication.presentation.LoginUserDto
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class AuthenticationService(
    private val accountRepository: AccountRepository,
    private val authenticationManager: AuthenticationManager,
    private val passwordEncoder: PasswordEncoder,
) {

    @Transactional
    fun signup(input: AccountToCreate): Long {
        return accountRepository.save(
            AccountEntity(
                email = input.email,
                role = Role.USER,
                hashedPassword = passwordEncoder.encode(input.password),
            )
        ).id
    }

    fun authenticate(input: LoginUserDto): AccountEntity {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                input.email,
                input.password,
            )
        )

        return accountRepository.findByEmail(input.email)
            ?: throw IllegalArgumentException("User not found")
    }

    companion object {
        fun getUserFromContext(): AccountEntity {
            val account = SecurityContextHolder.getContext().authentication.principal
            if (account == "anonymousUser") {
                throw UsernameNotFoundException("User not found")
            }

            return account as AccountEntity
        }
    }
}

