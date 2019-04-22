package ch.guengel.funnel.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*

class FeedItems(val maxItems: Int = 20) {
    private val internalItems: MutableSet<FeedItem> = TreeSet()
    @get:JsonIgnore
    val size: Int get() = internalItems.size
    @get:JsonIgnore
    val empty: Boolean
        get() = internalItems.isEmpty()
    val items: Set<FeedItem> get() = internalItems
    @get:JsonIgnore
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