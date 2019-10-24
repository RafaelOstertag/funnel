package kafka

import data.FeedEnvelope
import jackson.serialize
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.util.*

class Producer(private val server: String) : Closeable {
    private val kafkaProducer: KafkaProducer<String, String> = KafkaProducer(createKafkaConfiguration())
    private var closed = false

    private fun createKafkaConfiguration(): Properties {
        val props = Properties()
        props.put("bootstrap.servers", server)
        props.put("acks", "all")
        props.put("batch.size", 16384)
        props.put("linger.ms", 1)
        props.put("buffer.memory", 33554432)
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

        return props
    }

    fun send(topic: String, feedEnvelope: FeedEnvelope) {
        check(!closed) {
            logger.error(closedKafkaClientError)
            closedKafkaClientError
        }
        val key = feedEnvelope.name
        val data = serialize(feedEnvelope)

        logger.debug("kafka send to topic '$topic': '$data' with key '$key'")
        kafkaProducer.send(ProducerRecord<String, String>(topic, key, data))
    }

    override fun close() {
        kafkaProducer.close()
        closed = true
        logger.info("Closed kafka producer")
    }

    private companion object {
        val logger = LoggerFactory.getLogger(Producer::class.java)
        const val closedKafkaClientError = "Trying to use closed kafka client"
    }
}