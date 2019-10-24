package ch.guengel.funnel.feed.logic

import assertk.assertThat
import assertk.assertions.isEqualTo
import ch.guengel.funnel.feed.bridges.FeedRetriever
import ch.guengel.funnel.feed.data.Feed
import ch.guengel.funnel.feed.data.FeedItem
import ch.guengel.funnel.feed.data.FeedItems
import ch.guengel.funnel.feed.data.Source
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

internal class FeedEnvelopeRetrieverTest {

    @Test
    fun retrieve() = runBlocking {
        val feed = Feed(
            "id",
            "title",
            FeedItems(listOf(FeedItem("1", "", now)))
        )
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