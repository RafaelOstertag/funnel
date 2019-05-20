package ch.guengel.funnel.retriever.connector

import ch.guengel.funnel.common.deserialize
import ch.guengel.funnel.domain.FeedEnvelope
import ch.guengel.funnel.kafka.Producer
import ch.guengel.funnel.kafka.Topics
import com.uchuhimo.konf.Config
import org.slf4j.LoggerFactory
import java.io.Closeable

class TopicHandler(configuration: Config) : Closeable {
    private val kafkaProducer = Producer(configuration[Configuration.kafka])
    private val feedUpdater = FeedUpdater(kafkaProducer)

    private fun handleRetrieveAll(data: String) {
        if (data.isBlank()) {
            return
        }

        try {
            logger.info("Process feeds arrived on '${allFeedReplyTopic}'")
            val feedEnvelopes = deserialize<List<FeedEnvelope>>(data)
            feedEnvelopes.forEach {
                feedUpdater.updateFeed(it)
            }
            logger.info("Done processing feeds for '${allFeedReplyTopic}'")
        } catch (e: Throwable) {
            logger.error("Error responding to feed retrieval response", e)
        }
    }

    fun handle(topic: String, key: String, data: String) {
        when (topic) {
            allFeedReplyTopic -> handleRetrieveAll(data)
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