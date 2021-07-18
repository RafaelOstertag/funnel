package ch.guengel.funnel.connector.xmlretriever.http

import io.smallrye.mutiny.Uni
import org.eclipse.microprofile.rest.client.RestClientBuilder
import org.jboss.logging.Logger
import java.net.URI
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.client.ClientRequestContext
import javax.ws.rs.client.ClientRequestFilter

@ApplicationScoped
class XmlFetcher {
    fun fetch(uri: URI): Uni<String> {
        logger.infof("Fetch feed from '%s'", uri)
        return getClientForUrl(uri).getDocument()
    }

    private fun getClientForUrl(uri: URI): XMLFeedApi = RestClientBuilder.newBuilder()
        .baseUri(uri)
        .register(DefaultHeaders())
        .followRedirects(true)
        .build(XMLFeedApi::class.java)

    private companion object {
        val logger: Logger = Logger.getLogger(XmlFetcher::class.java)
    }
}

class DefaultHeaders : ClientRequestFilter {
    override fun filter(requestContext: ClientRequestContext) {
        requestContext.headers["User-Agent"] = listOf("Funnel 8.0")
        requestContext.headers["Accept"] = listOf("*/*")
    }
}
