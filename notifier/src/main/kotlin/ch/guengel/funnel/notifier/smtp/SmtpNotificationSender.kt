package ch.guengel.funnel.notifier.smtp

import ch.guengel.funnel.feed.data.FeedEnvelope
import ch.guengel.funnel.notifier.NotificationSender
import java.util.*
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

class SmtpNotificationSender(private val smtpNotificationSettings: SmtpNotificationSettings) :
    NotificationSender {
    private val session = Session.getInstance(toProperties(smtpNotificationSettings))

    override fun notify(feedEnvelope: FeedEnvelope) {
        val message = toMessage(feedEnvelope)
        Transport.send(message)
    }

    private fun toMessage(feedEnvelope: FeedEnvelope): Message = MimeMessage(session).apply {
        setFrom(InternetAddress(smtpNotificationSettings.sender))
        setRecipients(Message.RecipientType.TO, InternetAddress.parse(feedEnvelope.user.email))

        val subject: String = composeSubject(feedEnvelope)
        setSubject(subject)

        val mimeBodyPart: MimeBodyPart = composeMainBodyPart(feedEnvelope)
        val multipart = MimeMultipart().apply {
            addBodyPart(mimeBodyPart)
        }
        setContent(multipart)
    }

    private fun composeMainBodyPart(feedEnvelope: FeedEnvelope): MimeBodyPart {
        val body = """Updates for <a href="${feedEnvelope.source.address}">${feedEnvelope.feed.title}</a>
            |<ul>
            |${feedEnvelope.feed.feedItems.items.map { "<li><a href=\"${it.link}\">${it.title}</a></li>" }.joinToString(
            separator = "\n"
        )}
            |</ul>
        """.trimMargin()
        return MimeBodyPart().apply {
            setContent(body, "text/html")
        }
    }

    private fun composeSubject(feedEnvelope: FeedEnvelope): String = "[Funnel] ${feedEnvelope.name}"

    private fun toProperties(smtpNotificationSettings: SmtpNotificationSettings): Properties = Properties().apply {
        put("mail.smtp.auth", false)
        put("mail.smtp.host", smtpNotificationSettings.server)
        put("mail.smtp.port", smtpNotificationSettings.port)
    }
}

