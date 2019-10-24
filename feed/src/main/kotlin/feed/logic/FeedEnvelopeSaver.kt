package ch.guengel.funnel.feed.logic

import ch.guengel.funnel.feed.bridges.FeedEnvelopePersistence
import ch.guengel.funnel.feed.data.FeedEnvelope

class FeedEnvelopeSaver(
    private val feedEnvelopePersistence: FeedEnvelopePersistence,
    private val feedEnvelopeTrimmer: FeedEnvelopeTrimmer
) {
    fun save(feedEnvelope: FeedEnvelope) {
        val trimmedFeed = feedEnvelopeTrimmer.trim(feedEnvelope)
        feedEnvelopePersistence.saveFeedEnvelope(trimmedFeed)
    }
}