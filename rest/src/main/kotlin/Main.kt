package ch.guengel.funnel.rest

import ch.guengel.funnel.build.info.readBuildInfo
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("rest-application")
private val buildInfo = readBuildInfo("/git.json")

fun main(args: Array<String>) {
    logger.info("${buildInfo.buildVersion} ${buildInfo.commitIdAbbrev}")

    val environment = commandLineEnvironment(args)

    embeddedServer(Netty, environment).start(wait = true)
}

