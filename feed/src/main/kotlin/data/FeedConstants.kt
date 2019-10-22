package data

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

object FeedConstants {
    val emptyCreated: OffsetDateTime =
        OffsetDateTime.of(
            LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC),
            ZoneOffset.UTC
        )
}