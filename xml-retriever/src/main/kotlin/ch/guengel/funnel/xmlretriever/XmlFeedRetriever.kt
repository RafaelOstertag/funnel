package ch.guengel.funnel.xmlretriever

import ch.guengel.funnel.domain.Feed
import ch.guengel.funnel.xmlretriever.network.HttpTransport
import java.time.ZonedDateTime


class XmlFeedRetriever(private val httpTransport: HttpTransport) : FeedRetriever {
    override suspend fun retrieve(since: ZonedDateTime): Feed {
        var feed: Feed = Feed()
        httpTransport.retrieve { contentType: String, content: String ->

            val feedType = FeedType.detectFeedType(content)
            val parser = createFeedParser(feedType)
            feed = parser.parse(content, since)
        }
        return feed
    }
}