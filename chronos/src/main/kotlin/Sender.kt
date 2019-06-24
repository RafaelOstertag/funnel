package ch.guengel.funnel.chronos

import ch.guengel.funnel.common.serialize
import ch.guengel.funnel.domain.FeedEnvelope
import ch.guengel.funnel.kafka.Producer
import org.slf4j.LoggerFactory

class Sender(private val topic: String, private val producer: Producer) {
    fun send(envelopes: List<FeedEnvelope>) {
        logger.info("Send all envelopes to '${topic}'")
        serialize(envelopes)
                .apply { producer.send(topic, KEY, this) }
        logger.debug("Sent {} envelope(s) to topic '{}' with key '{}'",
                envelopes.size, topic, KEY)
    }

    companion object {
        val logger = LoggerFactory.getLogger(Sender::class.java)
        val KEY = "chronos"
    }
}