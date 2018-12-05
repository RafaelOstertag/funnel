package ch.guengel.funnel.xmlretriever

import ch.guengel.funnel.domain.Feed
import ch.guengel.funnel.domain.FeedItem
import ch.guengel.funnel.domain.FeedItems
import org.xml.sax.Attributes
import org.xml.sax.InputSource
import org.xml.sax.XMLReader
import org.xml.sax.helpers.AttributesImpl
import org.xml.sax.helpers.DefaultHandler
import java.io.StringReader
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.xml.parsers.SAXParserFactory

private val epoch = ZonedDateTime.parse("1979-01-01T00:00:00Z")

internal class RssParser : FeedParser {
    private val saxParserFactory = SAXParserFactory.newInstance()

    override fun parse(xml: String, ignoreItemsBefore: ZonedDateTime): Feed {
        val xmlReader = makeParser()

        val context = ParserContext(ignoreItemsBefore)
        xmlReader.contentHandler = RssHandler(context)

        xmlReader.parse(stringToInputSource(xml))

        return context.getFeedData()
    }

    private fun stringToInputSource(str: String): InputSource {
        val stream = StringReader(str)
        return InputSource(stream)
    }

    private fun makeParser(): XMLReader {
        saxParserFactory.isNamespaceAware = true
        val saxParser = saxParserFactory.newSAXParser()
        return saxParser.xmlReader
    }
}

private class RssHandler(private val context: ParserContext) : DefaultHandler() {
    override fun startElement(uri: String?, localName: String?, qName: String?, attributes: Attributes?) {
        if (uri.isNullOrBlank()) {
            context.startTag(
                    localName ?: throw ParserException("No local name provided"),
                    attributes ?: AttributesImpl()
            )
        }
    }

    override fun endElement(uri: String?, localName: String?, qName: String?) {
        if (uri.isNullOrBlank()) {
            context.endTag(localName ?: throw ParserException("No local name provided"))
        }
    }

    override fun characters(ch: CharArray?, start: Int, length: Int) {
        context.handleCharacters(
                ch ?: CharArray(0),
                start,
                length
        )
    }
}

enum class TagNames {
    RSS,
    CHANNEL,
    TITLE,
    LINK,
    PUBDATE,
    GUID,
    ITEM
}

interface ParserState {
    fun startTag(feedData: FeedData, name: String, attributes: Attributes): ParserState
    fun endTag(feedData: FeedData, name: String): ParserState
    fun handleCharacters(feedData: FeedData, ch: CharArray, start: Int, length: Int): ParserState
}

class FeedData(private val ignoreItemsBefore: ZonedDateTime) {
    var feedTitle = ""
    var feedId = ""
    var currentFeedItemTitle = ""
    var currentFeedItemCreated = epoch
    var currentFeedItemId = ""
    private val feedItems: FeedItems = FeedItems()


    fun finalizeFeedItem() {
        if (currentFeedItemCreated.isAfter(ignoreItemsBefore))
            feedItems.add(FeedItem(currentFeedItemId, currentFeedItemTitle, currentFeedItemCreated))

        currentFeedItemCreated = epoch
        currentFeedItemId = ""
        currentFeedItemTitle = ""
    }

    fun getFeed(): Feed {
        return Feed(feedId, feedTitle, feedItems)
    }
}

class RootElement : ParserState {
    override fun endTag(feedData: FeedData, name: String): ParserState {
        return this
    }

    override fun handleCharacters(feedData: FeedData, ch: CharArray, start: Int, length: Int): ParserState {
        return this
    }

    override fun startTag(feedData: FeedData, name: String, attributes: Attributes): ParserState {
        return when (name.toUpperCase()) {
            FeedType.RSS.toString() -> InRssTag()
            else -> throw ParserException("Unexpected tag '$name'")
        }
    }
}

class InRssTag : ParserState {
    override fun endTag(feedData: FeedData, name: String): ParserState {
        return this
    }

    override fun handleCharacters(feedData: FeedData, ch: CharArray, start: Int, length: Int): ParserState {
        return this
    }

    override fun startTag(feedData: FeedData, name: String, attributes: Attributes): ParserState {
        return when (name.toUpperCase()) {
            TagNames.CHANNEL.toString() -> InChannelTag()
            else -> throw ParserException("Unexpected tag '$name'")
        }
    }

}

class InChannelTag : ParserState {
    override fun endTag(feedData: FeedData, name: String): ParserState {
        return InRssTag()
    }

    override fun handleCharacters(feedData: FeedData, ch: CharArray, start: Int, length: Int): ParserState {
        return this
    }

    override fun startTag(feedData: FeedData, name: String, attributes: Attributes): ParserState {
        return when (name.toUpperCase()) {
            TagNames.TITLE.toString() -> InChannelTitleTag()
            TagNames.LINK.toString() -> InChannelLinkTag()
            TagNames.ITEM.toString() -> InItemTag()
            else -> InUnknownChannelTag()
        }
    }

}

class InUnknownChannelTag : ParserState {
    override fun startTag(feedData: FeedData, name: String, attributes: Attributes): ParserState {
        return this
    }

    override fun endTag(feedData: FeedData, name: String): ParserState {
        return InChannelTag()
    }

    override fun handleCharacters(feedData: FeedData, ch: CharArray, start: Int, length: Int): ParserState {
        return this
    }

}

class InChannelTitleTag : ParserState {
    private val title = StringBuffer()

    override fun endTag(feedData: FeedData, name: String): ParserState {
        feedData.feedTitle = title.toString()
        return InChannelTag()
    }

    override fun startTag(feedData: FeedData, name: String, attributes: Attributes): ParserState {
        throw ParserException("Unexpected tag '$name'")
    }

    override fun handleCharacters(feedData: FeedData, ch: CharArray, start: Int, length: Int): ParserState {
        title.append(ch.copyOfRange(start, start + length))
        return this
    }
}

class InChannelLinkTag : ParserState {
    private val link = StringBuffer()

    override fun endTag(feedData: FeedData, name: String): ParserState {
        feedData.feedId = link.toString()
        return InChannelTag()
    }

    override fun handleCharacters(feedData: FeedData, ch: CharArray, start: Int, length: Int): ParserState {
        link.append(ch.copyOfRange(start, start + length))
        return this
    }

    override fun startTag(feedData: FeedData, name: String, attributes: Attributes): ParserState {
        throw ParserException("Unexpected tag '$name'")
    }

}

class InUnknownItemTag : ParserState {
    override fun startTag(feedData: FeedData, name: String, attributes: Attributes): ParserState {
        return this
    }

    override fun endTag(feedData: FeedData, name: String): ParserState {
        return InItemTag()
    }

    override fun handleCharacters(feedData: FeedData, ch: CharArray, start: Int, length: Int): ParserState {
        return this
    }

}

class InItemTag : ParserState {
    override fun endTag(feedData: FeedData, name: String): ParserState {
        feedData.finalizeFeedItem()
        return InChannelTag()
    }

    override fun startTag(feedData: FeedData, name: String, attributes: Attributes): ParserState {
        return when (name.toUpperCase()) {
            TagNames.TITLE.toString() -> InItemTitleTag()
            TagNames.PUBDATE.toString() -> InItemPubDateTag()
            TagNames.GUID.toString() -> InItemGuidTag()
            else -> InUnknownItemTag()
        }
    }

    override fun handleCharacters(feedData: FeedData, ch: CharArray, start: Int, length: Int): ParserState {
        return this
    }

}

class InItemTitleTag : ParserState {
    private val title = StringBuffer()

    override fun endTag(feedData: FeedData, name: String): ParserState {
        feedData.currentFeedItemTitle = title.toString()
        return InItemTag()
    }

    override fun startTag(feedData: FeedData, name: String, attributes: Attributes): ParserState {
        throw ParserException("Unexpected tag '$name'")
    }

    override fun handleCharacters(feedData: FeedData, ch: CharArray, start: Int, length: Int): ParserState {
        title.append(ch.copyOfRange(start, start + length))
        return this
    }

}

class InItemGuidTag : ParserState {
    private val guid = StringBuffer()

    override fun endTag(feedData: FeedData, name: String): ParserState {
        feedData.currentFeedItemId = guid.toString()
        return InItemTag()
    }

    override fun startTag(feedData: FeedData, name: String, attributes: Attributes): ParserState {
        throw ParserException("Unexpected tag '$name'")
    }

    override fun handleCharacters(feedData: FeedData, ch: CharArray, start: Int, length: Int): ParserState {
        guid.append(ch.copyOfRange(start, start + length))
        return this
    }

}

class InItemPubDateTag : ParserState {
    private val dateAsString = StringBuffer()

    override fun endTag(feedData: FeedData, name: String): ParserState {
        feedData.currentFeedItemCreated =
                ZonedDateTime.parse(dateAsString.toString(), DateTimeFormatter.RFC_1123_DATE_TIME)
        return InItemTag()
    }

    override fun startTag(feedData: FeedData, name: String, attributes: Attributes): ParserState {
        throw ParserException("Unexpected tag '$name'")
    }

    override fun handleCharacters(feedData: FeedData, ch: CharArray, start: Int, length: Int): ParserState {
        dateAsString.append(ch.copyOfRange(start, start + length))
        return this
    }

}

class ParserContext(
        private val ignoreItemsBefore: ZonedDateTime,
        private var currentState: ParserState = RootElement()) {
    private val feedData = FeedData(ignoreItemsBefore)

    fun startTag(name: String, attributes: Attributes) {
        currentState = currentState.startTag(feedData, name, attributes)
    }

    fun endTag(name: String) {
        currentState = currentState.endTag(feedData, name)
    }

    fun handleCharacters(ch: CharArray, start: Int, length: Int) {
        currentState = currentState.handleCharacters(feedData, ch, start, length)
    }

    fun getFeedData(): Feed {
        return feedData.getFeed()
    }

}

class ParserException(message: String) : Exception(message)
