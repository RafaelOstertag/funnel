package ch.guengel.funnel.kafka

import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata
import org.eclipse.microprofile.reactive.messaging.Message
import java.time.OffsetDateTime

class KafkaFeedEnvelope {
    lateinit var user: KafkaUser
    lateinit var source: KafkaSource
    lateinit var feed: KafkaFeed
}

fun KafkaFeedEnvelope.getKey() = "${user.userId}#${source.name}"

fun KafkaFeedEnvelope.asMessage(): Message<KafkaFeedEnvelope> {
    val metadata = OutgoingKafkaRecordMetadata.builder<String>()
        .withKey(getKey()).build()
    return Message.of(this).addMetadata(metadata)
}

class KafkaSource {
    lateinit var name: String
    lateinit var address: String
}

class KafkaUser {
    lateinit var userId: String
    lateinit var email: String
}

class KafkaFeed {
    lateinit var id: String
    lateinit var title: String
    lateinit var feedItems: KafkaFeedItems
}

class KafkaFeedItems {
    lateinit var items: Set<KafkaFeedItem>
}

class KafkaFeedItem {
    lateinit var id: String
    lateinit var title: String
    lateinit var link: String
    lateinit var created: OffsetDateTime
}
