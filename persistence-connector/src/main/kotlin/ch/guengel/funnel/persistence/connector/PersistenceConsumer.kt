package ch.guengel.funnel.persistence.connector


import kotlinx.coroutines.*
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*


class PersistenceConsumer(private val server: String, private val topic: String) {
    var job: Job? = null

    private fun makeKafkaConfiguration(): Properties {
        val props = Properties()
        props.put("bootstrap.servers", server)
        props.put("group.id", "funnel.persistence")
        props.put("enable.auto.commit", "true")
        props.put("auto.commit.interval.ms", "1000")
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")

        return props
    }


    fun start(handler: (topic: String, key: String, data: String) -> Unit) {
        job = CoroutineScope(Dispatchers.IO).launch {
            val consumer = KafkaConsumer<String, String>(makeKafkaConfiguration())
            consumer.subscribe(listOf(topic))

            logger.info("Subscribed to Kafka topic '${topic}'")

            try {
                while (isActive) {
                    consumer.poll(Duration.ofMillis(100)).forEach {
                        handler(it.topic(), it.key() ?: "", it.value())
                    }
                }
            } finally {
                consumer.close()
                logger.info("Close consumer for Kafka topic '${topic}'")
            }
        }
    }

    suspend fun stop() {
        job?.cancelAndJoin()
    }

    companion object {
        val logger = LoggerFactory.getLogger(PersistenceConsumer::class.java)
    }
}