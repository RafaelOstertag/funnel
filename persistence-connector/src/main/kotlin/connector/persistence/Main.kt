package ch.guengel.funnel.connector.persistence

import ch.guengel.funnel.build.readBuildInfo
import ch.guengel.funnel.configuration.readConfiguration
import ch.guengel.funnel.feed.logic.FeedEnvelopeSaver
import ch.guengel.funnel.feed.logic.FeedEnvelopeTrimmer
import ch.guengel.funnel.persistence.MongoFeedEnvelopePersistence
import org.slf4j.LoggerFactory
import java.util.concurrent.CountDownLatch

private val logger = LoggerFactory.getLogger("persistence-connector")
private val buildInfo = readBuildInfo("/git.json")

fun main() {
    logger.info("${buildInfo.buildVersion} ${buildInfo.commitIdAbbrev}")
    val configuration = readConfiguration(Configuration)


    val mongoFeedPersistence = MongoFeedEnvelopePersistence(
        configuration[Configuration.mongoDbURL], configuration[Configuration.mongoDb]
    )

    val feedEnvelopeTrimmer = FeedEnvelopeTrimmer(configuration[Configuration.retainMaxFeeds])
    val feedEnvelopeSaver = FeedEnvelopeSaver(mongoFeedPersistence, feedEnvelopeTrimmer)

    val feedEnvelopeSaveConsumer =
        FeedEnvelopeSaveConsumer(
            feedEnvelopeSaver,
            configuration[Configuration.kafka]
        )
    feedEnvelopeSaveConsumer.start()

    val feedEnvelopeDeleteConsumer =
        FeedEnvelopeDeleteConsumer(
            mongoFeedPersistence,
            configuration[Configuration.kafka]
        )
    feedEnvelopeDeleteConsumer.start()


    val countDownLatch = CountDownLatch(1)
    Runtime.getRuntime().addShutdownHook(Thread {
        feedEnvelopeSaveConsumer.close()
        feedEnvelopeDeleteConsumer.close()
        mongoFeedPersistence.close()
        countDownLatch.countDown()
    })

    logger.info("Startup complete")
    countDownLatch.await()
}