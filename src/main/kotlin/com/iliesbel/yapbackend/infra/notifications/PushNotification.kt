package com.iliesbel.yapbackend.infra.notifications

import com.iliesbel.yapbackend.infra.authentication.AuthenticationService
import com.iliesbel.yapbackend.infra.notifications.persistence.PushSubscription
import com.iliesbel.yapbackend.infra.notifications.persistence.PushSubscriptionRepository
import com.iliesbel.yapbackend.infra.notifications.presentation.SubscriptionRequest
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import nl.martijndwars.webpush.Notification
import nl.martijndwars.webpush.PushAsyncService
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.springframework.stereotype.Service
import java.security.Security


// 4. Create a service to handle push notifications (PushNotificationService.java)
@Service
class PushNotificationService(
    private val subscriptionRepository: PushSubscriptionRepository,
    notificationProperties: NotificationProperties,
) {
    init {
        // Add Bouncy Castle provider if not present
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(BouncyCastleProvider())
        }
    }

    private val pushService = PushAsyncService(
        notificationProperties.publicKey,
        notificationProperties.privateKey,
    )


    fun subscribe(request: SubscriptionRequest) {
        val currentUser = AuthenticationService.getAccountFromContext()

        val subscription = PushSubscription(
            userEmail = currentUser.email,
            endpoint = request.endpoint,
            p256dh = request.keys.p256dh,
            auth = request.keys.auth
        )
        subscriptionRepository.save(subscription)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun sendNotification(message: String) {
        val subscriptions = subscriptionRepository.findAll()


        subscriptions.forEach { subscription ->
            kotlinx.coroutines.GlobalScope.launch {
                val notification = Notification(
                    subscription.endpoint,
                    subscription.p256dh,
                    subscription.auth,
                    message.toByteArray()
                )
                pushService.send(notification)
            }
        }
    }
}

