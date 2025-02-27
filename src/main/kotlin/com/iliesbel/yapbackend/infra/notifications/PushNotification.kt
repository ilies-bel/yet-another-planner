package com.iliesbel.yapbackend.infra.notifications

import com.iliesbel.yapbackend.infra.authentication.AuthenticationService
import nl.martijndwars.webpush.Notification
import nl.martijndwars.webpush.PushService
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.springframework.stereotype.Service
import java.security.Security


// 4. Create a service to handle push notifications (PushNotificationService.java)
@Service
class PushNotificationService(
    private val subscriptionRepository: PushSubscriptionRepository,
    private val authenticationService: AuthenticationService,
    notificationProperties: NotificationProperties,
) {
    init {
        // Add Bouncy Castle provider if not present
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(BouncyCastleProvider())
        }
    }

    private val pushService = PushService(
        notificationProperties.publicKey,
        notificationProperties.privateKey,
    )


    fun subscribe(request: SubscriptionRequest) {
        val currentUser = AuthenticationService.getUserFromContext()

        val subscription = PushSubscription(
            userEmail = currentUser.username,
            endpoint = request.endpoint,
            p256dh = request.keys.p256dh,
            auth = request.keys.auth
        )
        subscriptionRepository.save(subscription)
    }

    fun sendNotification(message: String) {
        val subscriptions = subscriptionRepository.findAll()

        for (subscription in subscriptions) {

            val notification = Notification(
                subscription.endpoint,
                subscription.p256dh,
                subscription.auth,
                message.toByteArray()
            )

            // Send notification
            pushService.send(notification)
        }
    }
}

