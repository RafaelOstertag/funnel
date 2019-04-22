package ch.guengel.funnel.retriever.connector

import ch.guengel.funnel.kafka.Consumer
import ch.guengel.funnel.kafka.Topics
import com.uchuhimo.konf.Config
import kotlinx.coroutines.runBlocking

val groupId = "funnel.retriever.connector"

fun setUpConsumer(configuration: Config): Consumer {
    val consumer = Consumer(
        configuration[Configuration.kafka],
        groupId,
        listOf(Topics.retrieveAll)
    )

    Runtime.getRuntime().addShutdownHook(Thread {
        runBlocking {
            consumer.stop()
        }
    })
    return consumer
}