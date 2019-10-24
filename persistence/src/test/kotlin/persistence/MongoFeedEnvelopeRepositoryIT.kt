package persistence

import assertk.assertThat
import assertk.assertions.*
import bridges.FeedNotFoundException
import bridges.FeedPersistence
import ch.guengel.funnel.testutils.EmbeddedMongo
import logic.FeedEnvelopeMerger
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledOnOs
import org.junit.jupiter.api.condition.OS

@DisabledOnOs(OS.OTHER)
class MongoFeedEnvelopeRepositoryIT {
    private var embeddedMongo: EmbeddedMongo? = null

    private var feedEnvelopeRepository: FeedPersistence? = null

    @BeforeEach
    fun setUp() {
        embeddedMongo = EmbeddedMongo()
        embeddedMongo?.start()
        feedEnvelopeRepository = MongoFeedPersistence("mongodb://localhost:${embeddedMongo?.mongoPort}", "test")
    }

    @AfterEach
    fun tearDown() {
        embeddedMongo?.stop()
    }

    @Test
    fun `retrieveAll on empty database`() {
        assertThat(feedEnvelopeRepository?.findAllFeedEnvelopes()).isNullOrEmpty()
    }

    @Test
    fun `retrieve feed by feed envelope name`() {
        val feedEnvelope = makeFeedEnvelope(
            makeSource(1),
            makeFeed("testid", "test title", 9)
        )
        feedEnvelopeRepository?.saveFeedEnvelope(feedEnvelope)

        val actualFeedEnvelope = feedEnvelopeRepository?.findFeedEnvelope("sourceName 1")
        assertThat(actualFeedEnvelope).isNotNull()

        assertThat(actualFeedEnvelope).isEqualTo(feedEnvelope)
    }

    @Test
    fun `retrieve non-existing feed`() {
        assertThat { feedEnvelopeRepository?.findFeedEnvelope("should not exist") }.isFailure()
            .isInstanceOf(FeedNotFoundException::class)
    }

    @Test
    fun `update feed`() {
        val feedEnvelope1 = makeFeedEnvelope(
            makeSource(1),
            makeFeed("testid", "test title", 2)
        )

        feedEnvelopeRepository?.saveFeedEnvelope(feedEnvelope1)
        var actualFeedEnvelope = feedEnvelopeRepository?.findFeedEnvelope("sourceName 1")
        assertThat(actualFeedEnvelope).isNotNull()

        val feedEnvelope2 = makeFeedEnvelope(
            makeSource(1),
            makeFeed("testid", "test title", 4)
        )

        val updatedFeed = FeedEnvelopeMerger().merge(feedEnvelope1, feedEnvelope2)

        feedEnvelopeRepository?.saveFeedEnvelope(updatedFeed)
        actualFeedEnvelope = feedEnvelopeRepository?.findFeedEnvelope("sourceName 1")
        assertThat(actualFeedEnvelope?.feed?.feedItems?.size).isEqualTo(4)
    }

    @Test
    fun `retrieve all`() {
        val feedEnvelope1 = makeFeedEnvelope(
            makeSource(1),
            makeFeed("testid", "test title", 4)
        )
        feedEnvelopeRepository?.saveFeedEnvelope(feedEnvelope1)

        val feedEnvelope2 = makeFeedEnvelope(
            makeSource(2),
            makeFeed("testid 2", "test title", 5)
        )
        feedEnvelopeRepository?.saveFeedEnvelope(feedEnvelope2)

        val allEnvelopes = feedEnvelopeRepository?.findAllFeedEnvelopes()

        assertThat(allEnvelopes?.size).isEqualTo(2)
    }

    @Test
    fun `delete feed envelope`() {
        val controlFeedEnvelope = makeFeedEnvelope(
            makeSource(1),
            makeFeed("testid", "test title", 4)
        )
        feedEnvelopeRepository?.saveFeedEnvelope(controlFeedEnvelope)

        val storedFeedEnvelope = feedEnvelopeRepository?.findFeedEnvelope(controlFeedEnvelope.name)
        assertThat(storedFeedEnvelope?.feed?.feedItems?.size).isEqualTo(4)


        feedEnvelopeRepository?.deleteFeedEnvelope(storedFeedEnvelope!!)

        assertThat { feedEnvelopeRepository?.findFeedEnvelope(controlFeedEnvelope.name) }.isFailure()
            .isInstanceOf(FeedNotFoundException::class)
    }

    @Test
    fun `delete non-existing feed envelope`() {
        val controlFeedEnvelope = makeFeedEnvelope(
            makeSource(1),
            makeFeed("testid", "test title", 4)
        )
        feedEnvelopeRepository?.deleteFeedEnvelope(controlFeedEnvelope)
        // Not throwing an exception is the test
    }
}