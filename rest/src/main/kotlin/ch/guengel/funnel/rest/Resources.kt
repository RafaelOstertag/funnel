package ch.guengel.funnel.rest

import ch.guengel.funnel.rest.api.FeedsApi
import io.quarkus.security.Authenticated
import io.smallrye.common.annotation.Blocking
import io.smallrye.mutiny.Uni
import org.eclipse.microprofile.jwt.JsonWebToken
import javax.inject.Inject
import javax.ws.rs.core.Response
import ch.guengel.funnel.rest.model.Source as SourceDto

@Authenticated
class Resources(private val service: Service) : FeedsApi {
    @Inject
    private lateinit var jsonWebToken: JsonWebToken

    @Blocking
    override fun createFeed(source: SourceDto): Uni<Response> =
        service.createNewFeedEnvelope(source, jsonWebToken.emailAddress(), jsonWebToken.userId())
            .onItem().ifNotNull().transform { Response.status(Response.Status.CREATED) }
            .onItem().ifNull().continueWith { Response.status(Response.Status.NOT_FOUND) }
            .onItem().transform { it.build() }

    @Blocking
    override fun deleteFeedByName(name: String): Uni<Response> =
        service.deleteFeedEnvelopeForUser(jsonWebToken.userId(), name)
            .onItem()
            .transform { deleted -> if (deleted) Response.noContent() else Response.status(Response.Status.NOT_FOUND) }
            .onItem().transform { it.build() }

    @Blocking
    override fun getAllFeeds(): Uni<Response> = service.getAllSourcesForUser(jsonWebToken.userId())
        .collect().asList()
        .onItem().transform { Response.ok(it).build() }

    @Blocking
    override fun getFeedByName(name: String): Uni<Response> =
        service.getFeedEnvelopeForUser(jsonWebToken.userId(), name)
            .onItem().ifNotNull().transform { Response.ok(it) }
            .onItem().ifNull().continueWith { Response.status(Response.Status.NOT_FOUND) }
            .onItem().transform { it.build() }


    private fun JsonWebToken.userId() = this.subject
    private fun JsonWebToken.emailAddress(): String = this.claim<String>("email").orElseThrow {
        IllegalStateException("Unable to get 'email' claim from JWT")
    }

}
