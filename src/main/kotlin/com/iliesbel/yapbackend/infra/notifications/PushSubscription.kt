package com.iliesbel.yapbackend.infra.notifications

import jakarta.persistence.*

@Entity
@Table(name = "push_subscriptions")
class PushSubscription (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     val id: Long? = null,
     val endpoint: String,
     val p256dh: String,
     val auth: String,
    val userEmail: String,
)