package com.iliesbel.yapbackend.infra.notifications

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/public/push")
class PushNotificationController(private val pushNotificationService: PushNotificationService) {
    @PostMapping("/subscribe")
    fun subscribe(@RequestBody request: SubscriptionRequest): ResponseEntity<Void> {
        pushNotificationService.subscribe(request)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/notify")
    fun sendNotification(@RequestBody request: NotificationRequest): ResponseEntity<Void> {
        pushNotificationService.sendNotification(request.message)
        return ResponseEntity.ok().build()
    }
}