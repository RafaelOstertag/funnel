package ch.guengel.funnel.feed.logic

import assertk.assertThat
import assertk.assertions.isEqualTo
import ch.guengel.funnel.feed.data.*
import org.junit.jupiter.api.Test

internal class FeedEnvelopeTrimmerTest {

    @Test
    fun `trim feed envelope to one item`() {
        val feedItems = FeedItems(makeFeedItems(3))
        val feed = Feed("id", "title", feedItems)
        val source = Source("name", "address")
        val feedEnvelope = FeedEnvelope(source, feed)

        val feedEnvelopeTrimmer = FeedEnvelopeTrimmer(1)
        val trimmedFeed = feedEnvelopeTrimmer.trim(feedEnvelope)


        with(trimmedFeed.source) {
            assertThat(address).isEqualTo("address")
            assertThat(name).isEqualTo("name")
        }
        with(trimmedFeed.feed) {
            assertThat(id).isEqualTo("id")
            assertThat(title).isEqualTo("title")
        }
        with(trimmedFeed.feed.feedItems) {
            assertThat(size).isEqualTo(1)
            assertThat(items.last()).isEqualTo(FeedItem("3", "title 3", "link 3", now))
        }
    }

    @Test
    fun `trim feed envelope`() {
        val feedItems = FeedItems(makeFeedItems(3))
        val feed = Feed("id", "title", feedItems)
        val source = Source("name", "address")
        val feedEnvelope = FeedEnvelope(source, feed)

        val feedEnvelopeTrimmer = FeedEnvelopeTrimmer(2)
        val trimmedFeed = feedEnvelopeTrimmer.trim(feedEnvelope)


        with(trimmedFeed.source) {
            assertThat(address).isEqualTo("address")
            assertThat(name).isEqualTo("name")
        }
        with(trimmedFeed.feed) {
            assertThat(id).isEqualTo("id")
            assertThat(title).isEqualTo("title")
        }
        with(trimmedFeed.feed.feedItems) {
            assertThat(size).isEqualTo(2)
            assertThat(items.first()).isEqualTo(FeedItem("2", "title 2", "link 2", now.minusDays(1)))
            assertThat(items.last()).isEqualTo(FeedItem("3", "title 3", "link 3", now))
        }
    }

    @Test
    fun `no trimming necessary`() {
        val feedItems = FeedItems(makeFeedItems(3))
        val feed = Feed("id", "title", feedItems)
        val source = Source("name", "address")
        val feedEnvelope = FeedEnvelope(source, feed)

        val feedEnvelopeTrimmer = FeedEnvelopeTrimmer(4)
        val trimmedFeed = feedEnvelopeTrimmer.trim(feedEnvelope)

        with(trimmedFeed.source) {
            assertThat(address).isEqualTo("address")
            assertThat(name).isEqualTo("name")
        }
        with(trimmedFeed.feed) {
            assertThat(id).isEqualTo("id")
            assertThat(title).isEqualTo("title")
        }
        with(trimmedFeed.feed.feedItems) {
            assertThat(size).isEqualTo(3)
            assertThat(items.last()).isEqualTo(FeedItem("3", "title 3", "link 3", now))
        }
    }
}