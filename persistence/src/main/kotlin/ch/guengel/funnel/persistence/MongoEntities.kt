package ch.guengel.funnel.persistence

import java.time.OffsetDateTime

class MongoFeedEnvelope {
    lateinit var user: MongoUser
    lateinit var source: MongoSource
    lateinit var feed: MongoFeed
}


class MongoSource {
    lateinit var name: String
    lateinit var address: String
}

class MongoUser {
    lateinit var userId: String
    lateinit var email: String
}

class MongoFeed {
    lateinit var id: String
    lateinit var title: String
    lateinit var feedItems: MongoFeedItems
}

class MongoFeedItems {
    lateinit var items: Set<MongoFeedItem>
}

class MongoFeedItem {
    lateinit var id: String
    lateinit var title: String
    lateinit var link: String
    lateinit var created: OffsetDateTime
}
