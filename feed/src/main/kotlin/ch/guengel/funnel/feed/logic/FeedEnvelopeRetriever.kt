package ch.guengel.funnel.feed.logic

import ch.guengel.funnel.feed.bridges.FeedRetriever
import ch.guengel.funnel.feed.data.FeedEnvelope
import ch.guengel.funnel.feed.data.Source

class FeedEnvelopeRetriever(private val feedRetriever: FeedRetriever) {
    suspend fun retrieve(source: Source): FeedEnvelope {
        val feed = feedRetriever.fetch(source)
        return FeedEnvelope(source, feed)
    }
}