package com.iliesbel.yapbackend.infra.notifications

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.security.Security

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



@Configuration
class PushNotificationProperties {

    private val logger = LoggerFactory.getLogger(NotificationProperties::class.java)
    init {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            logger.info("[BouncyCastleProvider] setting security to BouncyCastle")
            Security.insertProviderAt(BouncyCastleProvider(), 1)
        }
    }
}