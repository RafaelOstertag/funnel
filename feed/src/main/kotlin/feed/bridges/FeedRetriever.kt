package ch.guengel.funnel.feed.bridges

import ch.guengel.funnel.feed.data.Feed
import ch.guengel.funnel.feed.data.Source

interface FeedRetriever {
    suspend fun fetch(source: Source): Feed
}