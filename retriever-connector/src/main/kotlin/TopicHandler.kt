package ch.guengel.funnel.retriever.connector

import ch.guengel.funnel.common.deserialize
import ch.guengel.funnel.domain.FeedEnvelope
import org.slf4j.LoggerFactory

class TopicHandler(private val feedUpdater: FeedUpdater) {
    private fun handleRetrieveAll(data: String) {
        try {
            logger.info("Process feeds arrived on '${ALL_FEEDS_TOPIC}'")
            val feedEnvelopes = deserialize<List<FeedEnvelope>>(data)
            feedEnvelopes.forEach {
                feedUpdater.updateFeed(it)
            }
            logger.info("Done processing feeds for '${ALL_FEEDS_TOPIC}'")
        } catch (e: Throwable) {
            logger.error("Error responding to feed retrieval response", e)
        }
    }

    fun handle(topic: String, key: String, data: String) {
        when (topic) {
            ALL_FEEDS_TOPIC -> handleRetrieveAll(data)
            else -> logger.error("Don't know how to handle message in topic '$topic'")
        }
    }

    companion object {
        val logger = LoggerFactory.getLogger(TopicHandler::class.java)
    }

}