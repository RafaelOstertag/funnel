package ch.guengel.funnel.connector.xmlretriever

import ch.guengel.funnel.feed.bridges.FeedEnvelopePersistence
import ch.guengel.funnel.kafka.KafkaFeedEnvelope
import ch.guengel.funnel.kafka.toFeedEnvelope
import io.smallrye.reactive.messaging.annotations.Blocking
import org.eclipse.microprofile.reactive.messaging.Incoming
import org.jboss.logging.Logger
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class Kafka(@Inject private val feedEnvelopePersistence: FeedEnvelopePersistence) {
    @Incoming("new-in")
    @Blocking
    fun receiveNewFeedEnvelope(kafkaFeedEnvelope: KafkaFeedEnvelope) {
        logger.infof(
            "Received new feedenvelope '%s' for user '%s'",
            kafkaFeedEnvelope.source.name,
            kafkaFeedEnvelope.user.userId
        )
        feedEnvelopePersistence.saveFeedEnvelope(kafkaFeedEnvelope.toFeedEnvelope())
    }

    @Incoming("delete-in")
    @Blocking
    fun receiveDeletionFeedEnvelope(kafkaFeedEnvelope: KafkaFeedEnvelope) {
        logger.infof(
            "Received deleted feedenvelope '%s' for user '%s'",
            kafkaFeedEnvelope.source.name,
            kafkaFeedEnvelope.user.userId
        )
        feedEnvelopePersistence.deleteFeedEnvelope(kafkaFeedEnvelope.toFeedEnvelope())
    }

    private companion object {
        private val logger: Logger = Logger.getLogger(Kafka::class.java)
    }
}
