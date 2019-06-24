package ch.guengel.funnel.persistence.connector

import ch.guengel.funnel.kafka.Consumer

val groupId = "funnel.persistence.connector"

fun setUpConsumer(kafka: String): Consumer {
    val consumer = Consumer(
            kafka,
            groupId,
            listOf(
                    PERSIST_TOPIC,
                    DELETE_TOPIC
            )
    )

    return consumer
}