package ch.guengel.funnel.rest.modules

import io.ktor.application.*
import io.ktor.features.*
import org.slf4j.event.Level

fun Application.callLogging() {
    log.info("Setup Call Logging Module")
    install(CallLogging) {
        level = Level.INFO
    }
}