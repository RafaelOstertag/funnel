package ch.guengel.funnel.feed.logic

import assertk.assertThat
import assertk.assertions.isEqualTo
import ch.guengel.funnel.feed.data.*
import org.junit.jupiter.api.Test

internal class FeedEnvelopeMergerTest {
    private val feedEnvelopeMerger = FeedEnvelopeMerger()

    @Test
    fun `self merge`() {
        val feedItems1 = makeFeedItems(3)
        val source1 = Source("name1", "address1")
        val feed1 = Feed("id1", "title1", FeedItems(feedItems1))
        val user1 = User("userId1", "email1")
        val feedEnvelope1 = FeedEnvelope(user1, source1, feed1)

        val mergedFeedEnvelope = feedEnvelopeMerger.merge(feedEnvelope1, feedEnvelope1)

        assertThat(mergedFeedEnvelope.source).isEqualTo(source1)
        assertThat(mergedFeedEnvelope.user).isEqualTo(user1)
        with(mergedFeedEnvelope.feed) {
            assertThat(id).isEqualTo("id1")
            assertThat(title).isEqualTo("title1")
            assertThat(feedItems.size).isEqualTo(3)
        }
    }

    @Test
    fun merge() {
        val feedItems1 = makeFeedItems(3)
        val feed1 = Feed("id1", "title1", FeedItems(feedItems1))
        val source1 = Source("name1", "address1")
        val user1 = User("userId1", "email1")
        val feedEnvelope1 = FeedEnvelope(user1, source1, feed1)

        val feedItem2 = FeedItem("latest", "title", "link", now.plusDays(1))
        val feedItems2 = FeedItems(listOf(feedItem2))
        val feed2 = Feed("id2", "title2", feedItems2)
        val source2 = Source("name2", "address2")
        val user2 = User("userId2", "email2")
        val feedEnvelope2 = FeedEnvelope(user2, source2, feed2)

        val mergedFeedEnvelope = feedEnvelopeMerger.merge(feedEnvelope1, feedEnvelope2)

        assertThat(mergedFeedEnvelope.user).isEqualTo(user1)
        assertThat(mergedFeedEnvelope.source).isEqualTo(source2)
        with(mergedFeedEnvelope.feed) {
            assertThat(id).isEqualTo("id2")
            assertThat(title).isEqualTo("title2")
            assertThat(feedItems.size).isEqualTo(4)
            assertThat(feedItems.items.last()).equals(feedItem2)
        }
    }
}