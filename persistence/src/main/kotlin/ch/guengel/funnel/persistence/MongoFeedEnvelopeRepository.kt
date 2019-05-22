package ch.guengel.funnel.persistence

import ch.guengel.funnel.domain.FeedEnvelope
import com.mongodb.ConnectionString
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.UpdateOptions
import org.litote.kmongo.*

class MongoFeedEnvelopeRepository(connection: String, databaseName: String) :
    FeedEnvelopeRepository {
    private val client = KMongo.createClient(ConnectionString(connection))
    private val database = client.getDatabase(databaseName)
    private val collection: MongoCollection<FeedEnvelope> = database.getCollection<FeedEnvelope>()

    init {
        collection
                .createIndex("{ name: 1  }", IndexOptions().unique(true))
    }

    override fun retrieveAll(): List<FeedEnvelope> {
        return collection.find().distinct()
    }

    override fun retrieveByName(name: String): FeedEnvelope? {
        return collection.findOne(FeedEnvelope::name eq name)
    }

    override fun save(feedEnvelope: FeedEnvelope) {
        collection.updateOne(FeedEnvelope::name eq feedEnvelope.name, feedEnvelope, UpdateOptions().upsert(true))
    }

    override fun deleteByName(name: String) {
        collection.deleteOne(FeedEnvelope::name eq name)
    }

    override fun getAllFeedNames(): List<String> {
        return collection
                .find()
                .map { document -> document.name }
                .toList()
    }
}