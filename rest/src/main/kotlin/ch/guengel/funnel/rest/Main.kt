package ch.guengel.funnel.rest

import ch.guengel.funnel.build.logBuildInfo
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("rest-application")

fun main(args: Array<String>) {
    logBuildInfo(logger)

    val environment = commandLineEnvironment(args)

    embeddedServer(Netty, environment).start(wait = true)
}

