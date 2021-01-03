package ch.guengel.funnel.chronos

import ch.guengel.funnel.build.logBuildInfo
import ch.guengel.funnel.persistence.MongoFeedEnvelopePersistence
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("funnel-chronos")

fun main(args: Array<String>) {
    logBuildInfo(logger)

    val environment = commandLineEnvironment(args)

    val mongoUrl = environment.config.property("mongo.url").getString()
    val mongoDb = environment.config.property("mongo.database").getString()
    val feedPersistence =
        MongoFeedEnvelopePersistence(mongoUrl, mongoDb)

    val kafkaServer = environment.config.property("kafka.server").getString()
    val feedEmitter = FeedEmitter(feedPersistence, kafkaServer)

    val interval = environment.config.property("chronos.interval").getString().toLong()
    val scheduler = Scheduler(interval, feedEmitter)

    scheduler.start()

    Runtime.getRuntime().addShutdownHook(Thread {
        scheduler.close()
        feedPersistence.close()
        feedEmitter.close()
    })

    embeddedServer(Netty, environment).start(wait = true)
}