package ch.guengel.funnel.connector.xmlretriever.http

import ch.guengel.funnel.feed.data.Feed
import ch.guengel.funnel.feed.data.FeedConstants
import ch.guengel.funnel.feed.data.FeedItem
import ch.guengel.funnel.feed.data.FeedItems
import com.rometools.rome.feed.module.DCModule
import com.rometools.rome.feed.module.Module
import com.rometools.rome.feed.synd.SyndEntry
import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.SyndFeedInput
import org.jboss.logging.Logger

import java.io.StringReader
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.*

internal class SyndAdapter {
    private val syndFeedInput = SyndFeedInput()

    fun toFeed(feedData: String): Feed {
        val stringReader = stringToReader(feedData)
        val syndFeed = stringReader.use {
            syndFeedInput.build(it)
        }

        return syndFeedToFeed(syndFeed)
    }

    private fun syndFeedToFeed(syndFeed: SyndFeed): Feed {
        return syndFeed.entries
            .map {
                extractFeedItem(it)
            }
            .filter { feedItem ->
                feedItem != FeedItem.empty
            }
            .let {
                FeedItems(it)
            }
            .let {
                Feed(syndFeed.uri ?: syndFeed.link, syndFeed.title, it)
            }
    }

    private fun extractFeedItem(syndEntry: SyndEntry): FeedItem {
        var createdDate: OffsetDateTime? = null
        try {
            createdDate = extractCreatedDate(syndEntry)
        } catch (e: Exception) {
            logger.warn("No created date available, use empty date")
        }
        if (syndEntry.title == null || syndEntry.uri == null) {
            logger.warn("No title available, return empty feed")
            return FeedItem.empty
        }
        return FeedItem(
            syndEntry.uri,
            syndEntry.title,
            syndEntry.link,
            createdDate ?: FeedConstants.emptyCreated
        )
    }

    private fun extractCreatedDate(syndEntry: SyndEntry): OffsetDateTime {
        var createdDate: Date? = syndEntry.updatedDate
        if (createdDate != null) {
            return legacyDateToOffsetDateTime(createdDate)
        }

        createdDate = extractDateFromModules(syndEntry)
        return legacyDateToOffsetDateTime(createdDate)
    }

    private fun extractDateFromModules(syndEntry: SyndEntry): Date {
        val module: Module? = syndEntry.getModule("http://purl.org/dc/elements/1.1/")
        if (module == null) {
            logger.warnf("No way of extracting created date from %s", syndEntry.title)
            throw IllegalArgumentException("Unable to extract created date")
        }

        module as DCModule
        val dcDate: Date? = module.date
        if (dcDate == null) {
            logger.warnf("No way of extracting created date from %s", syndEntry.title)
            throw IllegalArgumentException("Unable to extract created date")
        }

        return dcDate
    }

    private fun legacyDateToOffsetDateTime(legacyDate: Date): OffsetDateTime {
        val instant = legacyDate.toInstant()
        return OffsetDateTime.ofInstant(instant, ZoneId.of("UTC"))
    }

    private fun stringToReader(feedData: String): StringReader {
        return StringReader(feedData)
    }

    private companion object {
        val logger: Logger = Logger.getLogger(SyndAdapter::class.java)
    }
}
