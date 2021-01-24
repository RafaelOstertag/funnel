package ch.guengel.funnel.feed.data

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

internal class FeedEnvelopeTest {

    @Test
    fun `envelope name`() {
        val source = Source("name", "address")
        val user = User("user-id", "email")
        val feedEnvelope = FeedEnvelope(user, source, Feed())
        assertThat(feedEnvelope.name).isEqualTo("name")
    }
}