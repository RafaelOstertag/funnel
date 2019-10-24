package ch.guengel.funnel.rest.modules

import ch.guengel.funnel.feed.bridges.FeedEnvelopePersistence
import ch.guengel.funnel.feed.data.Feed
import ch.guengel.funnel.feed.data.FeedEnvelope
import ch.guengel.funnel.feed.data.Source
import ch.guengel.funnel.persistence.MongoFeedEnvelopePersistence
import feed.logic.FeedEnvelopeRemover
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
    val feedEnvelopePersistence = setupMongoFeedEnvelopePersistence(this)

    routing {
        route("/feeds") {
            get { retrieveAllNames(feedEnvelopePersistence) }
            post { saveEnvelope(feedEnvelopePersistence) }
            route("/{name}") {
                get { retrieveByName(feedEnvelopePersistence) }
                delete { deleteByName(feedEnvelopePersistence) }
            }
        }
    }
    log.info("Routes set up")
}

private suspend fun PipelineContext<Unit, ApplicationCall>.saveEnvelope(feedEnvelopePersistence: FeedEnvelopePersistence) {
    val source = call.receive<Source>()
    val feedEnvelope = FeedEnvelope(source, Feed())

    feedEnvelopePersistence.saveFeedEnvelope(feedEnvelope)
    call.respond(HttpStatusCode.Created)
}

private suspend fun PipelineContext<Unit, ApplicationCall>.deleteByName(feedEnvelopePersistence: FeedEnvelopePersistence) {
    val feedEnvelopeName = call.parameters["name"]
    feedEnvelopeName ?: throw IllegalArgumentException("no name specified")

    FeedEnvelopeRemover(feedEnvelopePersistence).remove(feedEnvelopeName)
    call.respond(HttpStatusCode.NoContent)
}

private suspend fun PipelineContext<Unit, ApplicationCall>.retrieveAllNames(
    feedEnvelopePersistence: FeedEnvelopePersistence
) {
    call.respond(feedEnvelopePersistence.findAllFeedEnvelopes().map { feedEnvelope -> feedEnvelope.source })
}

private suspend fun PipelineContext<Unit, ApplicationCall>.retrieveByName(
    feedEnvelopeRepository: FeedEnvelopePersistence
) {
    val feedEnvelopeName = call.parameters["name"]
    feedEnvelopeName ?: throw IllegalArgumentException("no name specified")

    val feedEnvelope = feedEnvelopeRepository.findFeedEnvelope(feedEnvelopeName)
    call.respond(feedEnvelope)
}

private fun setupMongoFeedEnvelopePersistence(application: Application): FeedEnvelopePersistence {
    application.log.info("Initialize Feed Envelope Repository")
    val mongoConfig = application.environment.config.config("mongo")

    return MongoFeedEnvelopePersistence(
        mongoConfig.property("url").getString(),
        mongoConfig.property("database").getString()
    )
}

