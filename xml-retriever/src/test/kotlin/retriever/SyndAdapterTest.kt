package retriever

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
    }

    @Test
    fun `sample rss 2`() {
        val rssFeed = this::class.java.getResource("/sample-rss-2.xml").readText()
        val feed = syndAdapter.toFeed(rssFeed)
        assertThat(feed.feedItems.size).isEqualTo(3)
    }

    @Test
    fun `feed burner rss 2_0`() {
        val rssFeed = this::class.java.getResource("/feed-burner.xml").readText()
        val feed = syndAdapter.toFeed(rssFeed)
        assertThat(feed.feedItems.size).isEqualTo(12)
    }
}