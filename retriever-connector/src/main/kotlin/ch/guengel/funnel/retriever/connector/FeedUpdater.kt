package ch.guengel.funnel.retriever.connector

import ch.guengel.funnel.common.serialize
import ch.guengel.funnel.domain.Feed
import ch.guengel.funnel.domain.FeedEnvelope
import ch.guengel.funnel.domain.Source
import ch.guengel.funnel.kafka.Constants
import ch.guengel.funnel.kafka.Producer
import ch.guengel.funnel.kafka.Topics
import ch.guengel.funnel.xmlretriever.XmlFeedRetriever
import ch.guengel.funnel.xmlretriever.network.HttpTransport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.time.ZonedDateTime

class FeedUpdater(private val producer: Producer) {
    companion object {
        val logger = LoggerFactory.getLogger(FeedUpdater::class.java)
    }

    fun updateFeed(feedEnvelope: FeedEnvelope): Job {
        val source = feedEnvelope.source
        val lastUpdated = feedEnvelope.lastUpdated

        return CoroutineScope(Dispatchers.IO).launch {
            val retrievedFeed = retrieveFeed(source, lastUpdated)
            if (retrievedFeed.feedItems.empty) {
                logger.info("No new feed items for {} since {}", source.address, lastUpdated)
                return@launch
            }

            logger.info("New feed items for {} since {}", source.address, lastUpdated)

            // This will guarantee that we have the current feed id and title in the envelope
            val newFeedEnvelope = createNewFeedEnvelopeFromRetrievedFeed(feedEnvelope, retrievedFeed)

            informOfNewFeedItems(newFeedEnvelope)

            saveMergedFeedEvelope(feedEnvelope, newFeedEnvelope)
        }

    }

    /**
     * Given an existing feed envelope and a feed, creates a new feed envelope from the feed.
     */
    private fun createNewFeedEnvelopeFromRetrievedFeed(existingFeedEnvelope: FeedEnvelope, feed: Feed) =
        FeedEnvelope(existingFeedEnvelope.source, feed)

    /**
     * Merge the old feed envelope to the new feed envelope
     */
    private fun saveMergedFeedEvelope(oldFeedEnvelope: FeedEnvelope, newFeedEnvelope: FeedEnvelope) {
        newFeedEnvelope.feed.mergeWith(oldFeedEnvelope.feed)
        producer.send(Topics.persistFeed, Constants.noKey, serialize(newFeedEnvelope))
        logger.debug("Publish persistence event for {}", newFeedEnvelope.source)
    }

    private fun informOfNewFeedItems(newFeedEnvelope: FeedEnvelope) {
        producer.send(Topics.feedUpdate, "", serialize(newFeedEnvelope))
        logger.debug("Publish update event for {}", newFeedEnvelope.source)
    }

    private suspend fun retrieveFeed(source: Source, lastUpdated: ZonedDateTime): Feed {
        val httpTransport = HttpTransport(source)
        val xmlFeedRetriever = XmlFeedRetriever(httpTransport)
        return xmlFeedRetriever.retrieve(lastUpdated)
    }
}