package ch.guengel.funnel.kafka

import ch.guengel.funnel.feed.data.Feed
import ch.guengel.funnel.feed.data.FeedEnvelope
import ch.guengel.funnel.feed.data.FeedItem
import ch.guengel.funnel.feed.data.FeedItems
import ch.guengel.funnel.feed.data.Source
import ch.guengel.funnel.feed.data.User

fun FeedEnvelope.toKafkaFeedEnvelope() = KafkaFeedEnvelope().also {
    it.user = user.toKafkaUser()
    it.source = source.toKafkaSource()
    it.feed = feed.toKafkaFeed()
}

private fun Feed.toKafkaFeed(): KafkaFeed = KafkaFeed().also {
    it.id = id
    it.title = title
    it.feedItems = feedItems.toKafkaFeedItems()
}

private fun FeedItems.toKafkaFeedItems(): KafkaFeedItems = KafkaFeedItems().also { it.items = items.toKafkaFeedItems() }
private fun Set<FeedItem>.toKafkaFeedItems(): Set<KafkaFeedItem> = map {
    KafkaFeedItem().apply {
        id = it.id
        title = it.title
        link = it.link
        created = it.created
    }
}.toHashSet()

private fun User.toKafkaUser(): KafkaUser = KafkaUser().also {
    it.userId = userId
    it.email = email
}

private fun Source.toKafkaSource(): KafkaSource = KafkaSource().also {
    it.name = name
    it.address = address
}

fun KafkaFeedEnvelope.toFeedEnvelope() = FeedEnvelope(user.toUser(), source.toSource(), feed.toFeed())
private fun KafkaFeed.toFeed(): Feed = Feed(id, title, feedItems.toFeedItems())
private fun KafkaFeedItems.toFeedItems(): FeedItems = FeedItems(items.toFeedItems())
private fun Set<KafkaFeedItem>.toFeedItems(): Set<FeedItem> =
    map { FeedItem(it.id, it.title, it.link, it.created) }.toHashSet()

private fun KafkaSource.toSource(): Source = Source(name, address)
private fun KafkaUser.toUser(): User = User(userId, email)

