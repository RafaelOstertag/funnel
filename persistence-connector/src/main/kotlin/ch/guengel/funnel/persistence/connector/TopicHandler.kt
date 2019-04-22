package ch.guengel.funnel.persistence.connector

import ch.guengel.funnel.common.deserialize
import ch.guengel.funnel.common.serialize
import ch.guengel.funnel.domain.FeedEnvelope
import ch.guengel.funnel.kafka.Producer
import ch.guengel.funnel.kafka.Topics
import ch.guengel.funnel.persistence.MongoFeedEnvelopeRepository
import com.uchuhimo.konf.Config
import org.slf4j.LoggerFactory
import java.io.Closeable

class TopicHandler(configuration: Config) : Closeable {
    private val kafkaProducer = Producer(configuration[Configuration.kafka])

    private val mongo =
        MongoFeedEnvelopeRepository(configuration[Configuration.mongoDbURL], configuration[Configuration.mongoDb])

    private fun saveFeed(serializedData: String) {
        try {
            val feedEnvelope = deserialize<FeedEnvelope>(serializedData)
            mongo.save(feedEnvelope)
            logger.info("Saved feed envelope '${feedEnvelope.name}'")
        } catch (e: Throwable) {
            logger.error("Error saving feed envelope", e)
        }
    }

    private fun sendAll(forKey: String, data: String) {
        if (!data.isBlank()) {
            return
        }

        try {
            val feedEnvelopes = mongo.retrieveAll()
            kafkaProducer.send(Topics.retrieveAll, forKey, serialize(feedEnvelopes))
            logger.info("Sent all feeds to '${Topics.retrieveAll}' for key '$forKey'")
        } catch (e: Throwable) {
            logger.error("Error responding to feed retrieval request", e)
        }
    }

    fun handle(topic: String, key: String, data: String) {
        when (topic) {
            Topics.feedUpdate -> saveFeed(data)
            Topics.retrieveAll -> sendAll(key, data)
            else -> logger.error("Don't know how to handle message in topic '$topic'")
        }
    }

    override fun close() {
        kafkaProducer.close()
    }

    companion object {
        val logger = LoggerFactory.getLogger(TopicHandler::class.java)
    }

}