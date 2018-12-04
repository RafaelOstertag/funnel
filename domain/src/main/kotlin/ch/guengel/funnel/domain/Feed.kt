package ch.guengel.funnel.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.ZonedDateTime

class Feed(val id: String, val title: String, val feedItems: FeedItems) {
    @get:JsonIgnore
    val lastUpdated: ZonedDateTime get() = feedItems.latest.created

    fun mergeWith(feed: Feed) {
        feedItems.mergeWith(feed.feedItems)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Feed

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }


    companion object {
        fun empty() = Feed("", "", FeedItems())
    }
}
