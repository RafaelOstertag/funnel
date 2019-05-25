package ch.guengel.funnel.kafka

object Topics {
    /**
     * Topic used to persist a Feed
     */
    const val persistFeed = "funnel.feed.persist"
    /**
     * Topic used to publish Feed updates
     */
    const val feedUpdate = "funnel.feed.update"
    const val retrieveAll = "funnel.feed.retrieve.all"
    /**
     * Topic used to delete Feed
     */
    const val feedDelete = "funnel.feed.delete"
    /**
     * Topic used to retrieve all feed names
     */
    const val retrieveAllNames = "funnel.feed.retrieve.all.names"
    /**
     * Topic used to retrieve feed by name
     */
    const val retrieveFeedByName = "funnel.feed.retrieve.by.name"

}