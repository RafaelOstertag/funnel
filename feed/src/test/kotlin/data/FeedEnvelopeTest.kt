package data

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

internal class FeedEnvelopeTest {

    @Test
    fun `envelope name`() {
        val source = Source("name", "address")
        val feedEnvelope = FeedEnvelope(source, Feed())
        assertThat(feedEnvelope.name).isEqualTo("name")
    }
}