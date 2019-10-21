package data

import java.time.ZonedDateTime

class Feed(val id: String, val title: String, val feedItems: FeedItems) {
    constructor() : this("", "", FeedItems())

    val lastUpdated: ZonedDateTime get() = feedItems.latest.created
}
