package ch.guengel.funnel.persistence

import ch.guengel.funnel.domain.FeedEnvelope

interface FeedEnvelopeRepository {
    fun retrieveAll(): List<FeedEnvelope>
    fun retrieveByName(name: String): FeedEnvelope?
    fun save(feedEnvelope: FeedEnvelope)
    fun deleteByName(name: String)
    fun getAllFeedNames(): List<String>
}