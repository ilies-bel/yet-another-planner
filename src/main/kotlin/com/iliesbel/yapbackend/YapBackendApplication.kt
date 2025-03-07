package com.iliesbel.yapbackend

import com.iliesbel.yapbackend.infra.authentication.configuration.SecurityConfigProperties
import com.iliesbel.yapbackend.infra.notifications.NotificationProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(value = [NotificationProperties::class, SecurityConfigProperties::class])
class YapBackendApplication

fun main(args: Array<String>) {
    runApplication<YapBackendApplication>(*args)
}
