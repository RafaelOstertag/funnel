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
    val countDownLatch = CountDownLatch(1)

    MongoFeedPersistence(configuration[Configuration.mongoDbURL], configuration[Configuration.mongoDb]).use {
        val feedEnvelopeTrimmer = FeedEnvelopeTrimmer(configuration[Configuration.retainMaxFeeds])
        val feedEnvelopeSaver = FeedEnvelopeSaver(it, feedEnvelopeTrimmer)

        FeedEnvelopeSaveConsumer(feedEnvelopeSaver, configuration[Configuration.kafka]).use {
            it.start()
            logger.info("Startup complete")
            countDownLatch.await()
        }
    }

    Runtime.getRuntime().addShutdownHook(Thread {
        countDownLatch.countDown()
    })
}