package ch.guengel.funnel.domain

class FeedItems {
    private val internalItems: MutableSet<FeedItem> = HashSet()
    val size: Int get() = internalItems.size
    val items: Set<FeedItem> get() = internalItems
    var latest: FeedItem = FeedItem.empty()
        private set

    fun add(item: FeedItem) {
        if (internalItems.add(item)) {
            updateLatest(item)
        }
    }

    private fun updateLatest(item: FeedItem) {
        if (latest.created.isBefore(item.created)) {
            latest = item
        }
    }

    fun hasItem(item: FeedItem): Boolean = internalItems.contains(item)

    fun mergeWith(feedItems: FeedItems) {
        feedItems.internalItems.forEach(::add)
    }
}