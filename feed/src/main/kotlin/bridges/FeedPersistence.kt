package bridges

import data.FeedEnvelope

interface FeedPersistence {
    fun findFeedEnvelope(name: String): FeedEnvelope
    fun findAllFeedEnvelopes(): List<FeedEnvelope>
    fun saveFeedEnvelope(feedEnvelope: FeedEnvelope)
    fun deleteFeedEnvelope(feedEnvelope: FeedEnvelope)
}