package ch.guengel.funnel.cli

import ch.guengel.funnel.build.getBuildInfoString
import ch.guengel.funnel.build.readBuildInfo
import ch.guengel.funnel.feed.data.Feed
import ch.guengel.funnel.feed.data.FeedEnvelope
import ch.guengel.funnel.feed.data.Source
import ch.guengel.funnel.kafka.Producer
import ch.guengel.funnel.kafka.feedEnvelopeDeleteTopic
import ch.guengel.funnel.kafka.feedEnvelopePersistTopic
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.findObject
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option

private const val defaultKafkaAddress = "localhost:9092"

private fun makeFeedEnvelope(sourceName: String, sourceAddress: String): FeedEnvelope {
    val source = Source(sourceName, sourceAddress)
    val feed = Feed()
    return FeedEnvelope(source, feed)
}

private fun createFeed(sourceName: String, sourceAddress: String, kafkaAddress: String) {
    val feed = makeFeedEnvelope(sourceName, sourceAddress)
    Producer(kafkaAddress).use {
        it.send(feedEnvelopePersistTopic, feed)
    }
}

private fun deleteFeed(sourceName: String, kafkaAddress: String) {
    val feed = makeFeedEnvelope(sourceName, "not required")
    Producer(kafkaAddress).use {
        it.send(feedEnvelopeDeleteTopic, feed)
    }
}

class CreateFeed : CliktCommand() {
    val sourceName: String by argument(help = "Source name")
    val sourceAddress: String by argument(help = "Source address")
    val config by findObject { mutableMapOf<String, String>() }
    override fun run() {
        createFeed(
            sourceName,
            sourceAddress,
            config["kafka"] ?: defaultKafkaAddress
        )
    }
}

class DeleteFeed : CliktCommand() {
    val sourceName: String by argument(help = "Source name")
    val config by findObject { mutableMapOf<String, String>() }
    override fun run() {
        deleteFeed(
            sourceName,
            config["kafka"] ?: defaultKafkaAddress
        )
    }
}

class CliOptions : CliktCommand() {
    val kafka by option(help = "Kafka Address").default(value = defaultKafkaAddress)
    val config by findObject { mutableMapOf<String, String>() }

    override fun run() {
        config["kafka"] = kafka
    }
}


fun main(args: Array<String>) {
    val buildInfo = readBuildInfo()
    println(getBuildInfoString(buildInfo))
    CliOptions()
        .subcommands(CreateFeed()).subcommands(DeleteFeed()).main(args)
}



