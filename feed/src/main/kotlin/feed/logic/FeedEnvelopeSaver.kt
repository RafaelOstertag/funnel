package ch.guengel.funnel.feed.logic

import ch.guengel.funnel.feed.bridges.FeedPersistence
import ch.guengel.funnel.feed.data.FeedEnvelope

class FeedEnvelopeSaver(
    private val feedPersistence: FeedPersistence,
    private val feedEnvelopeTrimmer: FeedEnvelopeTrimmer
) {
    fun save(feedEnvelope: FeedEnvelope) {
        val trimmedFeed = feedEnvelopeTrimmer.trim(feedEnvelope)
        feedPersistence.saveFeedEnvelope(trimmedFeed)
    }
}