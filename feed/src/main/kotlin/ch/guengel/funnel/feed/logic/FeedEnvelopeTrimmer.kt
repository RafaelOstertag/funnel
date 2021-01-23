package ch.guengel.funnel.feed.logic

import ch.guengel.funnel.feed.data.Feed
import ch.guengel.funnel.feed.data.FeedEnvelope
import ch.guengel.funnel.feed.data.FeedItems

class FeedEnvelopeTrimmer(private val size: Int) {
    fun trim(feedEnvelope: FeedEnvelope): FeedEnvelope {
        val feedItemSize = feedEnvelope.feed.feedItems.size

        if (size >= feedItemSize) {
            return feedEnvelope
        }

        return trimBySkipping(feedItemSize, feedEnvelope)
    }

    private fun trimBySkipping(feedItemSize: Int, feedEnvelope: FeedEnvelope): FeedEnvelope {
        // FeedItems are sorted ascending on created date, thus we have to skip
        // the first couple of items
        var keepFrom = feedItemSize - size
        keepFrom = if (keepFrom < 0) 0 else keepFrom

        var counter = 0
        val trimmedFeeds = feedEnvelope
            .feed
            .feedItems
            .items.partition { _ -> counter++ < keepFrom }

        return with(feedEnvelope.feed) {
            FeedEnvelope(
                feedEnvelope.user,
                feedEnvelope.source,
                Feed(id, title, FeedItems(trimmedFeeds.second))
            )
        }
    }
}