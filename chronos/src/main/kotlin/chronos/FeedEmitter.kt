package ch.guengel.funnel.chronos

import ch.guengel.funnel.feed.bridges.FeedEnvelopePersistence
import ch.guengel.funnel.kafka.Producer
import ch.guengel.funnel.kafka.allFeedTopics
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class FeedEmitter(private val feedEnvelopePersistence: FeedEnvelopePersistence, kafkaServer: String) : AutoCloseable {
    private val producer = Producer(kafkaServer)
    private var closed = false

    fun emitAll() {
        check(!closed) {
            val errorMessage = "FeedEmitter closed"
            logger.error(errorMessage)
            errorMessage
        }

        feedEnvelopePersistence.findAllFeedEnvelopes().forEach {
            producer.send(allFeedTopics, it)
            logger.info("Emitted '${it.name}'")
        }
    }

    override fun close() {
        producer.close()
    }

    private companion object {
        var logger: Logger = LoggerFactory.getLogger(FeedEmitter::class.java)
    }
}