package ch.guengel.funnel.retriever.connector

import ch.guengel.funnel.kafka.Constants
import ch.guengel.funnel.kafka.Constants.noData
import ch.guengel.funnel.kafka.Producer
import ch.guengel.funnel.kafka.Topics
import ch.guengel.funnel.readConfiguration
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("retriever-connector")
private const val MILLIS_PER_SECOND = 1000L
private fun secondsToMillis(seconds: Int): Long = seconds * MILLIS_PER_SECOND


fun main(args: Array<String>) {
    val configuration = readConfiguration(Configuration)
    val intervalSeconds = configuration[Configuration.interval]
    val intervalMillis = secondsToMillis(intervalSeconds)
    logger.info("Query sources every {} seconds", intervalSeconds)

    val producer = Producer(configuration[Configuration.kafka])
    val consumer = setUpConsumer(configuration)

    val topicHandler = TopicHandler(configuration)

    topicHandler.use {
        consumer.start(it::handle)

        logger.info("Startup complete")
        runBlocking {
            while (true) {
                delay(intervalMillis)
                producer.send(Topics.retrieveAll, Constants.noKey, allFeedReplyTopic)
                logger.info("Retrieve all sources")
            }
        }
    }
}



