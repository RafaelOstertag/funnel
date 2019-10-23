package adapters

import data.Feed
import data.Source

interface FeedRetriever {
    suspend fun fetch(source: Source): Feed
}