package data

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

internal class FeedTest {

    @Test
    fun `last updated on empty feeds`() {
        val emptyFeed = Feed("1", "", FeedItems(emptyList()))
        assertThat(emptyFeed.lastUpdated).isEqualTo(FeedConstants.emptyCreated)
    }

    @Test
    fun `last updated`() {
        val now = ZonedDateTime.now()
        val feedItem1 = FeedItem("1", "", now)
        val feedItem2 = FeedItem("2", "", now.minusDays(1))
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
}