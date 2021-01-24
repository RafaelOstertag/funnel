package ch.guengel.funnel.feed.logic

import ch.guengel.funnel.feed.bridges.FeedRetriever
import ch.guengel.funnel.feed.data.FeedEnvelope
import ch.guengel.funnel.feed.data.Source
import ch.guengel.funnel.feed.data.User

class FeedEnvelopeRetriever(private val feedRetriever: FeedRetriever) {
    suspend fun retrieve(userId: String, source: Source): FeedEnvelope {
        val feed = feedRetriever.fetch(source)
        val user = User(userId, "- n/a -")
        return FeedEnvelope(user, source, feed)
    }
}