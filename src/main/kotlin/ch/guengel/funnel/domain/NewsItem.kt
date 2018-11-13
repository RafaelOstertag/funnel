package ch.guengel.funnel.domain

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

data class NewsItem(val id: String,
                    val title: String,
                    val created: ZonedDateTime = emptyCreated) : Comparable<NewsItem> {


    companion object {
        val emptyCreated =
                ZonedDateTime.of(
                        LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC),
                        ZoneId.of("UTC"))
        private val emptyItem = NewsItem("", "", emptyCreated)
        fun empty() = emptyItem
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NewsItem

        if (id != other.id) return false
        if (created != other.created) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + created.hashCode()
        return result
    }

    override fun compareTo(other: NewsItem): Int {
        if (equals(other)) {
            return 0
        }

        return created.compareTo(other.created)
    }
}