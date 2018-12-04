package ch.guengel.funnel.domain

import assertk.assert
import assertk.assertions.isEqualTo
import ch.guengel.funnel.makeFeed
import ch.guengel.funnel.makeFeedEnvelope
import ch.guengel.funnel.makeSource
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

internal class FeedEnvelopeTest {

    @Test
    fun `feed envelope name property`() {
        val feedEnvelope = makeFeedEnvelope(makeSource(1), makeFeed("id", "title", 2))

        assert(feedEnvelope.name).isEqualTo("sourceName 1")
    }

    @Test
    fun `feed envelope lastUpdated property`() {
        val feedEnvelope = makeFeedEnvelope(makeSource(1), makeFeed("id", "title", 2))

        assert(feedEnvelope.lastUpdated).isEqualTo(ZonedDateTime.parse("2018-10-02T13:00:00+02:00"))
    }

    @Test
    fun `equal should work`() {
        val feedEnvelope1 = makeFeedEnvelope(makeSource(1), makeFeed("id", "title", 2))
        val feedEnvelope2 = makeFeedEnvelope(makeSource(1), makeFeed("id", "title", 3))

        assert(feedEnvelope1).isEqualTo(feedEnvelope2)
    }
}