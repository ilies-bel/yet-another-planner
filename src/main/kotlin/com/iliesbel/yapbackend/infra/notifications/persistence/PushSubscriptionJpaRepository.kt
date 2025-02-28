package com.iliesbel.yapbackend.infra.notifications.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

interface PushSubscriptionJpaRepository : JpaRepository<PushSubscription, Long>

@Repository
class PushSubscriptionRepository(private val pushSubscriptionJpaRepository: PushSubscriptionJpaRepository){

    fun findAll(): List<PushSubscription> {
        return pushSubscriptionJpaRepository.findAll()
    }

    fun save(subscription: PushSubscription): PushSubscription {
        return pushSubscriptionJpaRepository.save(subscription)
    }
}