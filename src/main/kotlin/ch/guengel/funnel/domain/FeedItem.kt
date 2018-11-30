package ch.guengel.funnel.domain

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

data class FeedItem(val id: String,
                    val title: String,
                    val created: ZonedDateTime = emptyCreated) : Comparable<FeedItem> {


    companion object {
        val emptyCreated =
                ZonedDateTime.of(
                        LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC),
                        ZoneId.of("UTC"))
        private val emptyItem = FeedItem("", "", emptyCreated)
        fun empty() = emptyItem
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FeedItem

        if (id != other.id) return false
        if (created.toEpochSecond() != other.created.toEpochSecond()) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + created.toEpochSecond().hashCode()
        return result
    }

    override fun compareTo(other: FeedItem): Int {
        if (equals(other)) {
            return 0
        }

        val delta = created.toEpochSecond() - other.created.toEpochSecond()

        return if (delta > 0) {
            1
        } else if (delta < 0) {
            -1
        } else {
            0
        }
    }
}