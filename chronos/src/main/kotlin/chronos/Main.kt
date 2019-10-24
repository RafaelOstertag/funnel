package ch.guengel.funnel.chronos

import ch.guengel.funnel.build.readBuildInfo
import ch.guengel.funnel.configuration.readConfiguration
import ch.guengel.funnel.persistence.MongoFeedPersistence
import org.slf4j.LoggerFactory
import java.util.concurrent.CountDownLatch


private val logger = LoggerFactory.getLogger("funnel-chronos")
private val buildInfo = readBuildInfo("/git.json")

fun main() {
    logger.info("${buildInfo.buildVersion} ${buildInfo.commitIdAbbrev}")
    val configuration = readConfiguration(Configuration)

    val feedPersistence =
        MongoFeedPersistence(configuration[Configuration.mongoDbURL], configuration[Configuration.mongoDb])
    val feedEmitter = FeedEmitter(feedPersistence, configuration[Configuration.kafka])
    val scheduler = Scheduler(configuration[Configuration.interval].toLong(), feedEmitter)

    scheduler.start()

    val countDownLatch = CountDownLatch(1)
    Runtime.getRuntime().addShutdownHook(Thread {
        scheduler.close()
        feedPersistence.close()
        feedEmitter.close()
        countDownLatch.countDown()
    })

    logger.info("Startup complete")
    countDownLatch.await()
}