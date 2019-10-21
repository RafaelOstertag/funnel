package logic

import data.Feed
import data.FeedEnvelope

class FeedEnvelopeMerger {
    fun merge(feedEnvelope1: FeedEnvelope, feedEnvelope2: FeedEnvelope): FeedEnvelope {
        return with(feedEnvelope2) {
            val mergedFeed = with(feed) {
                val mergedItems = feedItems + feedEnvelope1.feed.feedItems
                Feed(id, title, mergedItems)
            }
            FeedEnvelope(source, mergedFeed)
        }
    }
}