package feed.logic

import ch.guengel.funnel.feed.data.Feed
import ch.guengel.funnel.feed.data.FeedEnvelope
import ch.guengel.funnel.feed.logic.FeedEnvelopeDifference

class FeedEnvelopeUpdateNotifier(
    private val feedEnvelopeDifference: FeedEnvelopeDifference,
    private val block: (feedEnvelope: FeedEnvelope) -> Unit
) {
    fun notify(currentFeedEnvelope: FeedEnvelope, latestFeedEnvelope: FeedEnvelope) {
        feedEnvelopeDifference
            .difference(currentFeedEnvelope, latestFeedEnvelope)
            .apply {
                if (feed != Feed()) {
                    block(this)
                }
            }
    }

}