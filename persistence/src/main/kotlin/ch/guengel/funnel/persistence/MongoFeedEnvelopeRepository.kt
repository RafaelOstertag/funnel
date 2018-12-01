package ch.guengel.funnel.persistence

import ch.guengel.funnel.domain.FeedEnvelope
import com.mongodb.ConnectionString
import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.UpdateOptions
import org.litote.kmongo.*

class MongoFeedEnvelopeRepository(connection: String, databaseName: String) :
    FeedEnvelopeRepository {
    private val client = KMongo.createClient(ConnectionString(connection))
    private val database = client.getDatabase(databaseName)

    init {
        database
                .getCollection<FeedEnvelope>()
                .createIndex("{ name: 1  }", IndexOptions().unique(true))
    }

    override fun retrieveAll(): List<FeedEnvelope> {
        val collection = database.getCollection<FeedEnvelope>()
        return collection.find().distinct()
    }

    override fun retrieveById(name: String): FeedEnvelope {
        val collection = database.getCollection<FeedEnvelope>()
        return collection.findOne(FeedEnvelope::name eq name)
                ?: throw FeedEnvelopeNotFound("Feed Envelope '$name' not found")
    }

    override fun save(feedEnvelope: FeedEnvelope) {
        val collection = database.getCollection<FeedEnvelope>()

        collection.updateOne(FeedEnvelope::name eq feedEnvelope.name, feedEnvelope, UpdateOptions().upsert(true))
    }
}