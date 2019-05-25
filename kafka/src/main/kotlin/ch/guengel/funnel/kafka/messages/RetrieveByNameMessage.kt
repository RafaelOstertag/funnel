package ch.guengel.funnel.kafka.messages

data class RetrieveByNameMessage(val feedName: String, val replyToTopic: String)