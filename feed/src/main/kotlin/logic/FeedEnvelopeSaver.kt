package logic

import bridges.FeedPersistence
import data.FeedEnvelope

class FeedEnvelopeSaver(
    private val feedPersistence: FeedPersistence,
    private val feedEnvelopeTrimmer: FeedEnvelopeTrimmer
) {
    fun save(feedEnvelope: FeedEnvelope) {
        val trimmedFeed = feedEnvelopeTrimmer.trim(feedEnvelope)
        feedPersistence.saveFeedEnvelope(trimmedFeed)
    }
}