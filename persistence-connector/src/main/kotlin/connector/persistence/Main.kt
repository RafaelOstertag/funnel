package ch.guengel.funnel.connector.persistence

import ch.guengel.funnel.build.logBuildInfo
import ch.guengel.funnel.feed.logic.FeedEnvelopeRemover
import ch.guengel.funnel.feed.logic.FeedEnvelopeSaver
import ch.guengel.funnel.feed.logic.FeedEnvelopeTrimmer
import ch.guengel.funnel.persistence.MongoFeedEnvelopePersistence
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("persistence-connector")

fun main(args: Array<String>) {
    logBuildInfo(logger)

    val environment = commandLineEnvironment(args)

    val mongoUrl = environment.config.property("mongo.url").getString()
    val mongoDb = environment.config.property("mongo.database").getString()
    val mongoFeedPersistence = MongoFeedEnvelopePersistence(mongoUrl, mongoDb)

    val retainMaxFeeds = environment.config.property("persistence.retain-max-feeds").getString().toInt()
    val feedEnvelopeTrimmer = FeedEnvelopeTrimmer(retainMaxFeeds)
    val feedEnvelopeSaver = FeedEnvelopeSaver(mongoFeedPersistence, feedEnvelopeTrimmer)

    val kafkaServer = environment.config.property("kafka.server").getString()
    val feedEnvelopeSaveConsumer =
        FeedEnvelopeSaveConsumer(
            feedEnvelopeSaver,
            kafkaServer
        )
    feedEnvelopeSaveConsumer.start()

    val feedEnvelopeDeleteConsumer =
        FeedEnvelopeDeleteConsumer(
            FeedEnvelopeRemover(mongoFeedPersistence),
            kafkaServer
        )
    feedEnvelopeDeleteConsumer.start()

    Runtime.getRuntime().addShutdownHook(Thread {
        feedEnvelopeSaveConsumer.close()
        feedEnvelopeDeleteConsumer.close()
        mongoFeedPersistence.close()
    })

    embeddedServer(Netty, environment).start(wait = true)
}