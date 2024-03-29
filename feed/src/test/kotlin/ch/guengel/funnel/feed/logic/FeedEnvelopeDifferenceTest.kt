package ch.guengel.funnel.feed.logic

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import ch.guengel.funnel.feed.data.Feed
import ch.guengel.funnel.feed.data.FeedEnvelope
import ch.guengel.funnel.feed.data.FeedItem
import ch.guengel.funnel.feed.data.FeedItems
import ch.guengel.funnel.feed.data.Source
import ch.guengel.funnel.feed.data.User
import org.junit.jupiter.api.Test

internal class FeedEnvelopeDifferenceTest {
    private val feedEnvelopeDifference = FeedEnvelopeDifference()

    @Test
    fun `zero difference`() {
        val currentFeedItems = makeFeedItems(3)
        val currentSource = Source("name1", "address1")
        val currentFeed = Feed("id1", "title1", FeedItems(currentFeedItems))
        val currentUser = User("userId", "email")
        val currentFeedEnvelope = FeedEnvelope(currentUser, currentSource, currentFeed)

        val difference = feedEnvelopeDifference.difference(currentFeedEnvelope, currentFeedEnvelope)

        assertThat(difference.user).isEqualTo(currentUser)
        assertThat(difference.source).isEqualTo(currentSource)
        with(difference.feed) {
            assertThat(id).isEqualTo("id1")
            assertThat(title).isEqualTo("title1")
            assertThat(feedItems.isEmpty).isTrue()
        }
    }

    @Test
    fun `one feed difference`() {
        val currentFeedItems = makeFeedItems(3)
        val currentFeed = Feed("id1", "title1", FeedItems(currentFeedItems))
        val currentSource = Source("name1", "address1")
        val currentUser = User("userId", "email")
        val currentFeedEnvelope = FeedEnvelope(currentUser, currentSource, currentFeed)

        val latestFeedItem = FeedItem("latest", "title", "link", now.plusDays(1))
        val latestFeedItems = FeedItems(listOf(latestFeedItem))
        val latestFeed = Feed("id2", "title2", latestFeedItems)
        val latestSource = Source("name2", "address2")
        val latestUser = User("userId-1", "email-1")
        val latestFeedEnvelope = FeedEnvelope(latestUser, latestSource, latestFeed)

        val difference = feedEnvelopeDifference.difference(currentFeedEnvelope, latestFeedEnvelope)

        assertThat(difference.user).isEqualTo(currentUser)
        assertThat(difference.source).isEqualTo(latestSource)
        with(difference.feed) {
            assertThat(id).isEqualTo("id2")
            assertThat(title).isEqualTo("title2")
            assertThat(feedItems.size).isEqualTo(1)
            assertThat(feedItems.items.last()).equals(latestFeedItem)
        }
    }

}
