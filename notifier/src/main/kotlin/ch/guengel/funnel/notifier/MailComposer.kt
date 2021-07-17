package ch.guengel.funnel.notifier

import ch.guengel.funnel.feed.data.FeedEnvelope
import io.quarkus.mailer.Mail
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class MailComposer {
    fun composeFromFeedEnvelope(feedEnvelope: FeedEnvelope) =
        Mail
            .withHtml(feedEnvelope.user.email, composeSubject(feedEnvelope), composeMainBodyPart(feedEnvelope))

    private fun composeMainBodyPart(feedEnvelope: FeedEnvelope): String =
        """Updates for <a href="${feedEnvelope.source.address}">${feedEnvelope.feed.title}</a>
            |<ul>
            |${
            feedEnvelope.feed.feedItems.items.map { "<li><a href=\"${it.link}\">${it.title}</a></li>" }.joinToString(
                separator = "\n"
            )
        }
            |</ul>
        """.trimMargin()

    private fun composeSubject(feedEnvelope: FeedEnvelope): String = "[Funnel] ${feedEnvelope.name}"
}
