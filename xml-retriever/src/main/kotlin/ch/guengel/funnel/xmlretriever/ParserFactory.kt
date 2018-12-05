package ch.guengel.funnel.xmlretriever

internal fun makeFeedParser(feedType: FeedType): FeedParser {
    return when (feedType) {
        FeedType.ATOM -> AtomParser()
        FeedType.RSS -> RssParser()
    }
}