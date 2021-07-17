package ch.guengel.funnel.notifier

import ch.guengel.funnel.feed.data.FeedEnvelope
import io.quarkus.mailer.reactive.ReactiveMailer
import org.jboss.logging.Logger
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class SmtpService(@Inject private val reactiveMailer: ReactiveMailer, @Inject private val mailComposer: MailComposer) {
    fun sendMail(feedEnvelope: FeedEnvelope) {
        reactiveMailer.send(
            mailComposer.composeFromFeedEnvelope(feedEnvelope)
        ).subscribeAsCompletionStage().thenApply {
            logger.infof("Notified '%s' for changes on '%s'", feedEnvelope.user.email, feedEnvelope.name)
        }.exceptionally {
            logger.errorf(it, "Error notifying '%s' for changes on '%s'", feedEnvelope.user.email, feedEnvelope.name)
        }
    }

    private companion object {
        val logger: Logger = Logger.getLogger(SmtpService::class.java)
    }
}
