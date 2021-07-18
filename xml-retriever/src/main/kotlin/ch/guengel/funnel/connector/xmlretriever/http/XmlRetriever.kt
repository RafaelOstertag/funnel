package ch.guengel.funnel.connector.xmlretriever.http

import ch.guengel.funnel.feed.bridges.FeedRetriever
import ch.guengel.funnel.feed.bridges.FeedRetrieverException
import ch.guengel.funnel.feed.data.Feed
import ch.guengel.funnel.feed.data.Source
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jboss.logging.Logger
import java.net.URI
import java.time.Duration
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class XmlRetriever(
    @Inject private val xmlFetcher: XmlFetcher,
    @ConfigProperty(name = "funnel.xmlretriever.timeout-seconds",
        defaultValue = "10") private val timeout: Long,
) : FeedRetriever {
    private val syndAdapter = SyndAdapter()

    override suspend fun fetch(source: Source): Feed = xmlFetcher.fetch(URI.create(source.address))
        .onItem().transform {
            syndAdapter.toFeed(it)
        }
        .onFailure().transform {
            val errorMessage = "Error while retrieving feed ${source.address}"
            logger.error(errorMessage, it)
            FeedRetrieverException(errorMessage, it)
        }.await().atMost(Duration.ofSeconds(timeout))

    private companion object {
        val logger: Logger = Logger.getLogger(XmlRetriever::class.java)
    }
}

