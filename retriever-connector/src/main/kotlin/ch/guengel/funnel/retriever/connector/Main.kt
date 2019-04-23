package ch.guengel.funnel.retriever.connector

import ch.guengel.funnel.kafka.Constants.noData
import ch.guengel.funnel.kafka.Producer
import ch.guengel.funnel.kafka.Topics
import ch.guengel.funnel.readConfiguration
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

private val MILLIS_PER_SECOND = 1000L
private fun secondsToMillis(seconds: Int): Long = seconds * MILLIS_PER_SECOND

fun main(args: Array<String>) {
    val configuration = readConfiguration(Configuration)
    val sleepTime = secondsToMillis(configuration[Configuration.interval])

    val producer = Producer(configuration[Configuration.kafka])
    val consumer = setUpConsumer(configuration)

    val topicHandler = TopicHandler(configuration)

    topicHandler.use {
        consumer.start(it::handle)

        runBlocking {
            while (true) {
                delay(sleepTime)
                producer.send(Topics.retrieveAll, groupId, noData)
            }
        }
    }
}



