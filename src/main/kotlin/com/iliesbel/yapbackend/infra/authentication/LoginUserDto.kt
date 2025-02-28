package com.iliesbel.yapbackend.infra.authentication

data class LoginUserDto(
    val username: String,
    val password: String,
)