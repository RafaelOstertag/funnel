package ch.guengel.funnel.common

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

object Jackson {
    val objectMapper = jacksonObjectMapper()
        .registerModule(JavaTimeModule())
        .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
}

inline fun <reified T> deserialize(data: String) =
    Jackson.objectMapper.readValue<T>(data)

fun serialize(obj: Any) = Jackson.objectMapper.writeValueAsString(obj)