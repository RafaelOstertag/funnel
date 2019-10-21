package data

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

object FeedConstants {
    val emptyCreated: ZonedDateTime =
        ZonedDateTime.of(
            LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC),
            ZoneId.of("UTC")
        )
}