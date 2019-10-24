package ch.guengel.funnel.feed.logic

import ch.guengel.funnel.feed.bridges.FeedEnvelopePersistence
import ch.guengel.funnel.feed.data.Feed
import ch.guengel.funnel.feed.data.FeedEnvelope
import ch.guengel.funnel.feed.data.Source

class FeedEnvelopeRemover(private val feedEnvelopePersistence: FeedEnvelopePersistence) {
    fun remove(feedEnvelope: FeedEnvelope) {
        feedEnvelopePersistence.deleteFeedEnvelope(feedEnvelope)
    }

    fun remove(feedEnvelopeName: String) {
        val source = Source(feedEnvelopeName, "should be ignored")
        remove(FeedEnvelope(source, Feed()))
    }
}