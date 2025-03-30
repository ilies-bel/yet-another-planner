package com.iliesbel.yapbackend.infra.chromeBookmark.jacksonTimestampDeserializer

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class EpochToLocalDateTimeDeserializer : JsonDeserializer<LocalDateTime>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): LocalDateTime {
        val epochValue = p.longValue
        
        return LocalDateTime.ofInstant(
            Instant.ofEpochMilli(epochValue),
            ZoneId.systemDefault()
        )
    }
}