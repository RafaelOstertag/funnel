package logic

import data.FeedItem
import java.time.ZonedDateTime

internal val now = ZonedDateTime.now()

internal fun makeFeedItems(amount: Long): List<FeedItem> {
    val mutableList = mutableListOf<FeedItem>()
    for (number in amount downTo 1) {
        val item = FeedItem("$number", "title ${number}", now.minusDays(amount - number))
        mutableList.add(item)
    }

    return mutableList
}

