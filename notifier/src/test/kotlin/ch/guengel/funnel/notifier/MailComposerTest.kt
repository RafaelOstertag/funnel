package ch.guengel.funnel.notifier

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import ch.guengel.funnel.testutils.makeFeed
import ch.guengel.funnel.testutils.makeFeedEnvelope
import ch.guengel.funnel.testutils.makeSource
import org.junit.jupiter.api.Test

internal class MailComposerTest {
    private val mailComposer: MailComposer = MailComposer()

    @Test
    fun shouldComposeMail() {
        val feedEnvelope = makeFeedEnvelope("test", makeSource(1), makeFeed("test-id", "test", 3))
        val mail = mailComposer.composeFromFeedEnvelope(feedEnvelope)

        assertThat(mail.subject).isEqualTo("[Funnel] sourceName 1")
        assertThat(mail.html).isEqualTo("Updates for <a href=\"sourceAddress 1\">test</a>\n" +
                "<ul>\n" +
                "<li><a href=\"Link 1\">Item 1</a></li>\n" +
                "<li><a href=\"Link 2\">Item 2</a></li>\n" +
                "<li><a href=\"Link 3\">Item 3</a></li>\n" +
                "</ul>")
        assertThat(mail.to).containsExactly("test@example.com")
    }
}
