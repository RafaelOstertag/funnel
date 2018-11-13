package ch.guengel.funnel.domain

class NewsItems {
    private val internalItems: MutableSet<NewsItem> = HashSet()
    val size: Int get() = internalItems.size
    val items: Set<NewsItem> get() = internalItems
    var latest: NewsItem = NewsItem.empty()
        private set

    fun add(item: NewsItem) {
        if (internalItems.add(item)) {
            updateLatest(item)
        }
    }

    private fun updateLatest(item: NewsItem) {
        if (latest.created.isBefore(item.created)) {
            latest = item
        }
    }

    fun hasItem(item: NewsItem): Boolean = internalItems.contains(item)

    fun mergeWith(newsItems: NewsItems) {
        newsItems.internalItems.forEach(::add)
    }
}