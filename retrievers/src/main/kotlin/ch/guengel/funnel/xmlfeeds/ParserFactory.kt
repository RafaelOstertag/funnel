package ch.guengel.funnel.xmlfeeds

internal fun makeFeedParser(feedType: FeedType): FeedParser {
    return when (feedType) {
        FeedType.ATOM -> AtomParser()
        FeedType.RSS -> RssParser()
    }
}