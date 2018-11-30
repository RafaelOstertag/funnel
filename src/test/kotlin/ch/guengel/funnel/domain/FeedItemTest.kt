package ch.guengel.funnel.domain

import org.junit.jupiter.api.Test
import java.time.ZonedDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class FeedItemTest {

    @Test
    fun `equals and hashCode ignore title`() {
        val item1 = FeedItem("id", "title", ZonedDateTime.parse("2018-10-10T13:00:00+02:00"))
        val item2 = FeedItem("id", "title2", ZonedDateTime.parse("2018-10-10T13:00:00+02:00"))

        assertEquals(item1, item2)
        assertTrue(item1.hashCode() == item2.hashCode())
    }

    @Test
    fun `equals respects id`() {
        val item1 = FeedItem("id2", "title", ZonedDateTime.parse("2018-10-10T13:00:00+02:00"))
        val item2 = FeedItem("id", "title", ZonedDateTime.parse("2018-10-10T13:00:00+02:00"))

        assertNotEquals(item1, item2)
    }

    @Test
    fun `equals respects created date`() {
        val item1 = FeedItem("id", "title", ZonedDateTime.parse("2018-10-10T13:00:00+01:00"))
        val item2 = FeedItem("id", "title", ZonedDateTime.parse("2018-10-10T13:00:00+02:00"))

        assertNotEquals(item1, item2)
    }

    @Test
    fun `compareTo handles equality properly`() {
        val item1 = FeedItem("id", "title", ZonedDateTime.parse("2018-10-10T11:00:00Z"))
        val item2 = FeedItem("id", "title2", ZonedDateTime.parse("2018-10-10T13:00:00+02:00"))

        assertEquals(item1.compareTo(item2), 0)
    }

    @Test
    fun `compareTo uses created date`() {
        val item1 = FeedItem("id2", "title", ZonedDateTime.parse("2018-10-10T13:00:00+02:00"))
        val item2 = FeedItem("id", "title2", ZonedDateTime.parse("2018-10-10T13:00:00+02:00"))
        val item3 = FeedItem("id", "title2", ZonedDateTime.parse("2018-10-10T12:00:00+02:00"))

        assertEquals(item1.compareTo(item2), 0)

        assertTrue(item2.compareTo(item3) > 0)
        assertTrue(item3.compareTo(item2) < 0)
    }

}