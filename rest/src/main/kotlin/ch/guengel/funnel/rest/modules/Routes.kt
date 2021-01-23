package ch.guengel.funnel.rest.modules

import ch.guengel.funnel.feed.bridges.FeedEnvelopePersistence
import ch.guengel.funnel.feed.data.Feed
import ch.guengel.funnel.feed.data.FeedEnvelope
import ch.guengel.funnel.feed.data.Source
import ch.guengel.funnel.feed.logic.FeedEnvelopeRemover
import ch.guengel.funnel.persistence.MongoFeedEnvelopePersistence
import ch.guengel.funnel.rest.infoRoute
import ch.guengel.funnel.rest.utils.extractUser
import ch.guengel.funnel.rest.utils.getUserId
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*

fun Application.routes() {
    log.info("Setting up routes")
    val feedEnvelopePersistence = setupMongoFeedEnvelopePersistence(this)

    routing {
        infoRoute()

        route("/feeds") {
            authenticate {
                get { retrieveAllNames(feedEnvelopePersistence) }
                post { saveEnvelope(feedEnvelopePersistence) }
            }
            route("/{name}") {
                authenticate {
                    get { retrieveByName(feedEnvelopePersistence) }
                    delete { deleteByName(feedEnvelopePersistence) }
                }
            }
        }
    }
    log.info("Routes set up")
}

private suspend fun PipelineContext<Unit, ApplicationCall>.saveEnvelope(feedEnvelopePersistence: FeedEnvelopePersistence) {
    val source = call.receive<Source>()
    val principal = call.authentication.principal
    val user = extractUser(principal)
    val feedEnvelope = FeedEnvelope(user, source, Feed())

    feedEnvelopePersistence.saveFeedEnvelope(feedEnvelope)
    call.respond(HttpStatusCode.Created)
}

private suspend fun PipelineContext<Unit, ApplicationCall>.deleteByName(feedEnvelopePersistence: FeedEnvelopePersistence) {
    val feedEnvelopeName = call.parameters["name"]
    feedEnvelopeName ?: throw IllegalArgumentException("no name specified")

    val principal = call.authentication.principal
    FeedEnvelopeRemover(feedEnvelopePersistence).remove(getUserId(principal), feedEnvelopeName)
    call.respond(HttpStatusCode.NoContent)
}

private suspend fun PipelineContext<Unit, ApplicationCall>.retrieveAllNames(
    feedEnvelopePersistence: FeedEnvelopePersistence
) {
    val principal = call.authentication.principal
    call.respond(
        feedEnvelopePersistence.findAllFeedEnvelopesForUser(getUserId(principal))
            .map { feedEnvelope -> feedEnvelope.source })
}

private suspend fun PipelineContext<Unit, ApplicationCall>.retrieveByName(
    feedEnvelopeRepository: FeedEnvelopePersistence
) {
    val feedEnvelopeName = call.parameters["name"]
    feedEnvelopeName ?: throw IllegalArgumentException("no name specified")

    val principal = call.authentication.principal
    val feedEnvelope = feedEnvelopeRepository.findFeedEnvelope(getUserId(principal), feedEnvelopeName)
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

