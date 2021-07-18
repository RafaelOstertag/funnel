package ch.guengel.funnel.kafka

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer

class FeedEnvelopeDeserializer : ObjectMapperDeserializer<KafkaFeedEnvelope>(KafkaFeedEnvelope::class.java)
