package ch.guengel.funnel.rest

import ch.guengel.funnel.feed.data.Feed
import ch.guengel.funnel.feed.data.FeedEnvelope
import ch.guengel.funnel.feed.data.FeedItem
import ch.guengel.funnel.feed.data.FeedItems
import ch.guengel.funnel.feed.data.Source
import java.time.format.DateTimeFormatter
import ch.guengel.funnel.rest.model.Feed as FeedDto
import ch.guengel.funnel.rest.model.FeedEnvelope as FeedEnvelopeDto
import ch.guengel.funnel.rest.model.FeedItem as FeedItemDto
import ch.guengel.funnel.rest.model.Source as SourceDto

fun Source.toDto(): SourceDto = SourceDto().also {
    it.name = name
    it.address = address
}

fun SourceDto.toSource(): Source = Source(name, address)

fun FeedEnvelope.toFeedEnvelopeDto(): FeedEnvelopeDto = FeedEnvelopeDto()
    .source(source.toDto())
    .feed(feed.toFeedDto())

fun Feed.toFeedDto(): FeedDto = FeedDto()
    .feedItems(feedItems.toList())
    .lastUpdated(lastUpdated.format(DateTimeFormatter.ISO_DATE_TIME))
    .title(title)

fun FeedItems.toList() = items.map { it.toFeedItemDto() }

fun FeedItem.toFeedItemDto(): FeedItemDto = FeedItemDto().also {
    it.created = created
    it.link = link
    it.title = title
}
