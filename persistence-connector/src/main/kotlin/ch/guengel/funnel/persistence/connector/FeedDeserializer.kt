package ch.guengel.funnel.persistence.connector

import ch.guengel.funnel.domain.FeedEnvelope
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

private object Jackson {
    val objectMapper = jacksonObjectMapper()
}

fun deserialize(data: String) =
    Jackson.objectMapper.readValue<FeedEnvelope>(data)
