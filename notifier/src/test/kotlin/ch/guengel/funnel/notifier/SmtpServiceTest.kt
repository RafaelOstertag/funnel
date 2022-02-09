package ch.guengel.funnel.notifier

import ch.guengel.funnel.testutils.makeFeed
import ch.guengel.funnel.testutils.makeFeedEnvelope
import ch.guengel.funnel.testutils.makeSource
import io.mockk.every
import io.mockk.mockk
import io.quarkus.mailer.Mail
import io.quarkus.mailer.reactive.ReactiveMailer
import io.smallrye.mutiny.TimeoutException
import io.smallrye.mutiny.Uni
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration

internal class SmtpServiceTest {
    private lateinit var reactiveMailerMock: ReactiveMailer
    private lateinit var mailComposerMock: MailComposer
    private lateinit var smtpService: SmtpService

    @BeforeEach
    fun beforeEach() {
        reactiveMailerMock = mockk()
        mailComposerMock = mockk()
        smtpService = SmtpService(reactiveMailerMock, mailComposerMock)
    }

    @Test
    fun shouldSendMail() {
        val feedEnvelope = makeFeedEnvelope("test", makeSource(1), makeFeed("test-id", "test", 3))
        val mail = Mail.withHtml("test@example.com", "subject", "html")

        every { mailComposerMock.composeFromFeedEnvelope(feedEnvelope) } returns mail

        val mailUni: Uni<Void> = Uni.createFrom().nullItem()
        every { reactiveMailerMock.send(mail) } returns mailUni

        smtpService.sendMail(feedEnvelope)

        try {
            mailUni.onSubscription().call { _ -> Uni.createFrom().nullItem<Void>() }
                .await().atMost(Duration.ofSeconds(1))
        } catch (ex: TimeoutException) {
            System.err.println(ex)
            fail("Uni subscription timed out")
        }
    }


}
