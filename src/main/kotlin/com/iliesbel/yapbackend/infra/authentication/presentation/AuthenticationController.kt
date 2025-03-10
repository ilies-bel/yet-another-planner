package com.iliesbel.yapbackend.infra.authentication.presentation

import com.iliesbel.yapbackend.infra.authentication.AuthenticationService
import com.iliesbel.yapbackend.infra.authentication.JwtService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController


@RestController
class AuthenticationController(
    private val authenticationService: AuthenticationService,
    private val jwtService: JwtService,
) {
    @PostMapping("/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@RequestBody accountToCreate: AccountToCreate): Long {
        return authenticationService.signup(accountToCreate)
    }

    @PostMapping("/auth/login")
    fun login(@RequestBody loginUserDto: LoginUserDto): LoginResponse {
        val user = authenticationService.authenticate(loginUserDto)

        val jwtToken: String = jwtService.generateToken(user)

        return LoginResponse(jwtToken, jwtService.getExpirationTime())
    }

    @PostMapping("/auth/refresh")
    fun login(): LoginResponse {
        val user = AuthenticationService.getAccountFromContext()

        val jwtToken: String = jwtService.generateToken(user)

        return LoginResponse(jwtToken, jwtService.getExpirationTime())
    }
}

data class LoginResponse(
    val token: String,
    val expiresIn: Long,
)