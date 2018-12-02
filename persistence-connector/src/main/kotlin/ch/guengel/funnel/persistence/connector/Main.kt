package ch.guengel.funnel.persistence.connector

import ch.guengel.funnel.persistence.MongoFeedEnvelopeRepository
import ch.guengel.funnel.readConfiguration
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {
    val configuration = readConfiguration(Configuration)

    val consumer = PersistenceConsumer(
        configuration[Configuration.kafka],
        "funnel.persistence"
    )
    val mongo =
        MongoFeedEnvelopeRepository(configuration[Configuration.mongoDbURL], configuration[Configuration.mongoDb])

    consumer.start { topic, key, data ->
        mongo.save(deserialize(data))
    }

    Runtime.getRuntime().addShutdownHook(Thread {
        runBlocking {
            consumer.stop()
        }
    })

    runBlocking {
        while (true) {
            delay(10000)
        }
    }
}