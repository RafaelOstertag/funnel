package ch.guengel.funnel.cli

import ch.guengel.funnel.common.serialize
import ch.guengel.funnel.domain.Feed
import ch.guengel.funnel.domain.FeedEnvelope
import ch.guengel.funnel.domain.Source
import ch.guengel.funnel.kafka.Constants
import ch.guengel.funnel.kafka.Producer
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.findObject
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option

private const val defaultKafkaAddress = "localhost:9092"
private const val PERSIST_TOPIC = "ch.guengel.funnel.persist.envelope"
private const val DELETE_TOPIC = "ch.guengel.funnel.delete.envelope"

private fun makeFeed(sourceName: String, sourceAddress: String): FeedEnvelope {
    val source = Source(sourceName, sourceAddress)
    val feed = Feed.empty()
    return FeedEnvelope(source, feed)
}

private fun createFeed(sourceName: String, sourceAddress: String, kafkaAddress: String) {
    val feed = makeFeed(sourceName, sourceAddress)
    Producer(kafkaAddress).use {
        it.send(PERSIST_TOPIC, Constants.noKey, serialize(feed))
    }
}

private fun deleteFeed(sourceName: String, kafkaAddress: String) {
    Producer(kafkaAddress).use {
        it.send(DELETE_TOPIC, Constants.noKey, sourceName)
    }
}

class CreateFeed : CliktCommand() {
    val sourceName: String by argument(help = "Source name")
    val sourceAddress: String by argument(help = "Source address")
    val config by findObject { mutableMapOf<String, String>() }
    override fun run() {
        createFeed(sourceName, sourceAddress, config["kafka"] ?: defaultKafkaAddress)
    }
}

class DeleteFeed : CliktCommand() {
    val sourceName: String by argument(help = "Source name")
    val config by findObject { mutableMapOf<String, String>() }
    override fun run() {
        deleteFeed(sourceName, config["kafka"] ?: defaultKafkaAddress)
    }
}

class CliOptions : CliktCommand() {
    val kafka by option(help = "Kafka Address").default(value = defaultKafkaAddress)
    val config by findObject { mutableMapOf<String, String>() }

    override fun run() {
        config["kafka"] = kafka
    }
}


fun main(args: Array<String>) = CliOptions().subcommands(CreateFeed()).subcommands(DeleteFeed()).main(args)



