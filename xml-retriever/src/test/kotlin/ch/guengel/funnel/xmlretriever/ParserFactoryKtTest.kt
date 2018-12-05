package ch.guengel.funnel.xmlretriever

import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class ParserFactoryKtTest {

    @Test
    fun makeFeedParser() {
        assertTrue(ch.guengel.funnel.xmlretriever.makeFeedParser(FeedType.ATOM) is AtomParser)
        assertTrue(ch.guengel.funnel.xmlretriever.makeFeedParser(FeedType.RSS) is RssParser)
    }
}