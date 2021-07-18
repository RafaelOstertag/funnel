package ch.guengel.funnel.feed.data

import java.time.OffsetDateTime

data class FeedItem(
    val id: String,
    val title: String,
    val link: String,
    val created: OffsetDateTime,
) : Comparable<FeedItem> {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FeedItem

        if (id != other.id) return false
        if (created != other.created) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + created.hashCode()
        return result
    }

    override fun compareTo(other: FeedItem): Int {
        return comparator.compare(this, other)
    }

    companion object {
        private val comparator = FeedItemComparator()
        val empty = FeedItem(
            "",
            "",
            "",
            FeedConstants.emptyCreated
        )
    }
}
