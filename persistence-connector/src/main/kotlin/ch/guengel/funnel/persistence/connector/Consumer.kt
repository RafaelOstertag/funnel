package ch.guengel.funnel.persistence.connector

import ch.guengel.funnel.kafka.Consumer
import ch.guengel.funnel.kafka.Topics
import com.uchuhimo.konf.Config
import kotlinx.coroutines.runBlocking

val groupId = "funnel.persistence.connector"

fun setUpConsumer(configuration: Config): Consumer {
    val consumer = Consumer(
        configuration[Configuration.kafka],
        groupId,
            listOf(Topics.retrieveAll,
                    Topics.persistFeed,
                    Topics.feedDelete,
                    Topics.retrieveAllNames,
                    Topics.retrieveFeedByName)
    )

    Runtime.getRuntime().addShutdownHook(Thread {
        runBlocking {
            consumer.stop()
        }
    })

    return consumer
}