package ch.guengel.funnel.persistence

import ch.guengel.funnel.domain.FeedEnvelope
import com.mongodb.ConnectionString
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.UpdateOptions
import org.litote.kmongo.*
import org.slf4j.LoggerFactory
import java.io.Closeable

class MongoFeedEnvelopeRepository(connection: String, databaseName: String) :
        FeedEnvelopeRepository, Closeable {
    private val client = KMongo.createClient(ConnectionString(connection))

    private val database = client.getDatabase(databaseName)
    private val collection: MongoCollection<FeedEnvelope> = database.getCollection<FeedEnvelope>()
    init {
        collection
                .createIndex("{ name: 1  }", IndexOptions().unique(true))
        logger.debug("Initialized Mongo Feed Envelope Repository")
    }

    override fun retrieveAll(): List<FeedEnvelope> {
        return collection.find().distinct()
    }

    override fun retrieveByName(name: String): FeedEnvelope? {
        logger.debug("Retrieve feed envelope '{}'", name)
        return collection.findOne(FeedEnvelope::name eq name)
    }

    override fun save(feedEnvelope: FeedEnvelope) {
        logger.debug("Save feed envelope '{}'", feedEnvelope.name)
        collection.updateOne(FeedEnvelope::name eq feedEnvelope.name, feedEnvelope, UpdateOptions().upsert(true))
    }

    override fun deleteByName(name: String) {
        logger.debug("Delete feed envelope '{}'", name)
        collection.deleteOne(FeedEnvelope::name eq name)
    }

    override fun getAllFeedNames(): List<String> {
        return collection
                .find()
                .map { document -> document.name }
                .toList()
    }

    override fun close() {
        client.close()
        logger.debug("Closed Mongo Feed Envelope Repository")
    }

    companion object {
        val logger = LoggerFactory.getLogger(MongoFeedEnvelopeRepository::class.java)
    }
}