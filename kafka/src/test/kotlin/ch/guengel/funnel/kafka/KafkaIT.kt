package ch.guengel.funnel.kafka

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import ch.guengel.funnel.feed.data.*
import ch.guengel.funnel.testutils.LocalKafka
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledOnOs
import org.junit.jupiter.api.condition.OS
import java.time.OffsetDateTime
import java.time.ZoneOffset

@DisabledOnOs(OS.OTHER)
class KafkaIT {
    private var localKafka: LocalKafka? = null

    @BeforeEach
    fun setUp() {
        localKafka = LocalKafka()
        localKafka?.start()
    }

    @AfterEach
    fun tearDown() {
        localKafka?.stop()
    }

    @Test
    fun `test kafka`() {
        val expectedFeedEnvelope = createFeedEnvelope()
        var success = false

        val consumer = Consumer(localKafka?.bootstrapServer!!, "test group", testTopic)
        consumer.start { topic, feedEnvelope ->
            assertThat(feedEnvelope).isEqualTo(feedEnvelope)
            success = true
        }

        Producer(localKafka?.bootstrapServer!!).use {
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
        val feedItem1 = FeedItem("id1", "title1", "link1", time)
        val feedItem2 = FeedItem("id2", "title2", "link2", time.plusDays(1))
        val feedItems = FeedItems(listOf<FeedItem>(feedItem1, feedItem2))
        val feed = Feed("id", "title", feedItems)
        val source = Source("name", "address")
        val user = User("userId", "email")
        return FeedEnvelope(user, source, feed)
    }

    private companion object {
        const val testTopic = "funnel.test.topic"
    }
}