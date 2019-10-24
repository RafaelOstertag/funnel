package ch.guengel.funnel.feed.bridges

import ch.guengel.funnel.feed.data.FeedEnvelope

interface FeedEnvelopePersistence {
    fun findFeedEnvelope(name: String): FeedEnvelope
    fun findAllFeedEnvelopes(): List<FeedEnvelope>
    fun saveFeedEnvelope(feedEnvelope: FeedEnvelope)
    fun deleteFeedEnvelope(feedEnvelope: FeedEnvelope)
}