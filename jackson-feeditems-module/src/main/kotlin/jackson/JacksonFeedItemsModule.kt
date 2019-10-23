package jackson

import com.fasterxml.jackson.databind.module.SimpleModule
import data.FeedItems

fun jacksonFeedItemsModule(): SimpleModule {
    val feedItemsModule = SimpleModule()
    feedItemsModule.addSerializer(FeedItemsSerializer())
    feedItemsModule.addDeserializer(FeedItems::class.java, FeedItemsDeserializer())
    return feedItemsModule
}