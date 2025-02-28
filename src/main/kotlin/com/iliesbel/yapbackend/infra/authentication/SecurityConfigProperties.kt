package com.iliesbel.yapbackend.infra.authentication

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "security.jwt", ignoreUnknownFields = false)
data class SecurityConfigProperties(
    val secretKey: String,
    var expirationTime: Long = 0,
)