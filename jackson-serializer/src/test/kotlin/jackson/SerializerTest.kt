package ch.guengel.funnel.jackson

import assertk.assertThat
import assertk.assertions.isEqualTo
import ch.guengel.funnel.feed.data.*
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset

internal class SerializerTest {
    @Test
    fun `serialize and deserialize`() {
        val time = OffsetDateTime.of(2019, 1, 2, 3, 4, 5, 0, ZoneOffset.UTC)
        val feedItem1 = FeedItem("id1", "title1", time)
        val feedItem2 = FeedItem("id2", "title2", time.plusDays(1))
        val feedItems = FeedItems(listOf<FeedItem>(feedItem1, feedItem2))
        val feed = Feed("id", "title", feedItems)
        val source = Source("name", "address")
        val feedEnvelope = FeedEnvelope(source, feed)

        val serialized = serialize(feedEnvelope)
        val deserializeFeedEnvelope: FeedEnvelope = deserialize(serialized)

        assertThat(deserializeFeedEnvelope).isEqualTo(feedEnvelope)
    }
}