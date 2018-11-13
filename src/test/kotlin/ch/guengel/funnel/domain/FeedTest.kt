package ch.guengel.funnel.domain

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.jupiter.api.assertThrows

class FeedTest {

    @Test
    fun `keep track of last updated of news items`() {
        val newsItems = NewsItems()
        val feed = Feed("id", "title", newsItems)

        assertEquals(NewsItem.emptyCreated, feed.lastUpdated)

        val newsItem = makeItem(1)
        newsItems.add(newsItem)

        val expectedDate = newsItem.created
        assertEquals(expectedDate, feed.lastUpdated)
    }

    @Test
    fun `should merge`() {
        val item1 = makeItem(1)
        val item2 = makeItem(2)
        val newsItems1 = NewsItems()
        newsItems1.add(item1)
        newsItems1.add(item2)

        val feed1 = Feed("id", "title", newsItems1)

        val item3 = makeItem(3)
        val item4 = makeItem(4)
        val newsItems2 = NewsItems()
        newsItems2.add(item3)
        newsItems2.add(item4)

        val feed2 = Feed("id", "title", newsItems2)

        feed1.mergeWith(feed2)
        assertEquals(4, feed1.newsItems.size)
        assertEquals(item4.created, feed1.lastUpdated)
    }

    @Test
    fun `should not merge with different id`() {
        val feed1 = Feed("id1", "title", NewsItems())
        val feed2 = Feed("id2", "title", NewsItems())

        assertThrows<CannotMergeFeeds> { feed1.mergeWith(feed2) }
    }
}