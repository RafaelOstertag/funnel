package ch.guengel.funnel.persistence

import ch.guengel.funnel.feed.bridges.FeedEnvelopePersistence
import ch.guengel.funnel.feed.data.FeedEnvelope
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.Indexes
import com.mongodb.client.model.ReplaceOptions
import io.quarkus.mongodb.reactive.ReactiveMongoClient
import io.quarkus.mongodb.reactive.ReactiveMongoCollection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bson.BsonReader
import org.bson.BsonWriter
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext
import org.bson.codecs.pojo.PropertyCodecProvider
import org.bson.codecs.pojo.PropertyCodecRegistry
import org.bson.codecs.pojo.TypeWithTypeParameters
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jboss.logging.Logger
import java.time.OffsetDateTime
import javax.annotation.PostConstruct
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class MongoFeedEnvelopePersistence(
    @Inject private val mongoClient: ReactiveMongoClient,
    @ConfigProperty(name = "funnel.mongodb.database") private val database: String,
    @ConfigProperty(name = "funnel.mongodb.collection") private val collectionName: String,
) : FeedEnvelopePersistence {

    private lateinit var collection: ReactiveMongoCollection<MongoFeedEnvelope>

    @PostConstruct
    fun createIndex() {
        collection = mongoClient.getDatabase(database).getCollection(collectionName, MongoFeedEnvelope::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            collection.createIndex(Indexes.ascending(userIdField, feedNameField), IndexOptions().unique(true)).await()
                .indefinitely()
        }
    }

    override fun findFeedEnvelope(userId: String, name: String): FeedEnvelope? {
        logger.debugf("Retrieve feed envelope '%s' for '%s'", name, userId)
        return collection.find(and(eq(userIdField, userId), eq(feedNameField, name)))
            .toUni()
            .onItem().ifNotNull().transform { it.toFeedEnvelope() }
            .await().indefinitely()

    }

    override fun findAllFeedEnvelopes(): List<FeedEnvelope> {
        return collection.find().onItem().transform { it.toFeedEnvelope() }.collect().asList().await().indefinitely()
    }

    override fun findAllFeedEnvelopesForUser(userId: String): List<FeedEnvelope> {
        logger.debugf("Retrieve all feed envelopes for '%s'", userId)
        return collection.find(eq(userIdField, userId)).onItem().transform { it.toFeedEnvelope() }.collect().asList()
            .await().indefinitely()
    }

    override fun saveFeedEnvelope(feedEnvelope: FeedEnvelope) {
        logger.debugf("Save feed envelope '%s'", feedEnvelope.name)
        collection.replaceOne(
            and(eq(feedNameField, feedEnvelope.name), eq(userIdField, feedEnvelope.user.userId)),
            feedEnvelope.toMongoFeedEnvelope(),
            ReplaceOptions().upsert(true)
        ).await().indefinitely()
    }

    override fun deleteFeedEnvelope(feedEnvelope: FeedEnvelope): Boolean {
        logger.debugf("Delete feed envelope '%f'", feedEnvelope.name)
        return collection.deleteOne(
            and(
                eq(userIdField, feedEnvelope.user.userId),
                eq(feedNameField, feedEnvelope.name)
            )
        )
            .onItem().transform { it.deletedCount == 1L }
            .await().indefinitely()
    }

    private companion object {
        val logger: Logger = Logger.getLogger(MongoFeedEnvelopePersistence::class.java)
        const val userIdField = "user.userId"
        const val feedNameField = "source.name"
    }
}

class OffsetDateTimeCodecProvider : PropertyCodecProvider {
    override fun <T : Any?> get(type: TypeWithTypeParameters<T>, registry: PropertyCodecRegistry): Codec<T>? {
        if (type.type == OffsetDateTime::class.java) {
            @Suppress("UNCHECKED_CAST")
            return OffsetDateTimeCode() as Codec<T>
        }

        return null
    }
}

class OffsetDateTimeCode : Codec<OffsetDateTime> {
    override fun encode(writer: BsonWriter, value: OffsetDateTime, encoderContext: EncoderContext?) {
        writer.writeString(value.toString())
    }

    override fun getEncoderClass(): Class<OffsetDateTime> = OffsetDateTime::class.java

    override fun decode(reader: BsonReader, decoderContext: DecoderContext): OffsetDateTime {
        return OffsetDateTime.parse(reader.readString())
    }

}
