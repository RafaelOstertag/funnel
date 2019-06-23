package ch.guengel.funnel.chronos

import ch.guengel.funnel.build.info.readBuildInfo
import ch.guengel.funnel.kafka.Producer
import ch.guengel.funnel.persistence.MongoFeedEnvelopeRepository
import ch.guengel.funnel.readConfiguration
import org.slf4j.LoggerFactory
import java.util.*


private val logger = LoggerFactory.getLogger("funnel-chronos")
private val TO_MILLIS = 1_000L
private val buildInfo = readBuildInfo("/git.json")

fun main(args: Array<String>) {
    logger.info("${buildInfo.buildVersion} ${buildInfo.commitIdAbbrev}")
    val configuration = readConfiguration(Configuration)

    val mongoFeedEnvelopeRepository = MongoFeedEnvelopeRepository(
            configuration[Configuration.mongoDbURL],
            configuration[Configuration.mongoDb])

    val producer = Producer(configuration[Configuration.kafka])

    val sender = Sender("ch.guengel.funnel.all.envelopes", producer)

    val feedEnvelopSenderTask = FeedEnvelopSenderTask(mongoFeedEnvelopeRepository, sender)


    val interval = configuration[Configuration.interval]
    logger.info("Send envelopes every {}sec", interval)

    val timer = Timer()
    timer.schedule(feedEnvelopSenderTask, 1000, interval * TO_MILLIS)

    Runtime.getRuntime().addShutdownHook(Thread {
        timer.cancel()
        producer.close()
        mongoFeedEnvelopeRepository.close()
    })

    logger.info("Startup complete")
}