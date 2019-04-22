package ch.guengel.funnel.persistence.connector

import assertk.assert
import assertk.assertions.isTrue
import ch.guengel.funnel.common.deserialize
import ch.guengel.funnel.common.serialize
import ch.guengel.funnel.domain.FeedEnvelope
import ch.guengel.funnel.kafka.Consumer
import ch.guengel.funnel.kafka.Producer
import ch.guengel.funnel.kafka.Topics
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import makeFeed
import makeFeedEnvelope
import makeSource
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable


@EnabledIfEnvironmentVariable(named = "HAS_LOCAL_ENVIRONMENT", matches = "yes|true")
class SendReceiveIT {
    companion object {
        val feedRetrievalConsumerTopic = "funnel.test.feed.consumer"
        val localKafkaServer = "localhost:9092"
    }


    @Test
    fun `end to end`() {
        var testSuccess = false
        val feedEnvelope = makeFeedEnvelope(makeSource(1), makeFeed("testId", "test title 1", 9))

        sendSaveEnvelopeMessage(feedEnvelope)

        val consumer = setupListOfFeedEnvelopesConsumer()

        consumer.start { topic, key, data ->
            if (topic != feedRetrievalConsumerTopic) {
                return@start
            }

            val actual = deserialize<List<FeedEnvelope>>(data)
            if (actual.size != 1) {
                return@start
            }
            if (actual.get(0) != feedEnvelope) {
                return@start
            }
            testSuccess = true
        }

        Producer(localKafkaServer).use {
            it.send(Topics.retrieveAll, "", feedRetrievalConsumerTopic)
        }

        runBlocking {
            delay(2000)
        }

        assert(testSuccess).isTrue()
    }

    private fun setupListOfFeedEnvelopesConsumer(): Consumer {
        val consumer = Consumer(localKafkaServer, "funnel.persistence.testGroup", listOf(feedRetrievalConsumerTopic))

        Runtime.getRuntime().addShutdownHook(Thread {
            runBlocking {
                consumer.stop()
            }
        })
        return consumer
    }

    private fun sendSaveEnvelopeMessage(feedEnvelope: FeedEnvelope) {
        Producer(localKafkaServer).use {
            it.send(Topics.saveSingle, "", serialize(feedEnvelope))
        }
    }
}
