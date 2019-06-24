package ch.guengel.funnel.rest

import ch.guengel.funnel.build.info.readBuildInfo
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.slf4j.LoggerFactory
import org.slf4j.event.Level

private val logger = LoggerFactory.getLogger("rest-application")
private val buildInfo = readBuildInfo("/git.json")

fun main(args: Array<String>) {
    logger.info("${buildInfo.buildVersion} ${buildInfo.commitIdAbbrev}")

    val environment = commandLineEnvironment(args)


    embeddedServer(Netty, environment).start(wait = true)
}

fun Application.jsonModule() {
    log.info("Setup JSON Module")
    install(ContentNegotiation) {
        jackson {
            registerModule(JavaTimeModule())
            disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        }
    }
}

fun Application.callLogging() {
    log.info("Setup Call Logging Module")
    install(CallLogging) {
        level = Level.INFO
    }
}
