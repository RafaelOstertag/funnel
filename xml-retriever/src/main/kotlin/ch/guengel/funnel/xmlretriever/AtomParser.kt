package ch.guengel.funnel.xmlretriever

import ch.guengel.funnel.domain.Feed
import ch.guengel.funnel.domain.FeedItem
import ch.guengel.funnel.domain.FeedItems
import com.fasterxml.jackson.annotation.JsonRootName
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.module.kotlin.readValue
import java.time.ZonedDateTime


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

@JsonRootName(value = "feed")
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
                FeedItem(it.id, it.title, it.updated)
            }.fold(FeedItems()) { acc, newsItem ->
                acc.add(newsItem)
                acc
            }

}
