package com.iliesbel.yapbackend.infra.authentication.presentation

data class UserToCreate(
    val email: String,
    val password: String,
)