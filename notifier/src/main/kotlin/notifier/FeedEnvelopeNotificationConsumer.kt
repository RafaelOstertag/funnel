package ch.guengel.funnel.notifier

import ch.guengel.funnel.feed.data.FeedEnvelope
import ch.guengel.funnel.kafka.Consumer
import ch.guengel.funnel.kafka.updateNotificationTopic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class FeedEnvelopeNotificationConsumer(private val notificationSender: NotificationSender, kafkaServer: String) :
    AutoCloseable {
    private val consumer = Consumer(kafkaServer, groupId, updateNotificationTopic)

    fun start() = consumer.start(this::handleNotification)

    private fun handleNotification(topic: String, feedEnvelope: FeedEnvelope) {
        try {
            notificationSender.notify(feedEnvelope)
            logger.info("Sent notification for ${feedEnvelope.name}")
        } catch (e: Exception) {
            logger.error("Error while sending notification for ${feedEnvelope.name}", e)
        }
    }

    override fun close() = consumer.stop()

    private companion object {
        val logger: Logger = LoggerFactory.getLogger(FeedEnvelopeNotificationConsumer::class.java)
        const val groupId = "funnel.notifier.smtp"
    }

}