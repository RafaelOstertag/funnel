package ch.guengel.funnel.domain

data class FeedEnvelope(val source: Source, val feed: Feed) {
    val name = source.name
    val lastUpdated get() = feed.lastUpdated

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FeedEnvelope

        if (source != other.source) return false

        return true
    }

    override fun hashCode(): Int {
        return source.hashCode()
    }
}