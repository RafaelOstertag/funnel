package ch.guengel.funnel.persistence

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isNullOrEmpty
import assertk.assertions.isTrue
import ch.guengel.funnel.feed.logic.FeedEnvelopeMerger
import ch.guengel.funnel.testutils.makeFeed
import ch.guengel.funnel.testutils.makeFeedEnvelope
import ch.guengel.funnel.testutils.makeSource
import io.quarkus.mongodb.reactive.ReactiveMongoClient
import io.quarkus.test.junit.QuarkusTest
import org.bson.Document
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

@QuarkusTest
class MongoFeedEnvelopePersistenceIT {
    @Inject
    private lateinit var mongoFeedEnvelopePersistence: MongoFeedEnvelopePersistence

    @Inject
    private lateinit var mongoClient: ReactiveMongoClient

    @ConfigProperty(name = "funnel.mongodb.database")
    private lateinit var database: String

    @ConfigProperty(name = "funnel.mongodb.collection")
    private lateinit var collectionName: String

    @BeforeEach
    fun beforeEach() {
        deletaAllDocuments()
    }

    @AfterEach
    fun afterEach() {
        deletaAllDocuments()
    }

    private fun deletaAllDocuments() {
        mongoClient.getDatabase(database).getCollection(collectionName).deleteMany(Document()).await().indefinitely()
    }

    @Test
    fun `retrieveAll on empty database`() {
        assertThat(mongoFeedEnvelopePersistence.findAllFeedEnvelopes()).isNullOrEmpty()
    }

    @Test
    fun `retrieveAll for user on empty database`() {
        assertThat(mongoFeedEnvelopePersistence.findAllFeedEnvelopesForUser("wdc")).isNullOrEmpty()
    }

    @Test
    fun `retrieve feed by user id and feed envelope name`() {
        val feedEnvelope = makeFeedEnvelope(
            "user1",
            makeSource(1),
            makeFeed("testid", "test title", 9)
        )
        mongoFeedEnvelopePersistence.saveFeedEnvelope(feedEnvelope)
        val feedEnvelopeOtherUser = makeFeedEnvelope(
            "user2",
            makeSource(2),
            makeFeed("testid", "test title", 1)
        )
        mongoFeedEnvelopePersistence.saveFeedEnvelope(feedEnvelopeOtherUser)

        val actualFeedEnvelope1 = mongoFeedEnvelopePersistence.findFeedEnvelope("user1", "sourceName 1")
        assertThat(actualFeedEnvelope1).isNotNull()

        assertThat(actualFeedEnvelope1).isEqualTo(feedEnvelope)

        val actualFeedEnvelope2 = mongoFeedEnvelopePersistence.findFeedEnvelope("user2", "sourceName 2")
        assertThat(actualFeedEnvelope2).isNotNull()

        assertThat(actualFeedEnvelope2).isEqualTo(feedEnvelopeOtherUser)
    }

    @Test
    fun `retrieve non-existing feed`() {
        val feedEnvelope = mongoFeedEnvelopePersistence.findFeedEnvelope("wdc", "should not exist")
        assertThat(feedEnvelope).isNull()
    }

    @Test
    fun `update feed`() {
        val feedEnvelope1 = makeFeedEnvelope(
            "user1",
            makeSource(1),
            makeFeed("testid", "test title", 2)
        )

        mongoFeedEnvelopePersistence.saveFeedEnvelope(feedEnvelope1)
        var actualFeedEnvelope = mongoFeedEnvelopePersistence.findFeedEnvelope("user1", "sourceName 1")
        assertThat(actualFeedEnvelope).isNotNull()

        val feedEnvelope2 = makeFeedEnvelope(
            "user1",
            makeSource(1),
            makeFeed("testid", "test title", 4)
        )

        val updatedFeed = FeedEnvelopeMerger().merge(feedEnvelope1, feedEnvelope2)

        mongoFeedEnvelopePersistence.saveFeedEnvelope(updatedFeed)
        actualFeedEnvelope = mongoFeedEnvelopePersistence.findFeedEnvelope("user1", "sourceName 1")
        assertThat(actualFeedEnvelope).isNotNull()
        assertThat(actualFeedEnvelope!!.feed.feedItems.size).isEqualTo(4)
    }

    @Test
    fun `retrieve all`() {
        val feedEnvelope1 = makeFeedEnvelope(
            "user1",
            makeSource(1),
            makeFeed("testid", "test title", 4)
        )
        mongoFeedEnvelopePersistence.saveFeedEnvelope(feedEnvelope1)

        val feedEnvelope2 = makeFeedEnvelope(
            "user2",
            makeSource(2),
            makeFeed("testid 2", "test title", 5)
        )
        mongoFeedEnvelopePersistence.saveFeedEnvelope(feedEnvelope2)

        val allEnvelopes = mongoFeedEnvelopePersistence.findAllFeedEnvelopes()

        assertThat(allEnvelopes.size).isEqualTo(2)
    }

    @Test
    fun `delete feed envelope`() {
        val controlFeedEnvelope = makeFeedEnvelope(
            "user4",
            makeSource(1),
            makeFeed("testid", "test title", 4)
        )
        mongoFeedEnvelopePersistence.saveFeedEnvelope(controlFeedEnvelope)

        val storedFeedEnvelope = mongoFeedEnvelopePersistence.findFeedEnvelope("user4", controlFeedEnvelope.name)
        assertThat(storedFeedEnvelope).isNotNull()
        assertThat(storedFeedEnvelope!!.feed.feedItems.size).isEqualTo(4)

        val result = mongoFeedEnvelopePersistence.deleteFeedEnvelope(storedFeedEnvelope)
        assertThat(result).isTrue()

        val feedEnvelope = mongoFeedEnvelopePersistence.findFeedEnvelope("user4", controlFeedEnvelope.name)
        assertThat(feedEnvelope).isNull()
    }

    @Test
    fun `delete non-existing feed envelope`() {
        val nonExistingFeedEnvelope = makeFeedEnvelope(
            "user1",
            makeSource(1),
            makeFeed("testid", "test title", 4)
        )


        val result = mongoFeedEnvelopePersistence.deleteFeedEnvelope(nonExistingFeedEnvelope)
        assertThat(result).isFalse()
    }
}
