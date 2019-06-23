package ch.guengel.funnel.retriever.connector

import ch.guengel.funnel.build.info.readBuildInfo
import ch.guengel.funnel.kafka.Producer
import ch.guengel.funnel.readConfiguration
import com.uchuhimo.konf.Config
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("retriever-connector")
private const val MILLIS_PER_SECOND = 1000L
private val buildInfo = readBuildInfo("/git.json")


fun main(args: Array<String>) {
    logger.info("${buildInfo.buildVersion} ${buildInfo.commitIdAbbrev}")
    val configuration = readConfiguration(Configuration)

    val producer = createProducer(configuration)
    Runtime.getRuntime().addShutdownHook(Thread {
        producer.close()
    })

    val consumer = setUpConsumer(configuration)
    Runtime.getRuntime().addShutdownHook(Thread {
        runBlocking {
            consumer.stop()
        }
    })

    val feedUpdater = FeedUpdater(producer)

    val topicHandler = TopicHandler(feedUpdater)

    consumer.start(topicHandler::handle)

    logger.info("Startup complete")
    runBlocking {
        while (true) {
            delay(1000)
        }
    }
}

private fun createProducer(configuration: Config): Producer {
    val producer = Producer(configuration[Configuration.kafka])

    return producer
}



