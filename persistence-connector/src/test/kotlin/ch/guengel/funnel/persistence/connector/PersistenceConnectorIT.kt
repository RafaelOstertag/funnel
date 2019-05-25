package ch.guengel.funnel.persistence.connector

import assertk.all
import assertk.assert
import assertk.assertions.contains
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import ch.guengel.funnel.common.deserialize
import ch.guengel.funnel.common.serialize
import ch.guengel.funnel.domain.FeedEnvelope
import ch.guengel.funnel.kafka.Constants
import ch.guengel.funnel.kafka.Consumer
import ch.guengel.funnel.kafka.Producer
import ch.guengel.funnel.kafka.Topics
import ch.guengel.funnel.kafka.messages.RetrieveByNameMessage
import ch.guengel.funnel.readConfiguration
import ch.guengel.funnel.testutils.EmbeddedMongo
import com.uchuhimo.konf.Config
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import makeFeed
import makeFeedEnvelope
import makeSource
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable

private const val allFeedRetrievalReply = "test.all.feed"
private const val nameListReply = "test.name.list"
private const val singleFeedRetrievalReply = "test.single.feed"
/**
 * This test requires a running Kafka locally
 */
@EnabledIfEnvironmentVariable(named = "HAS_LOCAL_ENVIRONMENT", matches = "yes|true")
class PersistenceConnectorIT {
    companion object {
        private var embeddedMongo: EmbeddedMongo? = null
        // Will be overriden by setUp(). But instead of making it nullable, we go with this and guarantee that it is not null
        private var configuration: Config = readConfiguration(Configuration)

        @BeforeAll
        @JvmStatic
        internal fun setUp() {
            embeddedMongo = EmbeddedMongo()
            embeddedMongo?.start()
            System.getProperties()
                    .setProperty("persistence.connector.mongodburl", "mongodb://localhost:${embeddedMongo?.mongoPort}")
            configuration = readConfiguration(Configuration)
        }

        @AfterAll
        @JvmStatic
        internal fun tearDown() {
            embeddedMongo?.stop()
        }
    }

    @Test
    fun `retrieve all`() {
        val feedEnvelope = makeFeedEnvelope(makeSource(1), makeFeed("testId", "test title 1", 9))

        // The listener to all retrieved feeds
        val consumer = setupTestFeedConsumer()
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
                it.send(Topics.retrieveAll, Constants.noKey, allFeedRetrievalReply)
                runBlocking {
                    delay(2000)
                }
            }
        }

        assert(testSuccess).isTrue()
        runBlocking {
            testConsumer.stop()
            consumer.stop()
        }
    }

    @Test
    fun `retrieve by name`() {
        val feedEnvelope = makeFeedEnvelope(makeSource(1), makeFeed("testid", "test title 1", 2))

        var feedRetrievalSuccess = false

        val consumer = setupTestFeedConsumer()
        consumer.start { topic, key, data ->
            if (topic == singleFeedRetrievalReply) {
                val deserializedResponse = deserialize<FeedEnvelope>(data)
                assert(deserializedResponse).isEqualTo(feedEnvelope)
                feedRetrievalSuccess = true
            }

        }

        val topicHandler = TopicHandler(configuration)
        val testConsumer = setUpConsumer(configuration)
        topicHandler.use {
            testConsumer.start(it::handle)

            sendSaveEnvelopeMessage(feedEnvelope)

            Producer(configuration[Configuration.kafka]).use {
                val retrieveByNameMessage = RetrieveByNameMessage("sourceName 1", singleFeedRetrievalReply)
                it.send(Topics.retrieveFeedByName, Constants.noKey, serialize(retrieveByNameMessage))
                runBlocking { delay(2000) }
            }
        }

        assert(feedRetrievalSuccess).isTrue()
        runBlocking {
            testConsumer.stop()
            consumer.stop()
        }
    }

    @Test
    fun `retrieve name list`() {
        val feedEnvelope = makeFeedEnvelope(makeSource(1), makeFeed("testid", "test title 1", 2))

        var nameRetrievalSuccess = false


        val consumer = setupTestFeedConsumer()
        consumer.start { topic, key, data ->
            if (topic == nameListReply) {
                val list = deserialize<List<String>>(data)
                assert(list).all {
                    contains("sourceName 1")
                    hasSize(1)
                }
                nameRetrievalSuccess = true
            }
        }

        val topicHandler = TopicHandler(configuration)
        val testConsumer = setUpConsumer(configuration)
        topicHandler.use {
            testConsumer.start(it::handle)

            sendSaveEnvelopeMessage(feedEnvelope)

            Producer(configuration[Configuration.kafka]).use {
                it.send(Topics.retrieveAllNames, Constants.noKey, nameListReply)
                runBlocking { delay(2000) }
            }


        }

        assert(nameRetrievalSuccess).isTrue()
        runBlocking {
            testConsumer.stop()
            consumer.stop()
        }
    }

    private fun setupTestFeedConsumer(): Consumer {
        val consumer =
                Consumer(configuration[Configuration.kafka], "funnel.persistence.testGroup",
                        listOf(allFeedRetrievalReply, nameListReply, singleFeedRetrievalReply))

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
