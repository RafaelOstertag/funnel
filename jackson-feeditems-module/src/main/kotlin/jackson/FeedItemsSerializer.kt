package jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import data.FeedItems

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