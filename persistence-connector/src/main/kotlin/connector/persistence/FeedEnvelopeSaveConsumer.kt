package ch.guengel.funnel.connector.persistence

import ch.guengel.funnel.feed.data.FeedEnvelope
import ch.guengel.funnel.feed.logic.FeedEnvelopeSaver
import ch.guengel.funnel.kafka.Consumer
import ch.guengel.funnel.kafka.feedEnvelopePersistTopic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class FeedEnvelopeSaveConsumer(private val feedEnvelopeSaver: FeedEnvelopeSaver, kafkaServer: String) : AutoCloseable {
    private val consumer = Consumer(kafkaServer, groupId, feedEnvelopePersistTopic)

    fun start() = consumer.start(this::handleSaveMessage)

    override fun close() = consumer.stop()

    private fun handleSaveMessage(topic: String, feedEnvelope: FeedEnvelope) {
        try {
            feedEnvelopeSaver.save(feedEnvelope)
            logger.info("Saved feed envelope '${feedEnvelope.name}' received via topic '${topic}'")
        } catch (e: Exception) {
            logger.error("Error saving feed envelope '${feedEnvelope.name}' received via topic '${topic}'", e)
        }
    }

    private companion object {
        val logger: Logger = LoggerFactory.getLogger(FeedEnvelopeSaveConsumer::class.java)
        const val groupId = "funnel.connector.persistence.save"
    }
}