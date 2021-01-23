package ch.guengel.funnel.feed.logic

import ch.guengel.funnel.feed.bridges.FeedEnvelopePersistence
import ch.guengel.funnel.feed.data.Feed
import ch.guengel.funnel.feed.data.FeedEnvelope
import ch.guengel.funnel.feed.data.Source
import ch.guengel.funnel.feed.data.User

class FeedEnvelopeRemover(private val feedEnvelopePersistence: FeedEnvelopePersistence) {
    fun remove(feedEnvelope: FeedEnvelope): Boolean {
        return feedEnvelopePersistence.deleteFeedEnvelope(feedEnvelope)
    }

    fun remove(userId: String, feedEnvelopeName: String): Boolean {
        val source = Source(feedEnvelopeName, "- n/a -")
        val user = User(userId, "- n/a -")
        return remove(FeedEnvelope(user, source, Feed()))
    }
}