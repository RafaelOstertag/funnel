package ch.guengel.funnel.persistence

import ch.guengel.funnel.domain.FeedEnvelope

class InMemoryFeedEnvelopeRepository : FeedEnvelopeRepository {
    private val store: MutableMap<String, FeedEnvelope> = HashMap()

    override fun retrieveAll(): List<FeedEnvelope> {
        return store.values.toList()
    }

    override fun retrieveById(name: String): FeedEnvelope {
        return store[name] ?: throw FeedEnvelopeNotFound("Feed Envelope with name '${name}' not found")
    }

    override fun save(feedEnvelope: FeedEnvelope) {
        store[feedEnvelope.name] = feedEnvelope
    }


}

class FeedEnvelopeNotFound(message: String) : Exception(message)
