package ch.guengel.funnel.xmlretriever

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.InputStreamReader
import kotlin.test.assertEquals

class FeedTypeTest {

    @Test
    fun mimeTypeToFeedType() {
        assertEquals(FeedType.ATOM, FeedType.detectFeedType(readSampleFile("/atom-feed.xml")))
        assertEquals(FeedType.RSS, FeedType.detectFeedType(readSampleFile("/baeldung.xml")))
        assertEquals(FeedType.RSS, FeedType.detectFeedType(readSampleFile("/sample-rss-2.xml")))

        assertThrows<UnknownFeedType> { FeedType.detectFeedType("<root/>") }
    }

    fun readSampleFile(resourcePath: String): String {
        FeedTypeTest::class.java.getResourceAsStream(resourcePath).use {
            val inputStreamReader = InputStreamReader(it)
            return inputStreamReader.readText()
        }
    }
}