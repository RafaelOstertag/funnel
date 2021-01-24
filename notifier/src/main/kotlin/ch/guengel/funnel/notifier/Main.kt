package ch.guengel.funnel.notifier

import ch.guengel.funnel.build.logBuildInfo
import ch.guengel.funnel.notifier.smtp.SmtpNotificationSender
import ch.guengel.funnel.notifier.smtp.SmtpNotificationSettings
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("notifier")

fun main(args: Array<String>) {
    logBuildInfo(logger)

    val environment = commandLineEnvironment(args)

    val smtpServer = environment.config.property("smtp.server").getString()
    val smtpPort = environment.config.property("smtp.port").getString().toInt()
    val smtpSender = environment.config.property("smtp.sender").getString()
    val smtpNotificationSettings = SmtpNotificationSettings(smtpServer, smtpPort, smtpSender)

    val kafkaServer = environment.config.property("kafka.server").getString()
    val feedEnvelopeNotificationConsumer =
        FeedEnvelopeNotificationConsumer(SmtpNotificationSender(smtpNotificationSettings), kafkaServer)
    feedEnvelopeNotificationConsumer.start()

    Runtime.getRuntime().addShutdownHook(Thread {
        feedEnvelopeNotificationConsumer.close()
    })

    embeddedServer(Netty, environment).start(wait = true)
}