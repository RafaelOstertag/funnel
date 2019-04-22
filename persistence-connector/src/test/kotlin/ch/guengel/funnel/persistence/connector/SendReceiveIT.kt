package ch.guengel.funnel.persistence.connector

import assertk.assert
import assertk.assertions.isTrue
import ch.guengel.funnel.common.deserialize
import ch.guengel.funnel.common.serialize
import ch.guengel.funnel.domain.FeedEnvelope
import ch.guengel.funnel.kafka.Consumer
import ch.guengel.funnel.kafka.Producer
import ch.guengel.funnel.kafka.Topics
import ch.guengel.funnel.readConfiguration
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import makeFeed
import makeFeedEnvelope
import makeSource
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable


@EnabledIfEnvironmentVariable(named = "HAS_LOCAL_ENVIRONMENT", matches = "yes|true")
class SendReceiveIT {
    private var configuration = readConfiguration(Configuration)

    @Test
    fun `end to end`() {
        var testSuccess = false
        val feedEnvelope = makeFeedEnvelope(makeSource(1), makeFeed("testId", "test title 1", 9))

        // The listener to all retrieved feeds
        val consumer = setupListOfFeedEnvelopesConsumer()
        consumer.start { topic, key, data ->
            if (topic == Topics.retrieveAll &&
                key == "the-test-key" &&
                !data.isBlank()
            ) {
                val actual = deserialize<List<FeedEnvelope>>(data)
                if (actual.size != 1) {
                    return@start
                }
                if (actual.get(0) != feedEnvelope) {
                    return@start
                }
                testSuccess = true
            }
        }

        val topicHandler = TopicHandler(configuration)
        val testConsumer = setUpConsumer(configuration)
        topicHandler.use {
            testConsumer.start(it::handle)

            sendSaveEnvelopeMessage(feedEnvelope)

            // Initiate retrieval of all feeds
            Producer(configuration[Configuration.kafka]).use {
                it.send(Topics.retrieveAll, "the-test-key", "")
            }

            runBlocking {
                delay(2000)
            }
        }

        assert(testSuccess).isTrue()
    }

    private fun setupListOfFeedEnvelopesConsumer(): Consumer {
        val consumer =
            Consumer(configuration[Configuration.kafka], "funnel.persistence.testGroup", listOf(Topics.retrieveAll))

        Runtime.getRuntime().addShutdownHook(Thread {
            runBlocking {
                consumer.stop()
            }
        })
        return consumer
    }

    private fun sendSaveEnvelopeMessage(feedEnvelope: FeedEnvelope) {
        Producer(configuration[Configuration.kafka]).use {
            it.send(Topics.feedUpdate, "", serialize(feedEnvelope))
        }
    }
}
