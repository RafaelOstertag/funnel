package ch.guengel.funnel.domain

import java.time.ZonedDateTime

class Feed(val id: String, val title: String, val newsItems: NewsItems) {
    val lastUpdated: ZonedDateTime get() = newsItems.latest.created

    fun mergeWith(feed: Feed) {
        if (!id.equals(feed.id)) {
            throw CannotMergeFeeds("Destination id '$id' does not match source id '$id'")
        }

        newsItems.mergeWith(feed.newsItems)
    }
}

class CannotMergeFeeds(message: String) : Exception(message)
