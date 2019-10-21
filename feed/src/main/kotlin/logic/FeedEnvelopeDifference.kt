package logic

import data.Feed
import data.FeedEnvelope
import data.FeedItems

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
            FeedEnvelope(source, newFeed)
        }
    }
}