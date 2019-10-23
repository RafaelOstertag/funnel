package kafka

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import java.time.OffsetDateTime
import java.time.ZoneOffset

@EnabledIfEnvironmentVariable(named = "HAS_LOCAL_ENVIRONMENT", matches = "yes|true")
class KafkaIT {
    @Test
    fun `test kafka`() {
        val expectedFeedEnvelope = createFeedEnvelope()
        var success = false

        val consumer = Consumer(kafkaServer, "test group", testTopic)
        consumer.start { topic, feedEnvelope ->
            assertThat(feedEnvelope).isEqualTo(feedEnvelope)
            success = true
        }

        Producer(kafkaServer).use {
            it.send(testTopic, expectedFeedEnvelope)
        }

        runBlocking {
            delay(500)
        }

        consumer.stop()
        assertThat(success).isTrue()
    }

    private fun createFeedEnvelope(): FeedEnvelope {
        val time = OffsetDateTime.of(2019, 1, 2, 3, 4, 5, 0, ZoneOffset.UTC)
        val feedItem1 = FeedItem("id1", "title1", time)
        val feedItem2 = FeedItem("id2", "title2", time.plusDays(1))
        val feedItems = FeedItems(listOf<FeedItem>(feedItem1, feedItem2))
        val feed = Feed("id", "title", feedItems)
        val source = Source("name", "address")
        return FeedEnvelope(source, feed)
    }

    private companion object {
        const val kafkaServer = "localhost:9092"
        const val testTopic = "funnel.test.topic"
    }
}