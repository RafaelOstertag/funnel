package ch.guengel.funnel.connector.xmlretriever.rest.modules

import ch.guengel.funnel.rest.infoRoute
import io.ktor.application.*
import io.ktor.routing.*

fun Application.routes() {
    log.info("Setting up routes")
    routing {
        infoRoute()
    }
    log.info("Routes set up")
}