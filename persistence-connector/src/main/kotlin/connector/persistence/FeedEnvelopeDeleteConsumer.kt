package ch.guengel.funnel.connector.persistence

import ch.guengel.funnel.feed.bridges.FeedEnvelopePersistence
import ch.guengel.funnel.feed.data.FeedEnvelope
import ch.guengel.funnel.kafka.Consumer
import ch.guengel.funnel.kafka.feedEnvelopeDeleteTopic
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class FeedEnvelopeDeleteConsumer(private val feedEnvelopePersistence: FeedEnvelopePersistence, kafkaServer: String) :
    AutoCloseable {
    private val consumer = Consumer(kafkaServer, groupId, feedEnvelopeDeleteTopic)

    fun start() = consumer.start(this::handleDeleteMessage)

    override fun close() = consumer.stop()

    private fun handleDeleteMessage(topic: String, feedEnvelope: FeedEnvelope) {
        try {
            feedEnvelopePersistence.deleteFeedEnvelope(feedEnvelope)
            logger.info("Deleted feed envelope '${feedEnvelope.name}' received via topic '${topic}'")
        } catch (e: Exception) {
            logger.error("Error deleting feed envelope '${feedEnvelope.name}' received via topic '${topic}'", e)
        }
    }

    private companion object {
        val logger: Logger = LoggerFactory.getLogger(FeedEnvelopeDeleteConsumer::class.java)
        const val groupId = "funnel.connector.persistence.delete"
    }
}