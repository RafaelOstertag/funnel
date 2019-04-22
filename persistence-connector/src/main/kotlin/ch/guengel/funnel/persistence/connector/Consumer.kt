package ch.guengel.funnel.persistence.connector

import ch.guengel.funnel.kafka.Consumer
import ch.guengel.funnel.kafka.Topics
import com.uchuhimo.konf.Config
import kotlinx.coroutines.runBlocking

fun setUpConsumer(configuration: Config): Consumer {
    val consumer = Consumer(
        configuration[Configuration.kafka],
        "funnel.persistence.connector",
        listOf(Topics.retrieveAll, Topics.feedUpdate)
    )

    Runtime.getRuntime().addShutdownHook(Thread {
        runBlocking {
            consumer.stop()
        }
    })
    return consumer
}