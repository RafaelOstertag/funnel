package ch.guengel.funnel.jackson

import ch.guengel.funnel.feed.data.FeedItems
import com.fasterxml.jackson.databind.module.SimpleModule

fun jacksonFeedItemsModule(): SimpleModule {
    val feedItemsModule = SimpleModule()
    feedItemsModule.addSerializer(FeedItemsSerializer())
    feedItemsModule.addDeserializer(FeedItems::class.java, FeedItemsDeserializer())
    return feedItemsModule
}