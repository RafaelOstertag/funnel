package ch.guengel.funnel.kafka

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.util.*

class Producer(private val server: String) : Closeable {
    val producer: KafkaProducer<String, String>? = KafkaProducer(makeKafkaConfiguration())

    private fun makeKafkaConfiguration(): Properties {
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

    fun send(topic: String, key: String, data: String) {
        logger.debug("kafka send to topic '$topic': '$data' with key '$key")
        producer?.send(ProducerRecord<String, String>(topic, key, data))
    }

    override fun close() {
        producer?.close()
        logger.info("Closed kafka consumer")
    }

    companion object {
        val logger = LoggerFactory.getLogger(Producer::class.java)
    }
}