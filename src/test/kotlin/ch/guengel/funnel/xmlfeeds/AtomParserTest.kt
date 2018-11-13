package ch.guengel.funnel.xmlfeeds

import ch.guengel.funnel.domain.NewsItem
import org.junit.Test

import java.io.File
import java.time.ZonedDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AtomParserTest {

    @Test
    fun `parse xml as atom`() {
        val xml = File("src/test/resources/atom-feed.xml").readText(Charsets.UTF_8)
        val atomParser = AtomParser()
        val feed = atomParser.parse(xml, ZonedDateTime.parse("2000-01-01T00:00:00Z"))

        assertEquals("urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6", feed.id)
        assertEquals(ZonedDateTime.parse("2003-12-13T18:30:02Z"), feed.lastUpdated)
        assertEquals("Example Feed", feed.title)
        assertEquals(1, feed.newsItems.size)

        val expectedNewsItem = NewsItem("urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a", "Atom-Powered Robots Run Amok", ZonedDateTime.parse("2003-12-13T18:30:02Z"))

        assertEquals(expectedNewsItem, feed.newsItems.latest)

        assertTrue(feed.newsItems.hasItem(expectedNewsItem))
    }

    @Test
    fun `honor ignore before`() {
        val xml = File("src/test/resources/atom-feed.xml").readText(Charsets.UTF_8)
        val atomParser = AtomParser()
        val feed = atomParser.parse(xml, ZonedDateTime.parse("2003-12-13T18:30:02Z"))
        assertTrue(feed.newsItems.size == 0)

    }
}