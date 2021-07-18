package ch.guengel.funnel.rest

import ch.guengel.funnel.feed.bridges.FeedEnvelopePersistence
import ch.guengel.funnel.feed.data.Feed
import ch.guengel.funnel.feed.data.FeedEnvelope
import ch.guengel.funnel.feed.data.User
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import ch.guengel.funnel.rest.model.FeedEnvelope as FeedEnvelopeDto
import ch.guengel.funnel.rest.model.Source as SourceDto

@ApplicationScoped
class Service(@Inject private val feedEnvelopePersistence: FeedEnvelopePersistence, @Inject private val kafka: Kafka) {

    fun getAllSourcesForUser(userId: String): Multi<SourceDto> = Multi.createFrom()
        .iterable(feedEnvelopePersistence.findAllFeedEnvelopesForUser(userId))
        .onItem().transform { it.source.toDto() }

    fun createNewFeedEnvelope(sourceDto: SourceDto, email: String, userId: String): Uni<Unit> = Uni.combine()
        .all().unis(Uni.createFrom().item(sourceDto), Uni.createFrom().item(userId), Uni.createFrom().item(email))
        .asTuple()
        .onItem().transform { FeedEnvelope(User(it.item2, it.item3), it.item1.toSource(), Feed()) }
        .onItem().transform {
            feedEnvelopePersistence.saveFeedEnvelope(it)
            it
        }.onItem().transformToUni { it -> kafka.notifyNewFeedEnvelope(it) }

    fun getFeedEnvelopeForUser(userId: String, name: String): Uni<FeedEnvelopeDto> = Uni.createFrom()
        .item(feedEnvelopePersistence.findFeedEnvelope(userId, name))
        .onItem().ifNotNull().transform { it!!.toFeedEnvelopeDto() }

    fun deleteFeedEnvelopeForUser(userId: String, name: String): Uni<Boolean> = Uni.createFrom()
        .item(feedEnvelopePersistence.findFeedEnvelope(userId, name))
        .onItem().ifNotNull().transform {
            feedEnvelopePersistence.deleteFeedEnvelope(it!!)
            it
        }
        .onItem().ifNotNull().transformToUni { it ->
            kafka.deleteFeedenvelope(it)
        }
        .onItem().ifNotNull().transform { true }
        .onItem().ifNull().continueWith { false }
}
