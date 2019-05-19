package ch.guengel.funnel.persistence

import ch.guengel.funnel.domain.FeedEnvelope

interface FeedEnvelopeRepository {
    fun retrieveAll(): List<FeedEnvelope>
    fun retrieveById(name: String): FeedEnvelope
    fun save(feedEnvelope: FeedEnvelope)
    fun deleteById(name: String)
}