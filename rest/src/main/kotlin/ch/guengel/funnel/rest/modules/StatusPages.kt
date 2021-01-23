package ch.guengel.funnel.rest.modules

import ch.guengel.funnel.feed.bridges.FeedEnvelopeNotFoundException
import ch.guengel.funnel.rest.utils.AuthenticationException
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.util.pipeline.*

private const val UNKNOWN_REASON = "unknown reason"

data class ErrorResponse(val code: Int, val message: String)

fun Application.statusPages() {
    log.info("Setup Status Pages")
    install(StatusPages) {
        exception<IllegalArgumentException> { cause ->
            badRequest(cause)
        }

        exception<JsonParseException> { cause ->
            badRequest(cause)
        }

        exception<JsonMappingException> { cause ->
            badRequest(cause)
        }

        exception<AuthenticationException> { cause ->
            call.respond(HttpStatusCode.Forbidden, cause.message ?: UNKNOWN_REASON)
        }

        exception<FeedEnvelopeNotFoundException> { cause ->
            val message = ErrorResponse(HttpStatusCode.NotFound.value, cause.message ?: UNKNOWN_REASON)
            call.respond(HttpStatusCode.NotFound, message)
        }

        exception<Throwable> { cause ->
            val message = ErrorResponse(HttpStatusCode.InternalServerError.value, cause.message ?: UNKNOWN_REASON)
            call.respond(HttpStatusCode.InternalServerError, message)
        }
    }

}

private suspend fun PipelineContext<Unit, ApplicationCall>.badRequest(
    cause: Throwable
) {
    val message = ErrorResponse(HttpStatusCode.BadRequest.value, cause.message ?: UNKNOWN_REASON)
    call.respond(HttpStatusCode.BadRequest, message)
}
