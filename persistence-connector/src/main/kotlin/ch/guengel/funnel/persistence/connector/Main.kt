package ch.guengel.funnel.persistence.connector

import ch.guengel.funnel.readConfiguration
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {
    val configuration = readConfiguration(Configuration)

    val consumer = setUpConsumer(configuration)

    val topicHandler = TopicHandler(configuration)

    topicHandler.use {
        consumer.start(it::handle)

        runBlocking {
            while (true) {
                delay(10000)
            }
        }
    }

}

