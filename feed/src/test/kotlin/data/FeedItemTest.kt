package data

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import assertk.assertions.isLessThan
import assertk.assertions.isNotEqualTo
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime

internal class FeedItemTest {
    val now = OffsetDateTime.now()

    @Test
    fun `are equal`() {


        val item1 = FeedItem("id", "title 1", now)
        val item2 = FeedItem("id", "title 2", now)

        assertThat(item1).isEqualTo(item2)
    }

    @Test
    fun `are not equal by id`() {
        val item1 = FeedItem("id1", "", now)
        val item2 = FeedItem("id2", "", now)

        assertThat(item1).isNotEqualTo(item2)
    }

    @Test
    fun `are not equal by created date`() {
        val time2 = now.plusDays(1)

        val item1 = FeedItem("id1", "", now)
        val item2 = FeedItem("id1", "", time2)

        assertThat(item1).isNotEqualTo(item2)
    }

    @Test
    fun `equal hash value`() {
        val item1 = FeedItem("id", "title 1", now)
        val item2 = FeedItem("id", "title 2", now)

        assertThat(item1.hashCode()).isEqualTo(item2.hashCode())
    }

    @Test
    fun `non-equal hash value`() {
        val item1 = FeedItem("id1", "title 1", now)
        val item2 = FeedItem("id2", "title 2", now)

        assertThat(item1.hashCode()).isNotEqualTo(item2.hashCode())
    }

    @Test
    fun `compare equal feed items`() {
        val item1 = FeedItem("id", "title 1", now)
        val item2 = FeedItem("id", "title 2", now)

        assertThat(item1.compareTo(item2)).isEqualTo(0)
    }

    @Test
    fun `compare non-equal feed items`() {
        val time2 = now.plusDays(1)

        val item1 = FeedItem("id", "title 1", now)
        val item2 = FeedItem("id", "title 2", time2)

        assertThat(item1.compareTo(item2)).isLessThan(0)
        assertThat(item2.compareTo(item1)).isGreaterThan(0)
    }
}