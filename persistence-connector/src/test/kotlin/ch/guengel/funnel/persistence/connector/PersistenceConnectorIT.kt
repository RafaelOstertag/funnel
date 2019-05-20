package ch.guengel.funnel.persistence.connector

import assertk.assert
import assertk.assertions.isTrue
import ch.guengel.funnel.common.deserialize
import ch.guengel.funnel.common.serialize
import ch.guengel.funnel.domain.FeedEnvelope
import ch.guengel.funnel.kafka.Constants
import ch.guengel.funnel.kafka.Constants.noData
import ch.guengel.funnel.kafka.Consumer
import ch.guengel.funnel.kafka.Producer
import ch.guengel.funnel.kafka.Topics
import ch.guengel.funnel.readConfiguration
import ch.guengel.funnel.testutils.EmbeddedMongo
import com.uchuhimo.konf.Config
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import makeFeed
import makeFeedEnvelope
import makeSource
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable

private const val feedReply = "test.all.feed.retriever"
/**
 * This test requires a running Kafka locally
 */
@EnabledIfEnvironmentVariable(named = "HAS_LOCAL_ENVIRONMENT", matches = "yes|true")
class PersistenceConnectorIT {
    private var embeddedMongo: EmbeddedMongo? = null

    // Will be overriden by setUp(). But instead of making it nullable, we go with this and guarantee that it is not null
    private var configuration: Config = readConfiguration(Configuration)

    @BeforeEach
    fun setUp() {
        embeddedMongo = EmbeddedMongo()
        embeddedMongo?.start()
        System.getProperties()
            .setProperty("persistence.connector.mongodburl", "mongodb://localhost:${embeddedMongo?.mongoPort}")
        configuration = readConfiguration(Configuration)
    }

    @AfterEach
    fun tearDown() {
        embeddedMongo?.stop()
    }

    @Test
    fun `end to end`() {
        val feedEnvelope = makeFeedEnvelope(makeSource(1), makeFeed("testId", "test title 1", 9))

        // The listener to all retrieved feeds
        val consumer = setupListOfFeedEnvelopesConsumer()
        var testSuccess = false
        consumer.start { topic, key, data ->
                val actual = deserialize<List<FeedEnvelope>>(data)
                if (actual.size != 1) {
                    return@start
                }
                if (actual.get(0) != feedEnvelope) {
                    return@start
                }
                testSuccess = true
        }

        val topicHandler = TopicHandler(configuration)
        val testConsumer = setUpConsumer(configuration)
        topicHandler.use {
            testConsumer.start(it::handle)


            sendSaveEnvelopeMessage(feedEnvelope)

            // Initiate retrieval of all feeds
            Producer(configuration[Configuration.kafka]).use {
                it.send(Topics.retrieveAll, Constants.noKey, feedReply)
            }

            runBlocking {
                delay(2000)
            }
        }

        assert(testSuccess).isTrue()
    }

    private fun setupListOfFeedEnvelopesConsumer(): Consumer {
        val consumer =
            Consumer(configuration[Configuration.kafka], "funnel.persistence.testGroup", listOf(feedReply))

        Runtime.getRuntime().addShutdownHook(Thread {
            runBlocking {
                consumer.stop()
            }
        })
        return consumer
    }

    private fun sendSaveEnvelopeMessage(feedEnvelope: FeedEnvelope) {
        Producer(configuration[Configuration.kafka]).use {
            it.send(Topics.persistFeed, Constants.noKey, serialize(feedEnvelope))
        }
    }
}
