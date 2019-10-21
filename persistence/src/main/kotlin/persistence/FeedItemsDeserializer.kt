package persistence

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import data.FeedItem
import data.FeedItems

internal class FeedItemsDeserializer : StdDeserializer<FeedItems>(FeedItems::class.java) {
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