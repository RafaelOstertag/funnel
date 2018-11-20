package ch.guengel.funnel

import ch.guengel.funnel.domain.Feed
import ch.guengel.funnel.domain.FeedEnvelope
import ch.guengel.funnel.domain.Source
import ch.guengel.funnel.persistence.FeedEnvelopeRepository
import ch.guengel.funnel.persistence.InMemoryFeedEnvelopeRepository
import ch.guengel.funnel.xmlfeeds.XmlFeedRetriever
import ch.guengel.funnel.xmlfeeds.network.HttpTransport

class Funnel {
    private val feedEnvelopeRepository: FeedEnvelopeRepository = InMemoryFeedEnvelopeRepository()

    fun update() {
        for (feedEnvelope in feedEnvelopeRepository.retrieveAll()) {
            val transport = HttpTransport(feedEnvelope.source)
            val feedRetriever = XmlFeedRetriever(transport)

            val currentFeed = feedRetriever.retrieve(feedEnvelope.lastUpdated)
            for (feedItem in currentFeed.feedItems.items) {
                println("Got ${feedEnvelope.name}: ${feedItem.title}")
            }

            feedEnvelope.feed.mergeWith(currentFeed)
            feedEnvelopeRepository.save(feedEnvelope)
        }
    }

    fun addNewSource(name: String, address: String) {
        feedEnvelopeRepository.save(FeedEnvelope(Source(name, address), Feed.empty()))
    }

}

fun main(args: Array<String>) {
    val funnel = Funnel()

    funnel.addNewSource("Fowler", "https://martinfowler.com/feed.atom")
    funnel.addNewSource("Baeldung", "https://feeds.feedburner.com/Baeldung")

    while (true) {
        funnel.update()
        Thread.sleep(10000)
    }
}