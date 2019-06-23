package ch.guengel.funnel.rest

import ch.guengel.funnel.domain.Feed
import ch.guengel.funnel.domain.FeedEnvelope
import ch.guengel.funnel.domain.Source
import ch.guengel.funnel.persistence.MongoFeedEnvelopeRepository
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.util.pipeline.PipelineContext

data class ErrorResponse(val code: Int, val message: String)

private const val UNKNOWN_REASON = "unknown reason"

fun Application.routes() {
    log.info("Setting up routes")
    val feedEnvelopeRepository = setupMongoFeedEnvelopeRepository(this)

    routing {
        route("/feedenvelopes") {
            get { retrieveAllNames(feedEnvelopeRepository) }
            post { saveEnvelope(feedEnvelopeRepository) }
            route("/{name}") {
                get { retrieveByName(feedEnvelopeRepository) }
                delete { deleteByName(feedEnvelopeRepository) }
            }
        }
    }
    log.info("Routes set up")
}

private suspend fun PipelineContext<Unit, ApplicationCall>.saveEnvelope(feedEnvelopeRepository: MongoFeedEnvelopeRepository) {
    val source = call.receive<Source>()
    val feedEnvelope = FeedEnvelope(source, Feed())
    try {
        feedEnvelopeRepository.save(feedEnvelope)
        call.respond(HttpStatusCode.Created)
    } catch (e: Throwable) {
        call.respond(
            HttpStatusCode.InternalServerError,
            ErrorResponse(HttpStatusCode.InternalServerError.value, e.message ?: UNKNOWN_REASON)
        )
    }

}

private suspend fun PipelineContext<Unit, ApplicationCall>.deleteByName(feedEnvelopeRepository: MongoFeedEnvelopeRepository) {
    val feedEnvelopeName = call.parameters["name"]
    if (feedEnvelopeName == null) {
        call.respond(HttpStatusCode.BadRequest, ErrorResponse(HttpStatusCode.BadRequest.value, "no name specified"))
        return
    }

    try {
        feedEnvelopeRepository.deleteByName(feedEnvelopeName)
    } catch (e: Throwable) {
        call.respond(
            HttpStatusCode.InternalServerError,
            ErrorResponse(HttpStatusCode.InternalServerError.value, e.message ?: UNKNOWN_REASON)
        )
        return
    }

    call.respond(HttpStatusCode.NoContent)
}

private suspend fun PipelineContext<Unit, ApplicationCall>.retrieveAllNames(
    feedEnvelopeRepository: MongoFeedEnvelopeRepository
) {
    call.respond(feedEnvelopeRepository.getAllFeedNames())
}

private suspend fun PipelineContext<Unit, ApplicationCall>.retrieveByName(
    feedEnvelopeRepository: MongoFeedEnvelopeRepository
) {
    val feedEnvelopeName = call.parameters["name"]
    if (feedEnvelopeName == null) {
        call.respond(HttpStatusCode.BadRequest, ErrorResponse(HttpStatusCode.BadRequest.value, "no name specified"))
        return
    }

    try {
        val feedEnvelope = feedEnvelopeRepository.retrieveByName(feedEnvelopeName)
        if (feedEnvelope == null) {
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(HttpStatusCode.NotFound.value, "No Feed Envelope with name ${feedEnvelopeName} exists")
            )
            return
        }
        call.respond(feedEnvelope)
    } catch (e: Throwable) {
        call.respond(
            HttpStatusCode.InternalServerError,
            ErrorResponse(HttpStatusCode.InternalServerError.value, e.message ?: UNKNOWN_REASON)
        )
    }
}

private fun setupMongoFeedEnvelopeRepository(application: Application): MongoFeedEnvelopeRepository {
    application.log.info("Initialize Feed Envelope Repository")
    val mongoConfig = application.environment.config.config("mongo")

    return MongoFeedEnvelopeRepository(
        mongoConfig.property("url").getString(),
        mongoConfig.property("database").getString()
    )
}

