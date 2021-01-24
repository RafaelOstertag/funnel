package ch.guengel.funnel.persistence

import assertk.assertThat
import assertk.assertions.*
import ch.guengel.funnel.feed.bridges.FeedEnvelopeNotFoundException
import ch.guengel.funnel.feed.bridges.FeedEnvelopePersistence
import ch.guengel.funnel.feed.logic.FeedEnvelopeMerger
import ch.guengel.funnel.testutils.LocalMongoDB
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledOnOs
import org.junit.jupiter.api.condition.OS

@DisabledOnOs(OS.OTHER)
class MongoFeedEnvelopePersistenceIT {
    private var localMongoDB: LocalMongoDB? = null

    private var feedEnvelopeRepository: FeedEnvelopePersistence? = null

    @BeforeEach
    fun setUp() {
        localMongoDB = LocalMongoDB()
        localMongoDB?.start()
        feedEnvelopeRepository = MongoFeedEnvelopePersistence("mongodb://localhost:${localMongoDB?.mongoPort}", "test")
    }

    @AfterEach
    fun tearDown() {
        localMongoDB?.stop()
    }

    @Test
    fun `retrieveAll on empty database`() {
        assertThat(feedEnvelopeRepository?.findAllFeedEnvelopes()).isNullOrEmpty()
    }

    @Test
    fun `retrieveAll for user on empty database`() {
        assertThat(feedEnvelopeRepository?.findAllFeedEnvelopesForUser("wdc")).isNullOrEmpty()
    }

    @Test
    fun `retrieve feed by user id and feed envelope name`() {
        val feedEnvelope = makeFeedEnvelope(
            "user1",
            makeSource(1),
            makeFeed("testid", "test title", 9)
        )
        feedEnvelopeRepository?.saveFeedEnvelope(feedEnvelope)
        val feedEnvelopeOtherUser = makeFeedEnvelope(
            "user2",
            makeSource(2),
            makeFeed("testid", "test title", 1)
        )
        feedEnvelopeRepository?.saveFeedEnvelope(feedEnvelopeOtherUser)

        val actualFeedEnvelope1 = feedEnvelopeRepository?.findFeedEnvelope("user1", "sourceName 1")
        assertThat(actualFeedEnvelope1).isNotNull()

        assertThat(actualFeedEnvelope1).isEqualTo(feedEnvelope)

        val actualFeedEnvelope2 = feedEnvelopeRepository?.findFeedEnvelope("user2", "sourceName 2")
        assertThat(actualFeedEnvelope2).isNotNull()

        assertThat(actualFeedEnvelope2).isEqualTo(feedEnvelopeOtherUser)
    }

    @Test
    fun `retrieve non-existing feed`() {
        assertThat { feedEnvelopeRepository?.findFeedEnvelope("wdc", "should not exist") }.isFailure()
            .isInstanceOf(FeedEnvelopeNotFoundException::class)
    }

    @Test
    fun `update feed`() {
        val feedEnvelope1 = makeFeedEnvelope(
            "user1",
            makeSource(1),
            makeFeed("testid", "test title", 2)
        )

        feedEnvelopeRepository?.saveFeedEnvelope(feedEnvelope1)
        var actualFeedEnvelope = feedEnvelopeRepository?.findFeedEnvelope("user1", "sourceName 1")
        assertThat(actualFeedEnvelope).isNotNull()

        val feedEnvelope2 = makeFeedEnvelope(
            "user1",
            makeSource(1),
            makeFeed("testid", "test title", 4)
        )

        val updatedFeed = FeedEnvelopeMerger().merge(feedEnvelope1, feedEnvelope2)

        feedEnvelopeRepository?.saveFeedEnvelope(updatedFeed)
        actualFeedEnvelope = feedEnvelopeRepository?.findFeedEnvelope("user1", "sourceName 1")
        assertThat(actualFeedEnvelope?.feed?.feedItems?.size).isEqualTo(4)
    }

    @Test
    fun `retrieve all`() {
        val feedEnvelope1 = makeFeedEnvelope(
            "user1",
            makeSource(1),
            makeFeed("testid", "test title", 4)
        )
        feedEnvelopeRepository?.saveFeedEnvelope(feedEnvelope1)

        val feedEnvelope2 = makeFeedEnvelope(
            "user2",
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
            "user4",
            makeSource(1),
            makeFeed("testid", "test title", 4)
        )
        feedEnvelopeRepository?.saveFeedEnvelope(controlFeedEnvelope)

        val storedFeedEnvelope = feedEnvelopeRepository?.findFeedEnvelope("user4", controlFeedEnvelope.name)
        assertThat(storedFeedEnvelope?.feed?.feedItems?.size).isEqualTo(4)

        val result = feedEnvelopeRepository?.deleteFeedEnvelope(storedFeedEnvelope!!)
        assertThat(result!!).isTrue()

        assertThat { feedEnvelopeRepository?.findFeedEnvelope("user4", controlFeedEnvelope.name) }.isFailure()
            .isInstanceOf(FeedEnvelopeNotFoundException::class)
    }

    @Test
    fun `delete non-existing feed envelope`() {
        val controlFeedEnvelope = makeFeedEnvelope(
            "user1",
            makeSource(1),
            makeFeed("testid", "test title", 4)
        )
        val result = feedEnvelopeRepository?.deleteFeedEnvelope(controlFeedEnvelope)
        assertThat(result!!).isFalse()
    }
}