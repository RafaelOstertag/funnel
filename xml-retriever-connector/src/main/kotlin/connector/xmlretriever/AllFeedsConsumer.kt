package ch.guengel.funnel.connector.xmlretriever

import ch.guengel.funnel.feed.data.FeedEnvelope
import ch.guengel.funnel.feed.logic.FeedEnvelopeDifference
import ch.guengel.funnel.feed.logic.FeedEnvelopeMerger
import ch.guengel.funnel.feed.logic.FeedEnvelopeRetriever
import ch.guengel.funnel.kafka.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AllFeedsConsumer(private val feedEnvelopeRetriever: FeedEnvelopeRetriever, kafkaServer: String) : AutoCloseable {
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val consumer = Consumer(
        kafkaServer,
        groupId, allFeedTopics
    )
    private val producer = Producer(kafkaServer)
    private var closed = false

    fun start() {
        check(!closed) {
            val errorMessage = "Cannot re-start closed AllFeedsConsumer"
            logger.error(errorMessage)
            errorMessage
        }

        consumer.start(this::handleFeed)
    }

    private fun handleFeed(topic: String, currentFeedEnvelope: FeedEnvelope) {
        ioScope.launch {
            try {
                logger.info("Retrieving updates for '{}'", currentFeedEnvelope.name)
                val latestFeedEnvelope = retrieveLatestFeed(currentFeedEnvelope)
                notifyFeedUpdateIfRequired(currentFeedEnvelope, latestFeedEnvelope)
                FeedEnvelopeMerger()
                    .merge(currentFeedEnvelope, latestFeedEnvelope)
                    .let {
                        producer.send(feedEnvelopePersistTopic, it)
                    }
            } catch (e: Exception) {
                logger.error("Error while handling updates of '{}'", currentFeedEnvelope.name, e)
            }
        }

    }

    private fun notifyFeedUpdateIfRequired(currentFeedEnvelope: FeedEnvelope, latestFeedEnvelope: FeedEnvelope) {
        val feedEnvelopeDifference =
            FeedEnvelopeDifference().difference(currentFeedEnvelope, latestFeedEnvelope)
        if (!feedEnvelopeDifference.feed.feedItems.isEmpty) {
            logger.info("Notifying of new items in feed '{}'", latestFeedEnvelope.name)
            producer.send(updateNotificationTopic, feedEnvelopeDifference)
        }
    }

    private suspend fun retrieveLatestFeed(currentFeedEnvelope: FeedEnvelope): FeedEnvelope {
        val source = currentFeedEnvelope.source
        val latestFeedEnvelope = feedEnvelopeRetriever.retrieve(source)
        logger.debug("Retrieved latest feed for '${source.name}'")
        return latestFeedEnvelope
    }

    override fun close() {
        consumer.stop()
        producer.close()
        ioScope.cancel()
        closed = true
        logger.debug("AllFeedsConsumer closed")
    }

    private companion object {
        val logger: Logger = LoggerFactory.getLogger(AllFeedsConsumer::class.java)
        const val groupId = "funnel.retriever.connector"
    }
}