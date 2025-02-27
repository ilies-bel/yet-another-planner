package com.iliesbel.yapbackend.infra.notifications

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "notification")
data class NotificationProperties(
    val enabled: Boolean = false,
    val publicKey: String,
    val privateKey: String,
) {
    init {
        if (enabled && publicKey.isBlank()) {
            throw IllegalArgumentException("[Notification] Public key must not be blank")
        // todo ajouter le prefix de log avec une annotation
        }
    }
}



