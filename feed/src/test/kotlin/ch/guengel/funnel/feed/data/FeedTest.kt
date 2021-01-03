package ch.guengel.funnel.feed.data

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import ch.guengel.funnel.feed.logic.createFeedItem
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime

internal class FeedTest {

    @Test
    fun `last updated on empty feeds`() {
        val emptyFeed = Feed("1", "", FeedItems(emptyList()))
        assertThat(emptyFeed.lastUpdated).isEqualTo(FeedConstants.emptyCreated)
    }


    @Test
    fun `last updated`() {
        val now = OffsetDateTime.now()
        val feedItem1 = createFeedItem("1", now)
        val feedItem2 = createFeedItem("2", now.minusDays(1))
        val feed = Feed(
            "1",
            "",
            FeedItems(listOf(feedItem1, feedItem2))
        )
        assertThat(feed.lastUpdated).isEqualTo(now)
    }

    @Test
    fun `empty feed`() {
        val emptyFeed = Feed()
        assertThat(emptyFeed.lastUpdated).isEqualTo(FeedConstants.emptyCreated)
        assertThat(emptyFeed.id).isEqualTo("")
        assertThat(emptyFeed.title).isEqualTo("")
        assertThat(emptyFeed.feedItems.isEmpty).isTrue()
    }

    @Test
    fun equality() {
        val now = OffsetDateTime.now()
        val feedItem1 = createFeedItem("1", now)
        val feedItem2 = createFeedItem("2", now.minusDays(1))
        val feed1 = Feed(
            "1",
            "",
            FeedItems(listOf(feedItem1, feedItem2))
        )

        val feedItem3 = createFeedItem("1", now)
        val feedItem4 = createFeedItem("2", now.minusDays(1))
        val feed2 = Feed(
            "1",
            "",
            FeedItems(listOf(feedItem3, feedItem4))
        )
        assertThat(feed1).isEqualTo(feed2)
        assertThat(feed1.hashCode()).isEqualTo(feed2.hashCode())
    }
}