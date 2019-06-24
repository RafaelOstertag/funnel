package ch.guengel.funnel.retriever.connector

import ch.guengel.funnel.kafka.Consumer
import com.uchuhimo.konf.Config

const val groupId = "funnel.retriever.connector"

fun setUpConsumer(configuration: Config): Consumer {
    val consumer = Consumer(
        configuration[Configuration.kafka],
            groupId,
            listOf(ALL_FEEDS_TOPIC)
    )

    return consumer
}