package com.iliesbel.yapbackend.infra.exception

import org.springframework.http.HttpStatus

class ApiException(message: String, val status: HttpStatus) : RuntimeException(message)