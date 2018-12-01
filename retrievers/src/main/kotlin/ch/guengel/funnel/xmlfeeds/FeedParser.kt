package ch.guengel.funnel.xmlfeeds

import ch.guengel.funnel.domain.Feed
import java.time.ZonedDateTime

interface FeedParser {
    fun parse(xml: String, ignoreItemsBefore: ZonedDateTime): Feed
}

class CannotDeserializeXML(cause: Throwable) : Exception(cause)