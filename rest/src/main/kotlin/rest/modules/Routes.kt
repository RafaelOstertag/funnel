package ch.guengel.funnel.rest.modules

import ch.guengel.funnel.feed.bridges.FeedPersistence
import ch.guengel.funnel.feed.data.Feed
import ch.guengel.funnel.feed.data.FeedEnvelope
import ch.guengel.funnel.feed.data.Source
import ch.guengel.funnel.persistence.MongoFeedPersistence
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.util.pipeline.PipelineContext


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

private suspend fun PipelineContext<Unit, ApplicationCall>.saveEnvelope(feedPersistence: FeedPersistence) {
    val source = call.receive<Source>()
    val feedEnvelope = FeedEnvelope(source, Feed())

    feedPersistence.saveFeedEnvelope(feedEnvelope)
    call.respond(HttpStatusCode.Created)
}

private suspend fun PipelineContext<Unit, ApplicationCall>.deleteByName(feedPersistence: FeedPersistence) {
    val feedEnvelopeName = call.parameters["name"]
    feedEnvelopeName ?: throw IllegalArgumentException("no name specified")

    val source = Source(feedEnvelopeName, "not required")
    val feedEnvelope = FeedEnvelope(source, Feed())

    feedPersistence.deleteFeedEnvelope(feedEnvelope)
    call.respond(HttpStatusCode.NoContent)
}

private suspend fun PipelineContext<Unit, ApplicationCall>.retrieveAllNames(
    feedPersistence: FeedPersistence
) {
    call.respond(feedPersistence.findAllFeedEnvelopes().map { feedEnvelope -> feedEnvelope.source })
}

private suspend fun PipelineContext<Unit, ApplicationCall>.retrieveByName(
    feedEnvelopeRepository: FeedPersistence
) {
    val feedEnvelopeName = call.parameters["name"]
    feedEnvelopeName ?: throw IllegalArgumentException("no name specified")

    val feedEnvelope = feedEnvelopeRepository.findFeedEnvelope(feedEnvelopeName)
    call.respond(feedEnvelope)
}

private fun setupMongoFeedEnvelopeRepository(application: Application): FeedPersistence {
    application.log.info("Initialize Feed Envelope Repository")
    val mongoConfig = application.environment.config.config("mongo")

    return MongoFeedPersistence(
        mongoConfig.property("url").getString(),
        mongoConfig.property("database").getString()
    )
}

