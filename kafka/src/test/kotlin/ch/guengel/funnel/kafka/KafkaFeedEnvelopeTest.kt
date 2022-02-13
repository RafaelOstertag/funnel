package ch.guengel.funnel.kafka

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata
import org.jeasy.random.EasyRandom
import org.junit.jupiter.api.Test

internal class KafkaFeedEnvelopeTest {
    private val easyRandom = EasyRandom()

    @Test
    fun shouldGenerateMessageKey() {
        val kafkaFeedEnvelope = easyRandom.nextObject(KafkaFeedEnvelope::class.java)

        val messageKey = kafkaFeedEnvelope.getKey()
        assertThat(messageKey).isEqualTo("${kafkaFeedEnvelope.user.userId}#${kafkaFeedEnvelope.source.name}")
    }

    @Test
    fun shouldCreateMessage() {
        val kafkaFeedEnvelope = easyRandom.nextObject(KafkaFeedEnvelope::class.java)

        val message = kafkaFeedEnvelope.asMessage()
        assertThat(message.payload).isEqualTo(kafkaFeedEnvelope)

        val metadata = message.metadata[OutgoingKafkaRecordMetadata::class.java]
        assertThat(metadata.isPresent).isTrue()
        assertThat(metadata.get().key).isEqualTo("${kafkaFeedEnvelope.user.userId}#${kafkaFeedEnvelope.source.name}")
    }
}
