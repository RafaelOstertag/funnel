package ch.guengel.funnel.chronos

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class Scheduler(private val interval: Long, private val feedEmitter: FeedEmitter) : AutoCloseable {
    private val scheduledThreadPoolExecutor = ScheduledThreadPoolExecutor(1)
    private var closed = false

    fun start() {
        check(!closed) {
            val errorMessage = "Cannot re-start Scheduler"
            logger.error(errorMessage)
            errorMessage
        }

        scheduledThreadPoolExecutor.scheduleAtFixedRate(this::emitAllTask, 0, interval, TimeUnit.SECONDS)
        logger.info("Emit all feeds scheduled to run every {} seconds", interval)
    }

    private fun emitAllTask() {
        try {
            logger.info("Emitting all feeds")
            feedEmitter.emitAll()
        } catch (e: Exception) {
            logger.error("Error while executing emitAllTask", e)
        }
    }

    override fun close() {
        scheduledThreadPoolExecutor.shutdown()
        closed = true
    }

    private companion object {
        val logger: Logger = LoggerFactory.getLogger(Scheduler::class.java)
    }
}