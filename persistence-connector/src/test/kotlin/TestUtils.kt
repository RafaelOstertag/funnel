package ch.guengel.funnel.persistence.connector

import ch.guengel.funnel.domain.*
import java.time.ZonedDateTime

fun makeItem(number: Int): FeedItem {
    return FeedItem(
            "item${number}",
            "Item ${number}",
            ZonedDateTime.parse("2018-10-0${number}T13:00:00+02:00")
    )
}

fun makeFeedItems(numberOfItems: Int): FeedItems {
    val feedItems = FeedItems(numberOfItems)
    (1..numberOfItems).forEach {
        feedItems.add(makeItem(it))
    }

    return feedItems
}

fun makeFeed(id: String, title: String, numberOfItems: Int) =
        Feed(id, title, makeFeedItems(numberOfItems))

fun makeSource(sourceNumber: Int): Source =
        Source("sourceName $sourceNumber", "sourceAddress $sourceNumber")

fun makeFeedEnvelope(source: Source, feed: Feed): FeedEnvelope {
    return FeedEnvelope(source, feed)
}