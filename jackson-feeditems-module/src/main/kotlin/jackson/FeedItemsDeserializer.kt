package ch.guengel.funnel.jackson

import ch.guengel.funnel.feed.data.FeedItem
import ch.guengel.funnel.feed.data.FeedItems
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer

class FeedItemsDeserializer : StdDeserializer<FeedItems>(FeedItems::class.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): FeedItems {
        val jsonNode = p.codec.readTree<JsonNode>(p)
        val feedItems = mutableListOf<FeedItem>()
        for (child in jsonNode.elements()) {
            val feedItem = p.codec.treeToValue(child, FeedItem::class.java)
            feedItems.add(feedItem)
        }

        return FeedItems(feedItems)
    }
}