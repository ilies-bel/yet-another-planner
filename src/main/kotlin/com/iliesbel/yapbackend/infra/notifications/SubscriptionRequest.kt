package com.iliesbel.yapbackend.infra.notifications

data class SubscriptionRequest (
    val endpoint: String,
    val keys: SubscriptionRequestKeys,
)

data class SubscriptionRequestKeys (
    val p256dh: String,
    val auth: String,
)