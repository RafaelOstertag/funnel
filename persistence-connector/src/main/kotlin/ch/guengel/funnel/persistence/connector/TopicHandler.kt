package ch.guengel.funnel.persistence.connector

import ch.guengel.funnel.common.deserialize
import ch.guengel.funnel.common.serialize
import ch.guengel.funnel.domain.FeedEnvelope
import ch.guengel.funnel.kafka.Producer
import ch.guengel.funnel.kafka.Topics
import ch.guengel.funnel.persistence.MongoFeedEnvelopeRepository
import org.slf4j.LoggerFactory
import java.io.Closeable

class TopicHandler(mongoDbUrl: String, mongoDb: String, kafkaServer: String) : Closeable {
    private val kafkaProducer = Producer(kafkaServer)

    private val mongo =
            MongoFeedEnvelopeRepository(mongoDbUrl, mongoDb)

    private fun saveFeed(serializedData: String) {
        try {
            val feedEnvelope = deserialize<FeedEnvelope>(serializedData)
            mongo.save(feedEnvelope)
            logger.info("Saved feed envelope '${feedEnvelope.name}'")
        } catch (e: Throwable) {
            logger.error("Error saving feed envelope", e)
        }
    }

    private fun sendAll(toTopic: String) {
        try {
            val feedEnvelopes = mongo.retrieveAll()
            kafkaProducer.send(toTopic, "", serialize(feedEnvelopes))
            logger.info("Sent all feeds to '$toTopic'")
        } catch (e: Throwable) {
            logger.error("Error responding to feed retrieval request", e)
        }
    }

    fun handle(topic: String, key: String, data: String) {
        when (topic) {
            Topics.saveSingle -> saveFeed(data)
            Topics.retrieveAll -> sendAll(data)
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