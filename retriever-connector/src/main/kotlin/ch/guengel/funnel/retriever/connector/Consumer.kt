package ch.guengel.funnel.retriever.connector

import ch.guengel.funnel.kafka.Consumer
import ch.guengel.funnel.kafka.Topics
import com.uchuhimo.konf.Config
import kotlinx.coroutines.runBlocking

const val groupId = "funnel.retriever.connector"
const val allFeedReplyTopic = "funnel.retriever.connector.allfeeds"

fun setUpConsumer(configuration: Config): Consumer {
    val consumer = Consumer(
        configuration[Configuration.kafka],
        groupId,
        listOf(allFeedReplyTopic)
    )

    Runtime.getRuntime().addShutdownHook(Thread {
        runBlocking {
            consumer.stop()
        }
    })
    return consumer
}