package ch.guengel.funnel.notifier

import ch.guengel.funnel.kafka.KafkaFeedEnvelope
import ch.guengel.funnel.kafka.toFeedEnvelope
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.jeasy.random.EasyRandom
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class NotifierTest {
    private val easyRandom = EasyRandom()

    private lateinit var smtpServiceMock: SmtpService
    private lateinit var notifier: Notifier

    @BeforeEach
    fun beforeEach() {
        smtpServiceMock = mockk()
        notifier = Notifier(smtpServiceMock)
    }

    @Test
    fun shouldReceiveUpdate() {
        every { smtpServiceMock.sendMail(any()) } just runs

        val kafkaFeedEnvelope = easyRandom.nextObject(KafkaFeedEnvelope::class.java)
        notifier.receiveUpdate(kafkaFeedEnvelope)

        verify { smtpServiceMock.sendMail(kafkaFeedEnvelope.toFeedEnvelope()) }
    }
}
