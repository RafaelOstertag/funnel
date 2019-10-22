package data

import java.time.OffsetDateTime

class Feed(val id: String, val title: String, val feedItems: FeedItems) {
    constructor() : this("", "", FeedItems())

    val lastUpdated: OffsetDateTime get() = feedItems.latest.created

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Feed

        if (id != other.id) return false
        if (title != other.title) return false
        if (feedItems != other.feedItems) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + feedItems.hashCode()
        return result
    }
}
