package com.iliesbel.yapbackend.infra.chromeBookmark.jacksonTimestampDeserializer

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
@JacksonAnnotationsInside
@JsonDeserialize(using = EpochToLocalDateTimeDeserializer::class)
annotation class EpochToLocalDateTime

