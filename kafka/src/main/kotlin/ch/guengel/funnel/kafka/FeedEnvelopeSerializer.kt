package ch.guengel.funnel.kafka

import io.quarkus.kafka.client.serialization.ObjectMapperSerializer

class FeedEnvelopeSerializer : ObjectMapperSerializer<KafkaFeedEnvelope>()
