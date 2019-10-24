package ch.guengel.funnel.feed.logic

import ch.guengel.funnel.feed.data.FeedItem
import java.time.OffsetDateTime

internal val now = OffsetDateTime.now()

internal fun makeFeedItems(amount: Long): List<FeedItem> {
    val mutableList = mutableListOf<FeedItem>()
    for (number in amount downTo 1) {
        val item = FeedItem("$number", "title ${number}", now.minusDays(amount - number))
        mutableList.add(item)
    }

    return mutableList
}

