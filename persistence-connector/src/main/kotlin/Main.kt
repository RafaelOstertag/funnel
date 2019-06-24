package ch.guengel.funnel.persistence.connector

import ch.guengel.funnel.build.info.readBuildInfo
import ch.guengel.funnel.persistence.MongoFeedEnvelopeRepository
import ch.guengel.funnel.readConfiguration
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.lang.Thread.sleep

private val logger = LoggerFactory.getLogger("persistence-connector")
private val buildInfo = readBuildInfo("/git.json")

fun main() {
    logger.info("${buildInfo.buildVersion} ${buildInfo.commitIdAbbrev}")
    val configuration = readConfiguration(Configuration)

    val consumer = setUpConsumer(configuration[Configuration.kafka])

    val feedEnvelopeRepository = MongoFeedEnvelopeRepository(configuration[Configuration.mongoDbURL], configuration[Configuration.mongoDb])
    val topicHandler = TopicHandler(feedEnvelopeRepository)

    consumer.start(topicHandler::handle)

    Runtime.getRuntime().addShutdownHook(Thread {
        feedEnvelopeRepository.close()
        runBlocking { consumer.stop() }
    })

    logger.info("Startup complete")
    while (true) {
        sleep(1000)
    }
}