package ch.guengel.funnel.xmlretriever

import ch.guengel.funnel.domain.Feed
import java.time.ZonedDateTime

interface FeedRetriever {
    suspend fun retrieve(since: ZonedDateTime): Feed
}