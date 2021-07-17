package ch.guengel.funnel.connector.xmlretriever.http

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

internal class SyndAdapterTest {
    val syndAdapter = SyndAdapter()

    @Test
    fun `sample atom feed`() {
        val atomFeed = this::class.java.getResource("/atom-feed.xml").readText()
        val feed = syndAdapter.toFeed(atomFeed)
        assertThat(feed.feedItems.size).isEqualTo(1)

        with(feed.feedItems.latest) {
            assertThat(id).isEqualTo("urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a")
            assertThat(title).isEqualTo("Atom-Powered Robots Run Amok")
            assertThat(link).isEqualTo("http://example.org/2003/12/13/atom03")
        }
    }

    @Test
    fun `sample rss 2`() {
        val rssFeed = this::class.java.getResource("/sample-rss-2.xml").readText()
        val feed = syndAdapter.toFeed(rssFeed)
        assertThat(feed.feedItems.size).isEqualTo(3)

        with(feed.feedItems.latest) {
            assertThat(id).isEqualTo("http://liftoff.msfc.nasa.gov/2003/06/03.html#item573")
            assertThat(title).isEqualTo("Star City")
            assertThat(link).isEqualTo("http://liftoff.msfc.nasa.gov/news/2003/news-starcity.asp")
        }
    }

    @Test
    fun `feed burner rss 2_0`() {
        val rssFeed = this::class.java.getResource("/feed-burner.xml").readText()
        val feed = syndAdapter.toFeed(rssFeed)
        assertThat(feed.feedItems.size).isEqualTo(12)
    }
}
