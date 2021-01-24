package ch.guengel.funnel.feed.logic

import assertk.assertThat
import assertk.assertions.isEqualTo
import ch.guengel.funnel.feed.data.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyAll
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime

internal class FeedEnvelopeUpdateNotifierTest {


    @Test
    fun `no difference`() {
        // The different sources are used to distinguish between the feed envelopes. In production the envelopes should
        // differ in the feed items, but not the sources
        val source1 = Source("source1", "test")
        val source2 = Source("source2", "test")
        val user = User("userId", "email1")
        val feedEnvelopeCurrent = FeedEnvelope(user, source1, Feed())
        val feedEnvelopeLatest = FeedEnvelope(user, source2, Feed())

        val feedEnvelopeDifferenceMock = mockk<FeedEnvelopeDifference>()
        every {
            feedEnvelopeDifferenceMock.difference(any(), any())
        }.returns(FeedEnvelope(User("userId", "email"), source1, Feed("test", "test", FeedItems())))

        val feedEnvelopeUpdateNotifier = FeedEnvelopeUpdateNotifier(feedEnvelopeDifferenceMock) {
            fail("There should be no difference to notify of")
        }

        feedEnvelopeUpdateNotifier.notify(feedEnvelopeCurrent, feedEnvelopeLatest)

        verifyAll {
            feedEnvelopeDifferenceMock.difference(feedEnvelopeCurrent, feedEnvelopeLatest)
        }
    }

    @Test
    fun difference() {
        // The different sources are used to distinguish between the feed envelopes. In production the envelopes should
        // differ in the feed items, but not the sources
        val source1 = Source("source1", "test")
        val source2 = Source("source2", "test")
        val user = User("userId", "email")
        val feedEnvelopeCurrent = FeedEnvelope(user, source1, Feed())
        val feedEnvelopeLatest = FeedEnvelope(user, source2, Feed())

        val feedEnvelopeDifferenceMock = mockk<FeedEnvelopeDifference>()

        val feedItem = FeedItem("id", "title", "link", OffsetDateTime.now())
        val expectedFeedEnvelope =
            FeedEnvelope(User("userId", "email"), source1, Feed("id", "title", FeedItems(listOf(feedItem))))
        every {
            feedEnvelopeDifferenceMock.difference(any(), any())
        }.returns(expectedFeedEnvelope)

        val feedEnvelopeUpdateNotifier = FeedEnvelopeUpdateNotifier(feedEnvelopeDifferenceMock) {
            assertThat(it).isEqualTo(expectedFeedEnvelope)
        }

        feedEnvelopeUpdateNotifier.notify(feedEnvelopeCurrent, feedEnvelopeLatest)

        verifyAll {
            feedEnvelopeDifferenceMock.difference(feedEnvelopeCurrent, feedEnvelopeLatest)
        }
    }
}