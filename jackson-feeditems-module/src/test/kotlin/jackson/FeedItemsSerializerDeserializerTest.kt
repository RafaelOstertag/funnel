package ch.guengel.funnel.jackson

import assertk.assertThat
import assertk.assertions.isEqualTo
import ch.guengel.funnel.feed.data.FeedItem
import ch.guengel.funnel.feed.data.FeedItems
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset

internal class FeedItemsSerializerDeserializerTest {
    val objectMapper: ObjectMapper = jacksonObjectMapper()
        .registerModule(jacksonFeedItemsModule())
        .registerModule(JavaTimeModule())
        .enable(SerializationFeature.INDENT_OUTPUT)

    @Test
    fun `serialize and deserialize`() {
        val time = OffsetDateTime.of(2019, 1, 2, 3, 4, 5, 0, ZoneOffset.UTC)
        val feedItem1 = FeedItem("id1", "title1", "link1", time)
        val feedItem2 = FeedItem("id2", "title2", "link2", time.plusDays(1))
        val feedItems = FeedItems(listOf<FeedItem>(feedItem1, feedItem2))

        val serialized = objectMapper.writeValueAsString(feedItems)
        val deserializedFeedItems = objectMapper.readValue(serialized, FeedItems::class.java)

        assertThat(deserializedFeedItems).isEqualTo(feedItems)
    }
}