package ch.guengel.funnel.feed.data

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotEqualTo
import assertk.assertions.isTrue
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime

internal class FeedItemsTest {
    private val now = OffsetDateTime.now()

    @Test
    fun `empty feed items`() {
        val feedItems = FeedItems()

        assertThat(feedItems.isEmpty).isTrue()
        assertThat(feedItems.size).isEqualTo(0)
        assertThat(feedItems.latest).isEqualTo(FeedItem.empty)
    }

    @Test
    fun `size of feed items`() {
        val feedItems = FeedItems(listOf(FeedItem.empty))
        assertThat(feedItems.size).isEqualTo(1)
    }

    @Test
    fun `isEmpty works`() {
        val feedItems = FeedItems(listOf(FeedItem.empty))
        assertThat(feedItems.isEmpty).isFalse()
    }

    @Test
    fun `keep items ordered by creation time`() {
        val item1 = FeedItem("1", "", now)

        val earlierThanNow = now.minusSeconds(1)
        val item2 = FeedItem("2", "", earlierThanNow)

        val feedItems = FeedItems(listOf(item1, item2))
        assertThat(feedItems.items.first())
            .isEqualTo(item2)
        assertThat(feedItems.items.last())
            .isEqualTo(item1)
    }

    @Test
    fun `latest returns latest feed item`() {
        val recentFeedItem = FeedItem("0", "", now)
        var feedItems = FeedItems(listOf(recentFeedItem))
        assertThat(feedItems.latest).isEqualTo(recentFeedItem)

        val older = now.minusSeconds(1)
        val lessRecentFeedItem = FeedItem("1", "", older)
        feedItems = FeedItems(listOf(lessRecentFeedItem, recentFeedItem))
        assertThat(feedItems.latest).isEqualTo(recentFeedItem)
    }

    @Test
    fun `add feed item`() {
        val feedItem1 = FeedItem("1", "", now)
        val feedItems1 = FeedItems(listOf(feedItem1))
        val feedItem2 = FeedItem("2", "", now.plusDays(1))

        val feedItemsSum = feedItems1 + feedItem2
        assertThat(feedItemsSum.hasItem(feedItem1)).isTrue()
        assertThat(feedItemsSum.hasItem(feedItem2)).isTrue()
    }

    @Test
    fun `add feed items`() {
        val feedItem1 = FeedItem("1", "", now)
        val feedItems1 = FeedItems(listOf(feedItem1))
        val feedItem2 = FeedItem("2", "", now.plusDays(1))
        val feedItems2 = FeedItems(listOf(feedItem2))

        val feedItemsSum = feedItems1 + feedItems2
        assertThat(feedItemsSum.hasItem(feedItem1)).isTrue()
        assertThat(feedItemsSum.hasItem(feedItem2)).isTrue()
    }

    @Test
    fun `not equal`() {
        val feedItem1 = FeedItem("1", "", now)
        val feedItems1 = FeedItems(listOf(feedItem1))

        val feedItem2 = FeedItem("2", "", now.plusDays(1))
        val feedItems2 = FeedItems(listOf(feedItem2))

        assertThat(feedItems1).isNotEqualTo(feedItems2)
    }

    @Test
    fun `is equal`() {
        val feedItem1 = FeedItem("1", "", now)
        val feedItems1 = FeedItems(listOf(feedItem1))

        val feedItem2 = FeedItem("1", "", now)
        val feedItems2 = FeedItems(listOf(feedItem2))

        assertThat(feedItems1).isEqualTo(feedItems2)
    }

    @Test
    fun `non-equal hash code`() {
        val feedItem1 = FeedItem("1", "", now)
        val feedItems1 = FeedItems(listOf(feedItem1))

        val feedItem2 = FeedItem("2", "", now.plusDays(1))
        val feedItems2 = FeedItems(listOf(feedItem2))

        assertThat(feedItems1.hashCode()).isNotEqualTo(feedItems2.hashCode())
    }

    @Test
    fun `equal hash code`() {
        val feedItem1 = FeedItem("1", "", now)
        val feedItems1 = FeedItems(listOf(feedItem1))

        val feedItem2 = FeedItem("1", "", now)
        val feedItems2 = FeedItems(listOf(feedItem2))

        assertThat(feedItems1.hashCode()).isEqualTo(feedItems2.hashCode())
    }
}