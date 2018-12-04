package ch.guengel.funnel.domain

import assertk.assert
import assertk.assertions.isEqualTo
import ch.guengel.funnel.makeFeed
import ch.guengel.funnel.makeFeedEnvelope
import ch.guengel.funnel.makeSource
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Test

class SerializeDeserializeTest {
    private val objectMapper = jacksonObjectMapper()
            .registerModule(JavaTimeModule())
            .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)

    @Test
    fun `serialize deserialize`() {
        val feedEnvelope = makeFeedEnvelope(makeSource(1), makeFeed("id", "title", 2))

        val serialized = objectMapper.writeValueAsString(feedEnvelope)
        val deserialized = objectMapper.readValue<FeedEnvelope>(serialized)

        assert(feedEnvelope).isEqualTo(deserialized)
    }
}