package funnel.connector.persistence

import ch.guengel.funnel.readConfiguration
import funnel.build.info.readBuildInfo
import logic.FeedEnvelopeSaver
import logic.FeedEnvelopeTrimmer
import org.slf4j.LoggerFactory
import persistence.MongoFeedPersistence
import java.util.concurrent.CountDownLatch

private val logger = LoggerFactory.getLogger("persistence-connector")
private val buildInfo = readBuildInfo("/git.json")

fun main() {
    logger.info("${buildInfo.buildVersion} ${buildInfo.commitIdAbbrev}")
    val configuration = readConfiguration(Configuration)


    val mongoFeedPersistence = MongoFeedPersistence(
        configuration[Configuration.mongoDbURL], configuration[Configuration.mongoDb]
    )

    val feedEnvelopeTrimmer = FeedEnvelopeTrimmer(configuration[Configuration.retainMaxFeeds])
    val feedEnvelopeSaver = FeedEnvelopeSaver(mongoFeedPersistence, feedEnvelopeTrimmer)

    val feedEnvelopeSaveConsumer =
        FeedEnvelopeSaveConsumer(feedEnvelopeSaver, configuration[Configuration.kafka])

    feedEnvelopeSaveConsumer.start()

    val countDownLatch = CountDownLatch(1)
    Runtime.getRuntime().addShutdownHook(Thread {
        feedEnvelopeSaveConsumer.close()
        mongoFeedPersistence.close()
        countDownLatch.countDown()
    })

    logger.info("Startup complete")
    countDownLatch.await()
}