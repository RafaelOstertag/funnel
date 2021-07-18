package ch.guengel.funnel.feed.logic

import ch.guengel.funnel.feed.data.FeedEnvelope

class FeedEnvelopeUpdateNotifier(
    private val feedEnvelopeDifference: FeedEnvelopeDifference,
    private val block: (feedEnvelope: FeedEnvelope) -> Unit,
) {
    fun notify(currentFeedEnvelope: FeedEnvelope, latestFeedEnvelope: FeedEnvelope) {
        feedEnvelopeDifference
            .difference(currentFeedEnvelope, latestFeedEnvelope)
            .apply {
                if (!feed.feedItems.isEmpty) {
                    block(this)
                }
            }
    }

}
