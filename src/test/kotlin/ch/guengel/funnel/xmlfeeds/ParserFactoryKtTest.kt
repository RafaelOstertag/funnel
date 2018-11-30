package ch.guengel.funnel.xmlfeeds

import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class ParserFactoryKtTest {

    @Test
    fun makeFeedParser() {
        assertTrue(ch.guengel.funnel.xmlfeeds.makeFeedParser(FeedType.ATOM) is AtomParser)
        assertTrue(ch.guengel.funnel.xmlfeeds.makeFeedParser(FeedType.RSS) is RssParser)
    }
}