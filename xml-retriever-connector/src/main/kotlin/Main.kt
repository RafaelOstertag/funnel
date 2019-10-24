package funnel.connector.retriever

import ch.guengel.funnel.readConfiguration
import funnel.build.info.readBuildInfo
import logic.FeedEnvelopeRetriever
import org.slf4j.LoggerFactory
import retriever.XmlRetriever
import java.util.concurrent.CountDownLatch

private val logger = LoggerFactory.getLogger("retriever-connector")
private val buildInfo = readBuildInfo("/git.json")

fun main() {
    logger.info("${buildInfo.buildVersion} ${buildInfo.commitIdAbbrev}")
    val configuration = readConfiguration(Configuration)

    val feedEnvelopeRetriever = FeedEnvelopeRetriever(XmlRetriever())
    val allFeedsConsumer = AllFeedsConsumer(feedEnvelopeRetriever, configuration[Configuration.kafka])

    allFeedsConsumer.start()

    val countDownLatch = CountDownLatch(1)
    Runtime.getRuntime().addShutdownHook(Thread {
        allFeedsConsumer.close()
        countDownLatch.countDown()
    })

    logger.info("Startup complete")
    countDownLatch.await()
}
