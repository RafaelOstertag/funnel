package ch.guengel.funnel.domain

import assertk.assert
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import ch.guengel.funnel.makeFeedItems
import ch.guengel.funnel.makeItem
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class FeedItemsTest {

    @Test
    fun `empty NewsItems`() {
        val empty = FeedItems()

        assertEquals(FeedItem.empty(), empty.latest)
    }

    @Test
    fun `should properly maintain latest`() {
        val item1 = makeItem(1)
        val item2 = makeItem(2)

        val newsItems = FeedItems()
        newsItems.add(item1)
        newsItems.add(item2)

        assertEquals(item2, newsItems.latest)
    }

    @Test
    fun `should merge`() {
        val item1 = makeItem(1)
        val item2 = makeItem(2)

        val newsItems1 = FeedItems()
        newsItems1.add(item1)
        newsItems1.add(item2)

        val item3 = makeItem(3)
        val item4 = makeItem(4)

        val newsItems2 = FeedItems()
        newsItems2.add(item3)
        newsItems2.add(item4)

        newsItems1.mergeWith(newsItems2)

        assertEquals(4, newsItems1.size)
        assertEquals(item4, newsItems2.latest)
    }

    @Test
    fun `should handle max items`() {
        val newsItems = FeedItems(2)
        for (i in 1..9) {
            newsItems.add(makeItem(i))
        }

        assertEquals(2, newsItems.size)
        assertEquals(makeItem(9), newsItems.latest)
    }

    @Test
    fun `has item`() {
        val feedItems = makeFeedItems(2)
        val feedItem = makeItem(1)
        assertEquals(true, feedItems.hasItem(feedItem))
    }

    @Test
    fun `size should report correct size`() {
        var feedItems = makeFeedItems(2)
        assert(feedItems.size).isEqualTo(2)

        feedItems = makeFeedItems(0)
        assert(feedItems.size).isEqualTo(0)
    }

    @Test
    fun `empty should work`() {
        var feedItems = makeFeedItems(2)
        assert(feedItems.empty).isFalse()

        feedItems = makeFeedItems(0)
        assert(feedItems.empty).isTrue()
    }


}