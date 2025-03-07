package com.iliesbel.yapbackend.infra.authentication.presentation

data class LoginUserDto(
    val email: String,
    val password: String,
)