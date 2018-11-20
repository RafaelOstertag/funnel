package ch.guengel.funnel.domain

import java.time.ZonedDateTime

fun makeItem(number: Int): FeedItem {
    return FeedItem(
            "item${number}",
            "Item ${number}",
            ZonedDateTime.parse("2018-10-0${number}T13:00:00+02:00")
    )
}