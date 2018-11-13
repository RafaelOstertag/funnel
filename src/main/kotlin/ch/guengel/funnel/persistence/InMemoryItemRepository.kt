package ch.guengel.funnel.persistence

import ch.guengel.funnel.domain.NewsItem
import ch.guengel.funnel.domain.NewsItems

class InMemoryItemRepository : NewsItemRepository {
    private val store: MutableMap<String, NewsItem> = HashMap()
    override fun retrieveAll(): NewsItems {
        val newsItems = NewsItems()
        return store.values.fold(newsItems) { a, b -> a.add(b); a }
    }

    override fun save(newsItems: NewsItems) {

    }
}