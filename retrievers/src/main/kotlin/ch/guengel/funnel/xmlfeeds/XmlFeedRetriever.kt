package ch.guengel.funnel.xmlfeeds

import ch.guengel.funnel.domain.Feed
import ch.guengel.funnel.domain.FeedRetriever
import ch.guengel.funnel.xmlfeeds.network.HttpTransport
import java.time.ZonedDateTime


class XmlFeedRetriever(private val httpTransport: HttpTransport) : FeedRetriever {
    override fun retrieve(since: ZonedDateTime): Feed {
        val feedType = FeedType.mimeTypeToFeedType(httpTransport.contentType())
        val parser = makeFeedParser(feedType)
        return parser.parse(httpTransport.response(), since)
    }
}