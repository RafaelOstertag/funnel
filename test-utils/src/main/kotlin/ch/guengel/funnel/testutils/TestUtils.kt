package ch.guengel.funnel.testutils

import ch.guengel.funnel.feed.data.Feed
import ch.guengel.funnel.feed.data.FeedEnvelope
import ch.guengel.funnel.feed.data.FeedItem
import ch.guengel.funnel.feed.data.FeedItems
import ch.guengel.funnel.feed.data.Source
import ch.guengel.funnel.feed.data.User
import java.time.OffsetDateTime

private val createdDate = OffsetDateTime.parse("2018-10-01T13:00:00Z")

fun makeItem(number: Int): FeedItem {
    return FeedItem(
        "item${number}",
        "Item ${number}",
        "Link ${number}",
        createdDate.plusDays(number.toLong())
    )
}

fun makeFeedItems(numberOfItems: Int): FeedItems {
    val feeds = mutableListOf<FeedItem>()
    (1..numberOfItems).forEach {
        feeds.add(makeItem(it))
    }

    return FeedItems(feeds)
}

fun makeFeed(id: String, title: String, numberOfItems: Int) =
    Feed(id, title, makeFeedItems(numberOfItems))

fun makeSource(sourceNumber: Int): Source =
    Source("sourceName $sourceNumber", "sourceAddress $sourceNumber")

fun makeFeedEnvelope(userId: String, source: Source, feed: Feed): FeedEnvelope {
    return FeedEnvelope(User(userId, "$userId@example.com"), source, feed)
}
