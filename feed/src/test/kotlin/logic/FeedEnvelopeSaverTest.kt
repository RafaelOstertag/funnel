package logic

import adapters.FeedPersistence
import data.Feed
import data.FeedEnvelope
import data.Source
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifySequence
import org.junit.jupiter.api.Test

internal class FeedEnvelopeSaverTest {

    @Test
    fun save() {
        val feedEnvelope = FeedEnvelope(Source("source", "address"), Feed())
        val persistenceMock = mockk<FeedPersistence>()
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