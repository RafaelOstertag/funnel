package ch.guengel.funnel.xmlfeeds

import ch.guengel.funnel.domain.Source
import ch.guengel.funnel.xmlfeeds.network.HttpTransport
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime


class XmlFeedRetrieverIT {
    @Test
    fun `retrieve fowler`() {
        val source = Source("fowler", "https://martinfowler.com/feed.atom")
        val httpTransport = HttpTransport(source)
        val xmlFeedRetriever = XmlFeedRetriever(httpTransport)
        xmlFeedRetriever.retrieve(ZonedDateTime.parse("2000-01-01T00:00:00Z"))
    }

    @Test
    fun `retrieve baeldung`() {
        val source = Source("fowler", "https://www.baeldung.com/feed/")
        val httpTransport = HttpTransport(source)
        val xmlFeedRetriever = XmlFeedRetriever(httpTransport)
        xmlFeedRetriever.retrieve(ZonedDateTime.parse("2000-01-01T00:00:00Z"))
    }

}