package logic

import adapters.FeedPersistence
import data.FeedEnvelope

class FeedEnvelopeSaver(
    private val feedPersistence: FeedPersistence,
    private val feedEnvelopeTrimmer: FeedEnvelopeTrimmer,
    private val feedEnvelopeMerger: FeedEnvelopeMerger
) {
    fun save(feedEnvelope: FeedEnvelope) {
        val trimmedFeed = feedEnvelopeTrimmer.trim(feedEnvelope)
        feedPersistence.saveFeedEnvelope(trimmedFeed)
    }

    fun mergeAndSave(feedEnvelope1: FeedEnvelope, feedEnvelope2: FeedEnvelope) {
        val mergedFeedEnvelope = feedEnvelopeMerger.merge(feedEnvelope1, feedEnvelope2)
        save(mergedFeedEnvelope)
    }
}