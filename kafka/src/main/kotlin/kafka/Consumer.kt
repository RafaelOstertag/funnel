package kafka

import data.FeedEnvelope
import jackson.deserialize
import kotlinx.coroutines.*
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*


class Consumer(private val server: String, private val groupId: String, private val topic: String) {
    var job: Job? = null

    private fun makeKafkaConfiguration(): Properties {
        val props = Properties()
        props.put("bootstrap.servers", server)
        props.put("group.id", groupId)
        props.put("enable.auto.commit", "true")
        props.put("auto.commit.interval.ms", "1000")
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")

        return props
    }


    fun start(handler: (topic: String, feedEnvelope: FeedEnvelope) -> Unit) {
        job = CoroutineScope(Dispatchers.IO).launch {
            val consumer = createConsumer()

            logSubscription()

            try {
                while (isActive) {
                    consumer.poll(Duration.ofMillis(100)).forEach {
                        logger.info("Call handler for topic '${it.topic()}'")

                        toFeedEnvelope(it.value())?.apply {
                            handler(it.topic(), this)
                        }
                    }
                }
            } catch (e: Throwable) {
                logger.error("Error polling consumer", e)
            } finally {
                consumer.close()
                logClose()
            }
        }
    }

    private fun toFeedEnvelope(value: String?): FeedEnvelope? {
        if (value == null) {
            logger.warn("No value received from kafka")
            return null
        }

        try {
            val feedEnvelope: FeedEnvelope = deserialize(value)
            return feedEnvelope
        } catch (e: Exception) {
            logger.warn("Unable to deserialize FeedEnvelope", e)
            return null
        }
    }

    private fun createConsumer(): KafkaConsumer<String, String> =
        KafkaConsumer<String, String>(makeKafkaConfiguration())
            .apply {
                subscribe(listOf(topic))
            }

    private fun logClose() = logger.info("Close consumer for Kafka topic '$topic'")

    private fun logSubscription() = logger.info("Subscribed to Kafka topic '$topic'")

    suspend fun stop() {
        job?.cancelAndJoin()
        job = null
    }

    private companion object {
        val logger = LoggerFactory.getLogger(Consumer::class.java)
    }
}