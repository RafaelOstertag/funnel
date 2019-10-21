package adapters

import data.Feed
import data.Source

interface FeedRetriever {
    fun fetch(source: Source): Feed
}