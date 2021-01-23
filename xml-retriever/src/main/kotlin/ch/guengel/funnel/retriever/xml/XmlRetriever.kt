package ch.guengel.funnel.retriever.xml

import ch.guengel.funnel.feed.bridges.FeedRetriever
import ch.guengel.funnel.feed.bridges.FeedRetrieverException
import ch.guengel.funnel.feed.data.Feed
import ch.guengel.funnel.feed.data.Source
import io.ktor.http.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class XmlRetriever : FeedRetriever {
    private val syndAdapter = SyndAdapter()

    override suspend fun fetch(source: Source): Feed {
        // Open and closing the client should not be a bottleneck, since it is assumed that interval between calls to the
        // same site is minutes or hours apart. But this should fix issues with the client hogging the CPU on long running
        // processes
        XmlFetcher().use {
            val url = Url(source.address)
            try {
                val feedXml = it.fetch(url)
                return syndAdapter.toFeed(feedXml)
            } catch (e: Exception) {
                val errorMessage = "Error while retrieving feed ${source.address}"
                logger.error(errorMessage, e)
                throw FeedRetrieverException(errorMessage, e)
            }
        }
    }

    private companion object {
        val logger: Logger = LoggerFactory.getLogger(XmlRetriever::class.java)
    }
}