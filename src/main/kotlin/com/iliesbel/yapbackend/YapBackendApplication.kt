package com.iliesbel.yapbackend

import com.iliesbel.yapbackend.infra.authentication.configuration.SecurityConfigProperties
import com.iliesbel.yapbackend.infra.notifications.NotificationProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.data.web.config.EnableSpringDataWebSupport
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode

@SpringBootApplication
@EnableConfigurationProperties(value = [NotificationProperties::class, SecurityConfigProperties::class])
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
class YapBackendApplication

fun main(args: Array<String>) {
    runApplication<YapBackendApplication>(*args)
}
