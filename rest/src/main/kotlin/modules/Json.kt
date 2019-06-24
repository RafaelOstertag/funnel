package ch.guengel.funnel.rest.modules

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson

fun Application.jsonModule() {
    log.info("Setup JSON Module")
    install(ContentNegotiation) {
        jackson {
            registerModule(JavaTimeModule())
            disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        }
    }
}