package ch.guengel.funnel.connector.xmlretriever.http

import io.smallrye.mutiny.Uni
import javax.ws.rs.GET
import javax.ws.rs.Path

@Path("")
interface XMLFeedApi {
    @GET
    fun getDocument(): Uni<String>
}
