package com.iliesbel.yapbackend.infra.notifications.presentation

data class SubscriptionRequest (
    val endpoint: String,
    val keys: SubscriptionRequestKeys,
)

data class SubscriptionRequestKeys (
    val p256dh: String,
    val auth: String,
)