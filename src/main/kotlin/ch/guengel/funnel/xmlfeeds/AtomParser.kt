package ch.guengel.funnel.xmlfeeds

import ch.guengel.funnel.domain.Feed
import ch.guengel.funnel.domain.NewsItem
import ch.guengel.funnel.domain.NewsItems
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.module.kotlin.readValue
import java.time.ZonedDateTime
import javax.xml.bind.annotation.XmlRootElement


private data class AtomLink(
        @JacksonXmlProperty(localName = "href", isAttribute = true)
        val href: String)

private data class AtomEntry(
        var title: String,
        var link: AtomLink,
        var id: String,
        var updated: ZonedDateTime,
        var summary: String = "")

private data class Author(var name: String)

@XmlRootElement(name = "feed")
private data class AtomFeedElement(
        var title: String,
        var link: AtomLink,
        var updated: ZonedDateTime,
        var author: Author,
        var id: String,
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "entry")
        var atomEntries: List<AtomEntry>)

internal class AtomParser : FeedParser {
    override fun parse(xml: String, ignoreItemsBefore: ZonedDateTime): Feed {
        val rssFeedElement = deserializeXml(xml)
        val newsItems = extractNewsItems(rssFeedElement, ignoreItemsBefore)

        return Feed(rssFeedElement.id, rssFeedElement.title, newsItems)
    }

    private fun deserializeXml(xml: String): AtomFeedElement {
        try {
            return xmlParser.readValue<AtomFeedElement>(xml)
        } catch (e: Throwable) {
            throw CannotDeserializeXML(e)
        }
    }

    private fun extractNewsItems(rssAtomFeedElement: AtomFeedElement, ignoreItemsBefore: ZonedDateTime) = rssAtomFeedElement.atomEntries
            .asSequence()
            .filter { it.updated.isAfter(ignoreItemsBefore) }
            .map {
                NewsItem(it.id, it.title, it.updated)
            }.fold(NewsItems()) { acc, newsItem ->
                acc.add(newsItem)
                acc
            }

}
