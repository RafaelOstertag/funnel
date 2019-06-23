package ch.guengel.funnel.persistence.connector

import assertk.assert
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import ch.guengel.funnel.common.serialize
import ch.guengel.funnel.domain.FeedEnvelope
import ch.guengel.funnel.kafka.Producer
import ch.guengel.funnel.persistence.MongoFeedEnvelopeRepository
import ch.guengel.funnel.testutils.EmbeddedMongo
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable

/**
 * This test requires a running Kafka locally
 */
@EnabledIfEnvironmentVariable(named = "HAS_LOCAL_ENVIRONMENT", matches = "yes|true")
class PersistenceConnectorIT {
    companion object {
        private var embeddedMongo: EmbeddedMongo? = null
        var feedEnvelopeRepository: MongoFeedEnvelopeRepository? = null
        var topicHandler: TopicHandler? = null


        @BeforeAll
        @JvmStatic
        internal fun setUp() {
            embeddedMongo = EmbeddedMongo()
            embeddedMongo?.start()

            feedEnvelopeRepository = MongoFeedEnvelopeRepository("mongodb://localhost:${embeddedMongo?.mongoPort}",
                    "test")

            topicHandler = TopicHandler(feedEnvelopeRepository ?: throw IllegalStateException())
        }

        @AfterAll
        @JvmStatic
        internal fun tearDown() {
            feedEnvelopeRepository?.close()
            embeddedMongo?.stop()
        }
    }

    val KAFKA_HOST = "localhost:9092"


    @Test
    fun `should persist feed envelope`() {
        val consumer = setUpConsumer(KAFKA_HOST)
        consumer.start(topicHandler!!::handle)

        val feedEnvelope = createTestFeed()

        Producer(KAFKA_HOST).use {
            it.send(PERSIST_TOPIC, feedEnvelope.name, serialize(feedEnvelope))
        }

        runBlocking {
            delay(500)
        }

        val actual = feedEnvelopeRepository?.retrieveByName("sourceName 0")
        assert(actual).isNotNull()
    }

    @Test
    fun `should delete feed envelope`() {
        var testFeedEnvelope: FeedEnvelope? = createTestFeed()
        feedEnvelopeRepository?.save(testFeedEnvelope!!)

        testFeedEnvelope = feedEnvelopeRepository?.retrieveByName("sourceName 0")
        assert(testFeedEnvelope).isNotNull()

        val consumer = setUpConsumer(KAFKA_HOST)
        consumer.start(topicHandler!!::handle)

        Producer(KAFKA_HOST).use {
            it.send(DELETE_TOPIC, "", "sourceName 0")
        }

        runBlocking {
            delay(500)
        }

        val actual = feedEnvelopeRepository?.retrieveByName("sourceName 0")
        assert(actual).isNull()

    }

    private fun createTestFeed(): FeedEnvelope {
        val feed = makeFeed("test", "title", 3)
        val source = makeSource(0)
        return makeFeedEnvelope(source, feed)
    }
}
