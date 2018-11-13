package ch.guengel.funnel.xmlfeeds

import ch.guengel.funnel.domain.NewsItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class RssParserTest {

    @Test
    fun `parse rss 2`() {
        val xml = File("src/test/resources/sample-rss-2.xml").readText(Charsets.UTF_8)
        val rssParser = RssParser()
        val feed = rssParser.parse(xml, ZonedDateTime.parse("2000-01-01T00:00:00Z"))

        assertEquals("http://liftoff.msfc.nasa.gov/", feed.id)
        assertEquals(
                toZonedDateTime("Tue, 03 Jun 2003 09:39:21 GMT"),
                feed.lastUpdated)
        assertEquals("Liftoff News", feed.title)
        assertEquals(4, feed.newsItems.size)

        val expectedItem1 = NewsItem("http://liftoff.msfc.nasa.gov/2003/06/03.html#item573", "Star City", toZonedDateTime("Tue, 03 Jun 2003 09:39:21 GMT"))
        assertEquals(expectedItem1, feed.newsItems.latest)
        assertTrue(feed.newsItems.hasItem(expectedItem1))

        val expectedItem2 = NewsItem("http://liftoff.msfc.nasa.gov/2003/05/30.html#item572",
                "Sky watchers in Europe, Asia, and parts of Alaska and Canada will experience a <a href=\"http://science.nasa.gov/headlines/y2003/30may_solareclipse.htm\">partial eclipse of the Sun</a> on Saturday, May 31st.",
                toZonedDateTime("Fri, 30 May 2003 11:06:42 GMT"))
        assertTrue(feed.newsItems.hasItem(expectedItem2))

        val expectedItem3 = NewsItem("http://liftoff.msfc.nasa.gov/2003/05/27.html#item571", "The Engine That Does More", toZonedDateTime("Tue, 27 May 2003 08:37:32 GMT"))
        assertTrue(feed.newsItems.hasItem(expectedItem3))

        val expectedItem4 = NewsItem("http://liftoff.msfc.nasa.gov/2003/05/20.html#item570", "Astronauts' Dirty Laundry", toZonedDateTime("Tue, 20 May 2003 08:56:02 GMT"))
        assertTrue(feed.newsItems.hasItem(expectedItem4))
    }

    @Test
    fun `honors ignore before`() {
        val xml = File("src/test/resources/sample-rss-2.xml").readText(Charsets.UTF_8)
        val rssParser = RssParser()
        val feed = rssParser.parse(xml, ZonedDateTime.parse("2003-05-30T11:06:42Z"))

        assertEquals(1, feed.newsItems.size)
        val expectedItem1 = NewsItem("http://liftoff.msfc.nasa.gov/2003/06/03.html#item573", "Star City", toZonedDateTime("Tue, 03 Jun 2003 09:39:21 GMT"))
        assertEquals(expectedItem1, feed.newsItems.latest)
        assertTrue(feed.newsItems.hasItem(expectedItem1))
    }

    private fun toZonedDateTime(str: String): ZonedDateTime = ZonedDateTime.parse(str, DateTimeFormatter.RFC_1123_DATE_TIME)
}