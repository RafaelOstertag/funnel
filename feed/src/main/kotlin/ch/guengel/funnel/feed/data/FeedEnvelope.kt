package ch.guengel.funnel.feed.data

data class FeedEnvelope(val user: User, val source: Source, val feed: Feed) {
    val name = source.name
}