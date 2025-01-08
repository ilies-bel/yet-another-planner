package com.iliesbel.yapbackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class YapBackendApplication

fun main(args: Array<String>) {
    runApplication<YapBackendApplication>(*args)
}
