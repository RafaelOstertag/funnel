package ch.guengel.funnel.connector.xmlretriever

import ch.guengel.funnel.build.logBuildInfo
import ch.guengel.funnel.feed.logic.FeedEnvelopeRetriever
import ch.guengel.funnel.retriever.xml.XmlRetriever
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("retriever-connector")

fun main(args: Array<String>) {
    logBuildInfo(logger)

    val environment = commandLineEnvironment(args)

    val feedEnvelopeRetriever = FeedEnvelopeRetriever(XmlRetriever())
    val kafkaServer = environment.config.property("kafka.server").getString()
    val allFeedsConsumer = AllFeedsConsumer(
        feedEnvelopeRetriever,
        kafkaServer
    )

    allFeedsConsumer.start()

    Runtime.getRuntime().addShutdownHook(Thread {
        allFeedsConsumer.close()
    })

    embeddedServer(Netty, environment).start(wait = true)
}
