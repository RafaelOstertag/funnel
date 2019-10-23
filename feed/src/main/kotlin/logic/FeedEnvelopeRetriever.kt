package logic


import adapters.FeedRetriever
import data.FeedEnvelope
import data.Source

class FeedEnvelopeRetriever(private val feedRetriever: FeedRetriever) {
    suspend fun retrieve(source: Source): FeedEnvelope {
        val feed = feedRetriever.fetch(source)
        return FeedEnvelope(source, feed)
    }
}