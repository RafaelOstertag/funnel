package ch.guengel.funnel.xmlfeeds

import ch.guengel.funnel.domain.Feed
import ch.guengel.funnel.domain.NewsItem
import ch.guengel.funnel.domain.NewsItems
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText
import com.fasterxml.jackson.module.kotlin.readValue
import java.text.ParseException
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.xml.bind.annotation.XmlRootElement

private val epoch = ZonedDateTime.parse("1979-01-01T00:00:00Z")

internal class CustomDateDeserializer(vc: Class<Any>? = null) : StdDeserializer<ZonedDateTime>(vc) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): ZonedDateTime {
        val date = p.text
        try {
            return ZonedDateTime.parse(date, DateTimeFormatter.RFC_1123_DATE_TIME)
        } catch (e: ParseException) {
            throw CannotDeserializeXML(e)
        }
    }

}

private class XmlNode {
    @JacksonXmlText
    var value: String = ""
}

private class RssItem {
    @JacksonXmlProperty(localName = "title")
    var title: XmlNode = XmlNode()
    @JacksonXmlProperty(localName = "description")
    var description: XmlNode = XmlNode()
    @JacksonXmlProperty(localName = "pubDate")
    @JsonDeserialize(using = CustomDateDeserializer::class)
    var updated: ZonedDateTime = epoch
    @JacksonXmlProperty(localName = "guid")
    var id: XmlNode = XmlNode()
}

private data class RssChannel(
        @JacksonXmlProperty(localName = "title")
        val title: XmlNode,
        @JacksonXmlProperty(localName = "link")
        val id: XmlNode,
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "item")
        val rssItems: List<RssItem>
)

@XmlRootElement(name = "rss")
private data class RssFeed(
        val channel: RssChannel
)

internal class RssParser : FeedParser {
    override fun parse(xml: String, ignoreItemsBefore: ZonedDateTime): Feed {
        val rssFeedElement = deserializeXml(xml)
        val newsItems = extractNewsItems(rssFeedElement, ignoreItemsBefore)

        return Feed(rssFeedElement.channel.id.value,
                rssFeedElement.channel.title.value, newsItems)
    }

    private fun deserializeXml(xml: String): RssFeed {
        try {
            return xmlParser.readValue<RssFeed>(xml)
        } catch (e: Throwable) {
            throw CannotDeserializeXML(e)
        }
    }

    private fun extractNewsItems(rssFeed: RssFeed, ignoreItemsBefore: ZonedDateTime) = rssFeed
            .channel
            .rssItems
            .asSequence()
            .filter { it.updated.isAfter(ignoreItemsBefore) }
            .map {
                NewsItem(it.id.value, it.title.value, it.updated)
            }.fold(NewsItems()) { acc, newsItem ->
                acc.add(newsItem)
                acc
            }

}