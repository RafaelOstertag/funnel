package ch.guengel.funnel.feed.logic

import assertk.assertThat
import assertk.assertions.isTrue
import ch.guengel.funnel.feed.bridges.FeedEnvelopePersistence
import ch.guengel.funnel.feed.data.Feed
import ch.guengel.funnel.feed.data.FeedEnvelope
import ch.guengel.funnel.feed.data.Source
import ch.guengel.funnel.feed.data.User
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
        }.returns(true)

        val result = FeedEnvelopeRemover(feedEnvelopePersistenceMock).remove("user-id", "name")
        assertThat(result).isTrue()

        val expectedFeedEnvelope = FeedEnvelope(User("user-id", "- n/a -"), Source("name", "- n/a -"), Feed())
        verifyAll {
            feedEnvelopePersistenceMock.deleteFeedEnvelope(expectedFeedEnvelope)
        }
    }

    @Test
    fun `remove by FeedEnvelope`() {
        val feedEnvelopePersistenceMock = mockk<FeedEnvelopePersistence>()
        every {
            feedEnvelopePersistenceMock.deleteFeedEnvelope(any())
        }.returns(true)

        val feedEnvelope = FeedEnvelope(User("user-id", "- n/a -"), Source("name", "address"), Feed())
        val result = FeedEnvelopeRemover(feedEnvelopePersistenceMock).remove(feedEnvelope)
        assertThat(result).isTrue()

        verifyAll {
            feedEnvelopePersistenceMock.deleteFeedEnvelope(feedEnvelope)
        }
    }
}