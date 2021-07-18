package ch.guengel.funnel.connector.xmlretriever

import ch.guengel.funnel.connector.xmlretriever.http.XmlRetriever
import ch.guengel.funnel.feed.bridges.FeedEnvelopePersistence
import ch.guengel.funnel.feed.data.FeedEnvelope
import ch.guengel.funnel.feed.logic.FeedEnvelopeDifference
import ch.guengel.funnel.feed.logic.FeedEnvelopeMerger
import ch.guengel.funnel.feed.logic.FeedEnvelopeRetriever
import ch.guengel.funnel.feed.logic.FeedEnvelopeSaver
import ch.guengel.funnel.feed.logic.FeedEnvelopeTrimmer
import ch.guengel.funnel.feed.logic.FeedEnvelopeUpdateNotifier
import ch.guengel.funnel.kafka.KafkaFeedEnvelope
import ch.guengel.funnel.kafka.asMessage
import ch.guengel.funnel.kafka.toKafkaFeedEnvelope
import io.quarkus.scheduler.Scheduled
import io.smallrye.reactive.messaging.annotations.Broadcast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Emitter
import org.jboss.logging.Logger
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class XmlFeedRetriever(
    @Inject private val xmlRetriever: XmlRetriever,
    @Inject private val feedEnvelopePersistence: FeedEnvelopePersistence,
) {
    @Inject
    @Channel("notify-out")
    @Broadcast
    private lateinit var feedNotification: Emitter<KafkaFeedEnvelope>

    @Inject
    @Channel("update-out")
    @Broadcast
    private lateinit var feedEnvelopeUpdate: Emitter<KafkaFeedEnvelope>

    private val feedEnvelopRetriever: FeedEnvelopeRetriever = FeedEnvelopeRetriever(xmlRetriever)
    private val feedEnvelopeMerger: FeedEnvelopeMerger = FeedEnvelopeMerger()
    private val feedEnvelopeSaver: FeedEnvelopeSaver =
        FeedEnvelopeSaver(feedEnvelopePersistence, FeedEnvelopeTrimmer(15))
    private val feedEnvelopeUpdateNotifier: FeedEnvelopeUpdateNotifier =
        FeedEnvelopeUpdateNotifier(FeedEnvelopeDifference()) {
            logger.infof("Send notification for '%s' for user '%s'", it.source.name, it.user.userId)
            notify(it)
        }

    private fun notify(feedEnvelope: FeedEnvelope) {
        val kafkaMessage = feedEnvelope.toKafkaFeedEnvelope().asMessage()
        feedNotification.send(kafkaMessage)
    }

    private fun sendFeedEnvelopeUpdate(feedEnvelope: FeedEnvelope) {
        val kafkaMessage = feedEnvelope.toKafkaFeedEnvelope().asMessage()
        feedEnvelopeUpdate.send(kafkaMessage)
    }

    @Scheduled(every = "{funnel.xmlretriever.schedule}")
    fun updateFeeds() {
        logger.info("Update all feeds")
        feedEnvelopePersistence.findAllFeedEnvelopes().chunked(5)
            .forEach { feedEnvelopesChunk ->
                receiveList(feedEnvelopesChunk)
            }
    }

    private fun receiveList(feedEnvelopesChunk: List<FeedEnvelope>) {
        runBlocking {
            feedEnvelopesChunk.map { currentFeedEnvelope ->
                retrieveAndUpdateFeedEnvelope(currentFeedEnvelope)
            }.joinAll()
        }
    }

    private fun retrieveAndUpdateFeedEnvelope(currentFeedEnvelope: FeedEnvelope) =
        CoroutineScope(Dispatchers.IO).launch {
            logger.infof("Retrieve and update '%s' for user '%s'",
                currentFeedEnvelope.name,
                currentFeedEnvelope.user.userId)
            val latestFeedEnvelope =
                feedEnvelopRetriever.retrieve(currentFeedEnvelope.user.userId,
                    currentFeedEnvelope.source)

            feedEnvelopeUpdateNotifier.notify(currentFeedEnvelope, latestFeedEnvelope)
            feedEnvelopeMerger.merge(currentFeedEnvelope, latestFeedEnvelope).let {
                feedEnvelopeSaver.save(it)
                sendFeedEnvelopeUpdate(it)
            }
        }

    private companion object {
        private val logger: Logger = Logger.getLogger(XmlRetriever::class.java)
    }

}
