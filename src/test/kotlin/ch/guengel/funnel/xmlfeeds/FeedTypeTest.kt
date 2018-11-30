package ch.guengel.funnel.xmlfeeds

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class FeedTypeTest {

    @Test
    fun mimeTypeToFeedType() {
        assertEquals(FeedType.ATOM, FeedType.mimeTypeToFeedType("application/atom+xml"))
        assertEquals(FeedType.RSS, FeedType.mimeTypeToFeedType("application/rss+xml"))
        assertEquals(FeedType.RSS, FeedType.mimeTypeToFeedType("text/xml"))

        assertThrows<UnknownFeedType> { FeedType.mimeTypeToFeedType("unknown") }
    }

    @Test
    fun `handle mimetype with character set information`() {
        assertEquals(FeedType.ATOM, FeedType.mimeTypeToFeedType("application/atom+xml; charset=UTF-8"))
    }
}