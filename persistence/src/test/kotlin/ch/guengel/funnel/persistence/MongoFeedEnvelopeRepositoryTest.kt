package ch.guengel.funnel.persistence

import assertk.assert
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNullOrEmpty
import ch.guengel.funnel.testutils.EmbeddedMongo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledOnOs
import org.junit.jupiter.api.condition.OS

@DisabledOnOs(OS.OTHER)
class MongoFeedEnvelopeRepositoryTest {
    private var embeddedMongo: EmbeddedMongo? = null

    private var feedEnvelopeRepository: FeedEnvelopeRepository? = null

    @BeforeEach
    fun setUp() {
        embeddedMongo = EmbeddedMongo()
        embeddedMongo?.start()
        feedEnvelopeRepository = MongoFeedEnvelopeRepository("mongodb://localhost:${embeddedMongo?.mongoPort}", "test")
    }

    @AfterEach
    fun tearDown() {
        embeddedMongo?.stop()
    }

    @Test
    fun `retrieveAll on empty database`() {
        assert(feedEnvelopeRepository?.retrieveAll()).isNullOrEmpty()
    }

    @Test
    fun `retrieve feed by id`() {
        val feedEnvelope = makeFeedEnvelope(makeSource(1), makeFeed("testid", "test title", 9))
        feedEnvelopeRepository?.save(feedEnvelope)

        val actualFeedEnvelope = feedEnvelopeRepository?.retrieveById("sourceName 1")
        assert(actualFeedEnvelope).isNotNull()

        assert(actualFeedEnvelope).isEqualTo(feedEnvelope)
    }

    @Test
    fun `keep latest maintained`() {
        val feedEnvelope = makeFeedEnvelope(makeSource(1), makeFeed("testid", "test title", 9))
        feedEnvelopeRepository?.save(feedEnvelope)

        val actualFeedEnvelope = feedEnvelopeRepository?.retrieveById("sourceName 1")

        assert(actualFeedEnvelope?.lastUpdated).isEqualTo(actualFeedEnvelope?.feed?.lastUpdated)
        assert(actualFeedEnvelope?.feed?.feedItems?.latest).isEqualTo(feedEnvelope.feed.feedItems.latest)
    }

    @Test
    fun `update feed`() {
        val feedEnvelope = makeFeedEnvelope(makeSource(1), makeFeed("testid", "test title", 2))

        feedEnvelopeRepository?.save(feedEnvelope)
        var actualFeedEnvelope = feedEnvelopeRepository?.retrieveById("sourceName 1")
        assert(actualFeedEnvelope).isNotNull()

        val newFeedItem = makeItem(9)
        feedEnvelope.feed.feedItems.add(newFeedItem)

        feedEnvelopeRepository?.save(feedEnvelope)
        actualFeedEnvelope = feedEnvelopeRepository?.retrieveById("sourceName 1")
        assert(actualFeedEnvelope).isNotNull()
        assert(actualFeedEnvelope?.feed?.feedItems?.hasItem(newFeedItem)).isEqualTo(true)
    }

    @Test
    fun `retrieve all`() {
        val feedEnvelope1 = makeFeedEnvelope(makeSource(1), makeFeed("testid", "test title", 4))
        feedEnvelopeRepository?.save(feedEnvelope1)

        val feedEnvelope2 = makeFeedEnvelope(makeSource(2), makeFeed("testid 2", "test title", 5))
        feedEnvelopeRepository?.save(feedEnvelope2)

        val allEnvelopes = feedEnvelopeRepository?.retrieveAll()

        assert(allEnvelopes?.size).isEqualTo(2)

    }
}