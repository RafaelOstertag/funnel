package ch.guengel.funnel.kafka

object Topics {
    /**
     * Topic used to persist a Feed
     */
    val persistFeed = "funnel.feed.persist"
    /**
     * Topic used to publish Feed updates
     */
    val feedUpdate = "funnel.feed.update"
    val retrieveAll = "funnel.feed.retrieve.all"
}