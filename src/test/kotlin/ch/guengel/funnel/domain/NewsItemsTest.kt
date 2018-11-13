package ch.guengel.funnel.domain

import org.junit.Test
import kotlin.test.assertEquals


class NewsItemsTest {

    @Test
    fun `empty NewsItems`() {
        val empty = NewsItems()

        assertEquals(NewsItem.empty(), empty.latest)
    }

    @Test
    fun `should properly maintain latest`() {
        val item1 = makeItem(1)
        val item2 = makeItem(2)

        val newsItems = NewsItems()
        newsItems.add(item1)
        newsItems.add(item2)

        assertEquals(item2, newsItems.latest)
    }

    @Test
    fun `should merge`() {
        val item1 = makeItem(1)
        val item2 = makeItem(2)

        val newsItems1 = NewsItems()
        newsItems1.add(item1)
        newsItems1.add(item2)

        val item3 = makeItem(3)
        val item4 = makeItem(4)

        val newsItems2 = NewsItems()
        newsItems2.add(item3)
        newsItems2.add(item4)

        newsItems1.mergeWith(newsItems2)

        assertEquals(4, newsItems1.size)
        assertEquals(item4, newsItems2.latest)
    }
}