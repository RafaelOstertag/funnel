package logic

import assertk.assertThat
import assertk.assertions.isEqualTo
import bridges.FeedRetriever
import data.Feed
import data.FeedItem
import data.FeedItems
import data.Source
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

internal class FeedEnvelopeRetrieverTest {

    @Test
    fun retrieve() = runBlocking {
        val feed = Feed("id", "title", FeedItems(listOf(FeedItem("1", "", now))))
        val feedRetrieverMock = mockk<FeedRetriever>()
        coEvery {
            feedRetrieverMock.fetch(any())
        }.returns(feed)
        val feedEnvelopeRetriever = FeedEnvelopeRetriever(feedRetrieverMock)

        val source = Source("name", "address")
        val feedEnvelope = feedEnvelopeRetriever.retrieve(source)

        assertThat(feedEnvelope.source).isEqualTo(source)
        assertThat(feedEnvelope.feed).isEqualTo(feed)
    }
}