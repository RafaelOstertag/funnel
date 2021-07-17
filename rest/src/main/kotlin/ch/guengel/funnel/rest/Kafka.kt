package ch.guengel.funnel.rest

import ch.guengel.funnel.feed.bridges.FeedEnvelopePersistence
import ch.guengel.funnel.feed.data.FeedEnvelope
import ch.guengel.funnel.kafka.KafkaFeedEnvelope
import ch.guengel.funnel.kafka.asMessage
import ch.guengel.funnel.kafka.toFeedEnvelope
import ch.guengel.funnel.kafka.toKafkaFeedEnvelope
import io.smallrye.mutiny.Uni
import io.smallrye.reactive.messaging.annotations.Blocking
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Emitter
import org.eclipse.microprofile.reactive.messaging.Incoming
import org.jboss.logging.Logger
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class Kafka(@Inject private val feedEnvelopePersistence: FeedEnvelopePersistence) {
    @Inject
    @Channel("deletion-out")
    private lateinit var deletionEmitter: Emitter<KafkaFeedEnvelope>

    @Inject
    @Channel("new-out")
    private lateinit var newFeedEnvelopeEmitter: Emitter<KafkaFeedEnvelope>

    fun notifyNewFeedEnvelope(feedEnvelope: FeedEnvelope): Uni<Unit> = Uni.createFrom().item(feedEnvelope)
        .onItem().transform {
            it.toKafkaFeedEnvelope().asMessage()
        }
        .onItem().transform { newFeedEnvelopeEmitter.send(it) }


    fun deleteFeedenvelope(feedEnvelope: FeedEnvelope): Uni<Unit> = Uni.createFrom().item(feedEnvelope)
        .onItem().transform {
            it.toKafkaFeedEnvelope().asMessage()
        }
        .onItem().transform { deletionEmitter.send(it) }

    @Incoming("update-in")
    @Blocking
    fun receiveUpdate(kafkaFeedEnvelope: KafkaFeedEnvelope) {
        logger.infof(
            "Received update on '%s' for user '%s'",
            kafkaFeedEnvelope.source.name,
            kafkaFeedEnvelope.user.userId
        )
        feedEnvelopePersistence.saveFeedEnvelope(kafkaFeedEnvelope.toFeedEnvelope())
    }

    private companion object {
        private val logger: Logger = Logger.getLogger(Kafka::class.java)
    }
}
