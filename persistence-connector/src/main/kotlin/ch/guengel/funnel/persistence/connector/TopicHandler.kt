package ch.guengel.funnel.persistence.connector

import ch.guengel.funnel.common.deserialize
import ch.guengel.funnel.common.serialize
import ch.guengel.funnel.domain.FeedEnvelope
import ch.guengel.funnel.kafka.Constants
import ch.guengel.funnel.kafka.Constants.noData
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

    private fun sendAll(toTopic: String) {
        try {
            val feedEnvelopes = mongo.retrieveAll()
            kafkaProducer.send(toTopic, Constants.noKey, serialize(feedEnvelopes))
            logger.info("Sent all feeds to '${toTopic}'")
        } catch (e: Throwable) {
            logger.error("Error responding to feed retrieval request", e)
        }
    }

    fun handle(topic: String, key: String, data: String) {
        when (topic) {
            Topics.persistFeed -> saveFeed(data)
            Topics.retrieveAll -> sendAll(data)
            Topics.feedDelete -> deleteFeed(key)
            else -> logger.error("Don't know how to handle message in topic '$topic'")
        }
    }

    private fun deleteFeed(name: String) {
        try {
            mongo.deleteById(name)
            logger.info("Deleted feed ${name}")
        } catch (e: Throwable) {
            logger.error("Error deleting feed ${name}")
        }
    }

    override fun close() {
        kafkaProducer.close()
    }

    companion object {
        val logger = LoggerFactory.getLogger(TopicHandler::class.java)
    }

}