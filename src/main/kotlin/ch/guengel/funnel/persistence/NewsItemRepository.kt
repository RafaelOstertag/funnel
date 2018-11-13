package ch.guengel.funnel.persistence

import ch.guengel.funnel.domain.NewsItems

interface NewsItemRepository {
    fun retrieveAll(): NewsItems
    fun save(newsItems: NewsItems)
}