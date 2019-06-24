package ch.guengel.funnel.rest.modules

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import io.ktor.application.*
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.util.pipeline.PipelineContext

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

        exception<FeedNotFoundException> { cause ->
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
