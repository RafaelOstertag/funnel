package ch.guengel.funnel.kafka

import assertk.assertThat
import assertk.assertions.isEqualTo
import ch.guengel.funnel.testutils.makeFeed
import ch.guengel.funnel.testutils.makeFeedEnvelope
import ch.guengel.funnel.testutils.makeSource
import org.junit.jupiter.api.Test
import java.util.*

internal class ConverterKtTest {
    @Test
    fun shouldCorrectlyConvert() {
        val feedEnvelope = makeFeedEnvelope(
            UUID.randomUUID().toString(),
            makeSource(1),
            makeFeed(UUID.randomUUID().toString(), "test", 10)
        )
        val acutal = feedEnvelope.toKafkaFeedEnvelope().toFeedEnvelope()

        assertThat(acutal).isEqualTo(feedEnvelope)
    }
}

