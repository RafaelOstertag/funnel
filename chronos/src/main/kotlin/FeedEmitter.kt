package ch.guengel.funnel.chronos

import bridges.FeedPersistence
import kafka.Producer
import kafka.allFeedTopics
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class FeedEmitter(private val feedPersistence: FeedPersistence, kafkaServer: String) : AutoCloseable {
    private val producer = Producer(kafkaServer)
    private var closed = false

    fun emitAll() {
        check(!closed) {
            val errorMessage = "FeedEmitter closed"
            logger.error(errorMessage)
            errorMessage
        }

        feedPersistence.findAllFeedEnvelopes().forEach {
            producer.send(allFeedTopics, it)
            logger.debug("Emitted '${it.name}'")
        }
    }

    override fun close() {
        producer.close()
    }

    private companion object {
        var logger: Logger = LoggerFactory.getLogger(FeedEmitter::class.java)
    }
}