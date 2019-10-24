package ch.guengel.funnel.retriever.xml

import ch.guengel.funnel.feed.bridges.FeedRetriever
import ch.guengel.funnel.feed.bridges.FeedRetrieverException
import ch.guengel.funnel.feed.data.Feed
import ch.guengel.funnel.feed.data.Source
import io.ktor.http.Url
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class XmlRetriever : FeedRetriever, AutoCloseable {
    private val syndAdapter = SyndAdapter()
    private val xmlFetcher = XmlFetcher()

    override suspend fun fetch(source: Source): Feed {
        check(!xmlFetcher.closed) {
            logger.error(closedClientErrorMessage)
            closedClientErrorMessage
        }

        val url = Url(source.address)
        try {
            val feedXml = xmlFetcher.fetch(url)
            return syndAdapter.toFeed(feedXml)
        } catch (e: Exception) {
            val errorMessage = "Error while retrieving feed ${source.address}"
            logger.error(errorMessage, e)
            throw FeedRetrieverException(errorMessage, e)
        }
    }

    override fun close() {
        xmlFetcher.close()
        logger.info("Close XML Feed Fetcher")
    }

    private companion object {
        val logger: Logger = LoggerFactory.getLogger(XmlRetriever::class.java)
        const val closedClientErrorMessage = "Client is closed"
    }
}