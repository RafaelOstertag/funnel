package ch.guengel.funnel.kafka

import kotlinx.coroutines.*
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*


class Consumer(private val server: String, private val groupId: String, private val topics: List<String>) {
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


    fun start(handler: (topic: String, key: String, data: String) -> Unit) {
        job = CoroutineScope(Dispatchers.IO).launch {
            val consumer = createConsumer()

            logSubscription()

            try {
                while (isActive) {
                    consumer.poll(Duration.ofMillis(100)).forEach {
                        logger.info("Call handler for topic '${it.topic()}'")
                        handler(it.topic(), it.key() ?: "", it.value())
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

    private fun createConsumer(): KafkaConsumer<String, String> {
        val consumer = KafkaConsumer<String, String>(makeKafkaConfiguration())
        consumer.subscribe(topics)
        return consumer
    }

    fun oneOff(handler: (topic: String, key: String, data: String) -> Unit, pollDuration: Duration) {
        CoroutineScope(Dispatchers.IO).launch {
            val consumer = createConsumer()

            try {
                consumer.poll(pollDuration).forEach {
                    logger.info("One Off call handler for topic '${it.topic()}'")
                    handler(it.topic(), it.key() ?: "", it.value())
                }
            } catch (e: Throwable) {
                logger.error("Error polling one off consumer", e)
            } finally {
                consumer.close()
                logClose()
            }
        }
    }

    private fun logClose() {
        topics.forEach {
            logger.info("Close consumer for Kafka topic '$it'")
        }
    }

    private fun logSubscription() {
        topics.forEach {
            logger.info("Subscribed to Kafka topic '$it'")
        }
    }

    suspend fun stop() {
        job?.cancelAndJoin()
    }

    companion object {
        val logger = LoggerFactory.getLogger(Consumer::class.java)
    }
}