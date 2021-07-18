package ch.guengel.funnel.feed.data

import java.util.*

class FeedItems {
    val items: Set<FeedItem>
    val size: Int get() = items.size
    val isEmpty: Boolean get() = items.isEmpty()
    val latest: FeedItem
        get() = if (isEmpty) FeedItem.empty else items.last()

    constructor() {
        this.items = emptySet()
    }

    constructor(items: Collection<FeedItem>) {
        this.items = asSortedSet(items)
    }

    private fun asSortedSet(items: Collection<FeedItem>): TreeSet<FeedItem> =
        TreeSet<FeedItem>().apply { addAll(items) }

    fun hasItem(item: FeedItem): Boolean = items.contains(item)

    operator fun plus(item: FeedItem): FeedItems =
        asSortedSet(items)
            .apply { add(item) }
            .let { FeedItems(it) }

    operator fun plus(feedItems: FeedItems): FeedItems =
        TreeSet<FeedItem>()
            .apply { addAll(items) }
            .apply { addAll(feedItems.items) }
            .let { FeedItems(it) }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FeedItems

        if (items != other.items) return false

        return true
    }

    override fun hashCode(): Int {
        return items.hashCode()
    }
}
