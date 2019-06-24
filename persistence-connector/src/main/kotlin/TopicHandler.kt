package ch.guengel.funnel.persistence.connector

import ch.guengel.funnel.common.deserialize
import ch.guengel.funnel.domain.FeedEnvelope
import ch.guengel.funnel.persistence.FeedEnvelopeRepository
import org.slf4j.LoggerFactory

class TopicHandler(private val feedEnvelopeRepository: FeedEnvelopeRepository) {

    private fun saveFeed(serializedData: String) {
        try {
            deserialize<FeedEnvelope>(serializedData).apply {
                feedEnvelopeRepository.save(this)
            }.also {
                logger.info("Saved feed envelope '${it.name}'")
            }
        } catch (e: Throwable) {
            logger.error("Error saving feed envelope", e)
        }
    }

    fun handle(topic: String, key: String, data: String) {
        when (topic) {
            PERSIST_TOPIC -> saveFeed(data)
            DELETE_TOPIC -> deleteFeed(data)
            else -> logger.error("Don't know how to handle message in topic '$topic'")
        }
    }

    private fun deleteFeed(name: String) {
        try {
            feedEnvelopeRepository.deleteByName(name)
            logger.info("Deleted feed ${name}")
        } catch (e: Throwable) {
            logger.error("Error deleting feed ${name}")
        }
    }

    companion object {
        val logger = LoggerFactory.getLogger(TopicHandler::class.java)
    }

}