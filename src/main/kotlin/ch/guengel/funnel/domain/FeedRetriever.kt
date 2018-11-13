package ch.guengel.funnel.domain

import java.time.ZonedDateTime

interface FeedRetriever {
    fun retrieve(since: ZonedDateTime): Feed
}