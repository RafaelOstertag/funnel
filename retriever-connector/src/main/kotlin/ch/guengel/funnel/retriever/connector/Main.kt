package ch.guengel.funnel.retriever.connector

import ch.guengel.funnel.kafka.Constants.noData
import ch.guengel.funnel.kafka.Producer
import ch.guengel.funnel.kafka.Topics
import ch.guengel.funnel.readConfiguration
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {
    val configuration = readConfiguration(Configuration)

    val producer = Producer(configuration[Configuration.kafka])
    val consumer = setUpConsumer(configuration)

    val topicHandler = TopicHandler(configuration)

    topicHandler.use {
        consumer.start(it::handle)

        runBlocking {
            while (true) {
                delay(configuration[Configuration.interval])
                producer.send(Topics.retrieveAll, groupId, noData)
            }
        }
    }

}

