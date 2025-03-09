package com.iliesbel.yapbackend.infra.authentication.presentation

data class AccountToCreate(
    val email: String,
    val password: String,
)