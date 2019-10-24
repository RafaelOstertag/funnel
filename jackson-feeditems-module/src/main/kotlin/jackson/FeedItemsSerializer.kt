package ch.guengel.funnel.jackson

import ch.guengel.funnel.feed.data.FeedItems
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer

class FeedItemsSerializer : StdSerializer<FeedItems>(FeedItems::class.java) {
    override fun serialize(value: FeedItems?, gen: JsonGenerator, provider: SerializerProvider) {
        if (value == null) {
            gen.writeNull()
            return
        }

        gen.writeStartArray()
        value.items.forEach { feedItem -> gen.writeObject(feedItem) }
        gen.writeEndArray()
    }
}