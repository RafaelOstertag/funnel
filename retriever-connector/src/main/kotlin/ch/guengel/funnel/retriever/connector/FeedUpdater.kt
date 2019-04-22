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
                logger.debug("No new feed items for {} since {}", source.address, lastUpdated)
                return@launch
            }

            logger.debug("New feed items for {} since {}", source.address, lastUpdated)

            informOfNewFeedItems(feedEnvelope, retrievedFeed)

            saveMergedFeedEvelope(feedEnvelope, retrievedFeed)
        }

    }

    private fun saveMergedFeedEvelope(feedEnvelope: FeedEnvelope, retrievedFeed: Feed) {
        feedEnvelope.feed.mergeWith(retrievedFeed)
        producer.send(Topics.persistFeed, Constants.noKey, serialize(feedEnvelope))
        logger.debug("Publish persistence event for {}", feedEnvelope.source)
    }

    private fun informOfNewFeedItems(currentFeedEnvelope: FeedEnvelope, newFeed: Feed) {
        val feedEnvelopeWithOnlyNewItems = FeedEnvelope(currentFeedEnvelope.source, newFeed)
        producer.send(Topics.feedUpdate, "", serialize(feedEnvelopeWithOnlyNewItems))
        logger.debug("Publish update event for {}", feedEnvelopeWithOnlyNewItems.source)
    }

    private suspend fun retrieveFeed(source: Source, lastUpdated: ZonedDateTime): Feed {
        val httpTransport = HttpTransport(source)
        val xmlFeedRetriever = XmlFeedRetriever(httpTransport)
        return xmlFeedRetriever.retrieve(lastUpdated)
    }
}