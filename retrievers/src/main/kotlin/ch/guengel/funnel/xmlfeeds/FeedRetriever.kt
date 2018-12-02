package ch.guengel.funnel.xmlfeeds

import ch.guengel.funnel.domain.Feed
import java.time.ZonedDateTime

interface FeedRetriever {
    suspend fun retrieve(since: ZonedDateTime): Feed
}