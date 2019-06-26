package ch.guengel.funnel.persistence

import ch.guengel.funnel.domain.FeedEnvelope
import ch.guengel.funnel.domain.Source

interface FeedEnvelopeRepository {
    fun retrieveAll(): List<FeedEnvelope>
    fun retrieveByName(name: String): FeedEnvelope?
    fun save(feedEnvelope: FeedEnvelope)
    fun deleteByName(name: String)
    fun retrieveAllSources(): List<Source>
}