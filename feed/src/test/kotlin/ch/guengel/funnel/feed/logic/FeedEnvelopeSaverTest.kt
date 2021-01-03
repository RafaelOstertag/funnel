package ch.guengel.funnel.feed.logic

import ch.guengel.funnel.feed.bridges.FeedEnvelopePersistence
import ch.guengel.funnel.feed.data.Feed
import ch.guengel.funnel.feed.data.FeedEnvelope
import ch.guengel.funnel.feed.data.Source
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifySequence
import org.junit.jupiter.api.Test

internal class FeedEnvelopeSaverTest {

    @Test
    fun save() {
        val feedEnvelope =
            FeedEnvelope(Source("source", "address"), Feed())
        val persistenceMock = mockk<FeedEnvelopePersistence>()
        every { persistenceMock.saveFeedEnvelope(any()) }.returns(Unit)
        val trimmerMock = mockk<FeedEnvelopeTrimmer>()
        every { trimmerMock.trim(any()) }.returns(feedEnvelope)

        val feedEnvelopeSaver = FeedEnvelopeSaver(persistenceMock, trimmerMock)

        feedEnvelopeSaver.save(feedEnvelope)

        verifySequence {
            trimmerMock.trim(feedEnvelope)
            persistenceMock.saveFeedEnvelope(feedEnvelope)
        }
    }
}