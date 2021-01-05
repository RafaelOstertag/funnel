package ch.guengel.funnel.rest.modules

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*

fun Application.headersModule() {
    log.info("Setup CORS")
    install(CORS) {
        method(HttpMethod.Get)
        method(HttpMethod.Post)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header(HttpHeaders.ContentType)
        anyHost()
        allowCredentials = true
    }

    log.info("Setup default headers")
    install(DefaultHeaders)
}