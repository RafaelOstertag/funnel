package ch.guengel.funnel.feed.logic

import ch.guengel.funnel.feed.data.Feed
import ch.guengel.funnel.feed.data.FeedEnvelope
import ch.guengel.funnel.feed.data.FeedItems


class FeedEnvelopeDifference {
    fun difference(current: FeedEnvelope, latest: FeedEnvelope): FeedEnvelope {
        val currentLastUpdated = current.feed.lastUpdated
        val difference = latest
            .feed
            .feedItems
            .items
            .filter { feedItem -> feedItem.created.isAfter(currentLastUpdated) }

        return with(latest) {
            val newFeed = with(feed) {
                Feed(id, title, FeedItems(difference))
            }
            FeedEnvelope(current.user, source, newFeed)
        }
    }
}