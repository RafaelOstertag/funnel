package ch.guengel.funnel.chronos

import ch.guengel.funnel.persistence.FeedEnvelopeRepository
import org.slf4j.LoggerFactory
import java.util.*

class FeedEnvelopSenderTask(private val repository: FeedEnvelopeRepository, private val sender: Sender) : TimerTask() {
    override fun run() {
        logger.debug("Execute scheduled task")
        repository.retrieveAll().let { sender.send(it) }
    }

    companion object {
        val logger = LoggerFactory.getLogger(FeedEnvelopSenderTask::class.java)
    }
}