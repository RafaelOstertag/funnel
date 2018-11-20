package ch.guengel.funnel.domain

import java.util.*

class FeedItems(val maxItems: Int = 20) {
    private val internalItems: MutableSet<FeedItem> = TreeSet()
    val size: Int get() = internalItems.size
    val items: Set<FeedItem> get() = internalItems
    val latest: FeedItem
        get() {
            return if (internalItems.isEmpty())
                FeedItem.empty()
            else
                internalItems.last()
        }

    fun add(item: FeedItem) {
        internalItems.add(item)
        trimToMaxItems()
    }

    fun hasItem(item: FeedItem): Boolean = internalItems.contains(item)

    fun mergeWith(feedItems: FeedItems) {
        internalItems.addAll(feedItems.internalItems)
        trimToMaxItems()
    }

    private fun trimToMaxItems() {
        while (internalItems.size > maxItems) {
            internalItems.remove(internalItems.first())
        }
    }
}