package ch.guengel.funnel.xmlretriever


import ch.guengel.funnel.xmlretriever.FeedType
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class ParserFactoryKtTest {

    @Test
    fun makeFeedParser() {
        assertTrue(createFeedParser(FeedType.ATOM) is AtomParser)
        assertTrue(createFeedParser(FeedType.RSS) is RssParser)
    }
}