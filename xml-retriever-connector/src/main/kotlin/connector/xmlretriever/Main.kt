package ch.guengel.funnel.connector.xmlretriever

import ch.guengel.funnel.build.logBuildInfo
import ch.guengel.funnel.configuration.readConfiguration
import ch.guengel.funnel.feed.logic.FeedEnvelopeRetriever
import ch.guengel.funnel.retriever.xml.XmlRetriever
import org.slf4j.LoggerFactory
import java.util.concurrent.CountDownLatch

private val logger = LoggerFactory.getLogger("retriever-connector")

fun main() {
    logBuildInfo(logger)
    val configuration = readConfiguration(Configuration)

    val feedEnvelopeRetriever = FeedEnvelopeRetriever(XmlRetriever())
    val allFeedsConsumer = AllFeedsConsumer(
        feedEnvelopeRetriever,
        configuration[Configuration.kafka]
    )

    allFeedsConsumer.start()

    val countDownLatch = CountDownLatch(1)
    Runtime.getRuntime().addShutdownHook(Thread {
        allFeedsConsumer.close()
        countDownLatch.countDown()
    })

    logger.info("Startup complete")
    countDownLatch.await()
}
