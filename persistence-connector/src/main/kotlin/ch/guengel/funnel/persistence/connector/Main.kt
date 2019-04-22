package ch.guengel.funnel.persistence.connector

import ch.guengel.funnel.kafka.Consumer
import ch.guengel.funnel.kafka.Topics
import ch.guengel.funnel.readConfiguration
import com.uchuhimo.konf.Config
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {
    val configuration = readConfiguration(Configuration)

    val consumer = setUpConsumer(configuration)

    val topicHandler = TopicHandler(
            configuration[Configuration.mongoDbURL],
            configuration[Configuration.mongoDb],
            configuration[Configuration.kafka])

    topicHandler.use {
        consumer.start { topic, key, data ->
            it.handle(topic, key, data)
        }

        runBlocking {
            while (true) {
                delay(10000)
            }
        }
    }

}

private fun setUpConsumer(configuration: Config): Consumer {
    val consumer = Consumer(
            configuration[Configuration.kafka],
            "funnel.persistence.connector",
            listOf(Topics.retrieveAll, Topics.saveSingle)
    )

    Runtime.getRuntime().addShutdownHook(Thread {
        runBlocking {
            consumer.stop()
        }
    })
    return consumer
}