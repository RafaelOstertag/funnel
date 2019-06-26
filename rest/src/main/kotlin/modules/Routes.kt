package ch.guengel.funnel.rest.modules

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

class FeedNotFoundException(message: String) : Exception(message)

fun Application.routes() {
    log.info("Setting up routes")
    val feedEnvelopeRepository = setupMongoFeedEnvelopeRepository(this)

    routing {
        route("/feeds") {
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

    feedEnvelopeRepository.save(feedEnvelope)
    call.respond(HttpStatusCode.Created)
}

private suspend fun PipelineContext<Unit, ApplicationCall>.deleteByName(feedEnvelopeRepository: MongoFeedEnvelopeRepository) {
    val feedEnvelopeName = call.parameters["name"]
    feedEnvelopeName ?: throw IllegalArgumentException("no name specified")

    feedEnvelopeRepository.deleteByName(feedEnvelopeName)
    call.respond(HttpStatusCode.NoContent)
}

private suspend fun PipelineContext<Unit, ApplicationCall>.retrieveAllNames(
    feedEnvelopeRepository: MongoFeedEnvelopeRepository
) {
    call.respond(feedEnvelopeRepository.retrieveAllSources())
}

private suspend fun PipelineContext<Unit, ApplicationCall>.retrieveByName(
    feedEnvelopeRepository: MongoFeedEnvelopeRepository
) {
    val feedEnvelopeName = call.parameters["name"]
    feedEnvelopeName ?: throw IllegalArgumentException("no name specified")

    val feedEnvelope = feedEnvelopeRepository.retrieveByName(feedEnvelopeName)
    feedEnvelope ?: throw FeedNotFoundException("No Feed Envelope with name ${feedEnvelopeName} exists")
    call.respond(feedEnvelope)
}

private fun setupMongoFeedEnvelopeRepository(application: Application): MongoFeedEnvelopeRepository {
    application.log.info("Initialize Feed Envelope Repository")
    val mongoConfig = application.environment.config.config("mongo")

    return MongoFeedEnvelopeRepository(
        mongoConfig.property("url").getString(),
        mongoConfig.property("database").getString()
    )
}

