package ch.guengel.funnel.retriever.connector.cli

import ch.guengel.funnel.common.serialize
import ch.guengel.funnel.domain.Feed
import ch.guengel.funnel.domain.FeedEnvelope
import ch.guengel.funnel.domain.Source
import ch.guengel.funnel.kafka.Constants
import ch.guengel.funnel.kafka.Producer
import ch.guengel.funnel.kafka.Topics
import ch.guengel.funnel.readConfiguration
import ch.guengel.funnel.retriever.connector.Configuration
import com.uchuhimo.konf.Config


fun main(args: Array<String>) {
    val validArgs = validateArgs(args)
    if (!validArgs) {
        printHelp()
        System.exit(1)
    }

    val feed = makeFeed(args[0], args[1])
    val producer = createProducer(readConfiguration(Configuration))
    producer.use {
        it.send(Topics.persistFeed, Constants.noKey, serialize(feed))
    }
}

fun createProducer(readConfiguration: Config): Producer {
    return Producer(readConfiguration[Configuration.kafka])
}

fun makeFeed(sourceName: String, sourceAddress: String): FeedEnvelope {
    val source = Source(sourceName, sourceAddress)
    val feed = Feed.empty()
    return FeedEnvelope(source, feed)
}

private fun validateArgs(args: Array<String>): Boolean = args.size == 2

private fun printHelp() {
    print(
        """
cli <sourceName> <sourceAddress>
    """.trimIndent()
    )
}
