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
        val mergerMock = mockk<FeedEnvelopeMerger>()

        val feedEnvelopeSaver = FeedEnvelopeSaver(persistenceMock, trimmerMock, mergerMock)

        feedEnvelopeSaver.save(feedEnvelope)

        verifySequence {
            trimmerMock.trim(feedEnvelope)
            persistenceMock.saveFeedEnvelope(feedEnvelope)
        }
    }

    @Test
    fun `merge and save`() {
        val feedEnvelope1 = FeedEnvelope(Source("source1", "address1"), Feed())
        val feedEnvelope2 = FeedEnvelope(Source("source2", "address2"), Feed())
        val persistenceMock = mockk<FeedPersistence>()
        every { persistenceMock.saveFeedEnvelope(any()) }.returns(Unit)
        val trimmerMock = mockk<FeedEnvelopeTrimmer>()
        every { trimmerMock.trim(any()) }.returns(feedEnvelope2)
        val mergerMock = mockk<FeedEnvelopeMerger>()
        every { mergerMock.merge(any(), any()) }.returns(feedEnvelope2)

        val feedEnvelopeSaver = FeedEnvelopeSaver(persistenceMock, trimmerMock, mergerMock)

        feedEnvelopeSaver.mergeAndSave(feedEnvelope1, feedEnvelope2)

        verifySequence {
            mergerMock.merge(feedEnvelope1, feedEnvelope2)
            trimmerMock.trim(feedEnvelope2)
            persistenceMock.saveFeedEnvelope(feedEnvelope2)
        }
    }
}