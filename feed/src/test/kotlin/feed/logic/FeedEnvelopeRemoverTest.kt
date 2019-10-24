package ch.guengel.funnel.feed.logic

import ch.guengel.funnel.feed.bridges.FeedEnvelopePersistence
import ch.guengel.funnel.feed.data.Feed
import ch.guengel.funnel.feed.data.FeedEnvelope
import ch.guengel.funnel.feed.data.Source
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyAll
import org.junit.jupiter.api.Test

internal class FeedEnvelopeRemoverTest {

    @Test
    fun `remove by name`() {
        val feedEnvelopePersistenceMock = mockk<FeedEnvelopePersistence>()
        every {
            feedEnvelopePersistenceMock.deleteFeedEnvelope(any())
        }.returns(Unit)

        FeedEnvelopeRemover(feedEnvelopePersistenceMock).remove("name")

        val expectedFeedEnvelope = FeedEnvelope(Source("name", "should be ignored"), Feed())
        verifyAll {
            feedEnvelopePersistenceMock.deleteFeedEnvelope(expectedFeedEnvelope)
        }
    }

    @Test
    fun `remove by FeedEnvelope`() {
        val feedEnvelopePersistenceMock = mockk<FeedEnvelopePersistence>()
        every {
            feedEnvelopePersistenceMock.deleteFeedEnvelope(any())
        }.returns(Unit)

        val feedEnvelope = FeedEnvelope(Source("name", "address"), Feed())
        FeedEnvelopeRemover(feedEnvelopePersistenceMock).remove(feedEnvelope)


        verifyAll {
            feedEnvelopePersistenceMock.deleteFeedEnvelope(feedEnvelope)
        }
    }
}