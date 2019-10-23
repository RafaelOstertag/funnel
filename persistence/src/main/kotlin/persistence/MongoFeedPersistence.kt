package persistence

import adapters.FeedPersistence
import com.mongodb.ConnectionString
import com.mongodb.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.UpdateOptions
import data.FeedEnvelope
import jackson.jacksonFeedItemsModule
import org.litote.kmongo.*
import org.litote.kmongo.util.KMongoConfiguration
import org.slf4j.LoggerFactory
import java.io.Closeable

class MongoFeedPersistence(connectionString: String, databaseName: String) : FeedPersistence, Closeable {
    private val client: MongoClient = KMongo.createClient(ConnectionString(connectionString))
    private val collection: MongoCollection<FeedEnvelope>

    init {
        val database = client.getDatabase(databaseName)
        collection = database.getCollection()
        collection.createIndex("{ name: 1 }", IndexOptions().unique(true))
        logger.debug("Initialized Mongo Feed Envelope Repository")
    }

    override fun close() {
        client.close()
        logger.debug("Closed Mongo Feed Envelope Repository")
    }

    override fun findFeedEnvelope(name: String): FeedEnvelope {
        logger.debug("Retrieve feed envelope '{}'", name)
        return collection.findOne(FeedEnvelope::name eq name) ?: throw FeedNotFoundException("Feed '${name}' not found")
    }

    override fun findAllFeedEnvelopes(): List<FeedEnvelope> {
        return collection.find().distinct()
    }

    override fun saveFeedEnvelope(feedEnvelope: FeedEnvelope) {
        logger.debug("Save feed envelope '{}'", feedEnvelope.name)
        collection.updateOne(FeedEnvelope::name eq feedEnvelope.name, feedEnvelope, UpdateOptions().upsert(true))
    }

    companion object {
        val logger = LoggerFactory.getLogger(MongoFeedPersistence::class.java)

        init {
            KMongoConfiguration.bsonMapper.registerModule(jacksonFeedItemsModule())
        }
    }
}