package ch.guengel.funnel.notifier

import ch.guengel.funnel.kafka.KafkaFeedEnvelope
import ch.guengel.funnel.kafka.toFeedEnvelope
import org.eclipse.microprofile.reactive.messaging.Incoming
import org.jboss.logging.Logger
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class Notifier(private val smtpService: SmtpService) {
    @Incoming("notify-in")
    fun receiveUpdate(kafkaFeedEnvelope: KafkaFeedEnvelope) {
        logger.infof(
            "Received updated feedenvelope '%s' for user '%s'",
            kafkaFeedEnvelope.source.name,
            kafkaFeedEnvelope.user.userId
        )
        smtpService.sendMail(kafkaFeedEnvelope.toFeedEnvelope())
    }

    private companion object {
        private val logger: Logger = Logger.getLogger(Notifier::class.java)
    }
}
