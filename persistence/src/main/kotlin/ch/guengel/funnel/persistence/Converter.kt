package ch.guengel.funnel.persistence

import ch.guengel.funnel.feed.data.Feed
import ch.guengel.funnel.feed.data.FeedEnvelope
import ch.guengel.funnel.feed.data.FeedItem
import ch.guengel.funnel.feed.data.FeedItems
import ch.guengel.funnel.feed.data.Source
import ch.guengel.funnel.feed.data.User

fun FeedEnvelope.toMongoFeedEnvelope() = MongoFeedEnvelope().also {
    it.user = user.toMongoUser()
    it.source = source.toMongoSource()
    it.feed = feed.toMongoFeed()
}

private fun Feed.toMongoFeed(): MongoFeed = MongoFeed().also {
    it.id = id
    it.title = title
    it.feedItems = feedItems.toMongoFeedItems()
}

private fun FeedItems.toMongoFeedItems(): MongoFeedItems = MongoFeedItems().also { it.items = items.toMongoFeedItems() }
private fun Set<FeedItem>.toMongoFeedItems(): Set<MongoFeedItem> = map {
    MongoFeedItem().apply {
        id = it.id
        title = it.title
        link = it.link
        created = it.created
    }
}.toHashSet()

private fun User.toMongoUser(): MongoUser = MongoUser().also {
    it.userId = userId
    it.email = email
}

private fun Source.toMongoSource(): MongoSource = MongoSource().also {
    it.name = name
    it.address = address
}

fun MongoFeedEnvelope.toFeedEnvelope() = FeedEnvelope(user.toUser(), source.toSource(), feed.toFeed())
private fun MongoFeed.toFeed(): Feed = Feed(id, title, feedItems.toFeedItems())
private fun MongoFeedItems.toFeedItems(): FeedItems = FeedItems(items.toFeedItems())
private fun Set<MongoFeedItem>.toFeedItems(): Set<FeedItem> =
    map { FeedItem(it.id, it.title, it.link, it.created) }.toHashSet()

private fun MongoSource.toSource(): Source = Source(name, address)
private fun MongoUser.toUser(): User = User(userId, email)

