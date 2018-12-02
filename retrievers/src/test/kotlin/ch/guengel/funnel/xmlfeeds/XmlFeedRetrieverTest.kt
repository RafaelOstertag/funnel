package ch.guengel.funnel.xmlfeeds

import ch.guengel.funnel.domain.FeedItem
import ch.guengel.funnel.domain.Source
import ch.guengel.funnel.xmlfeeds.network.HttpError
import ch.guengel.funnel.xmlfeeds.network.HttpTransport
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import java.io.File
import java.time.ZonedDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private const val ATOM_MIME_TYPE = "application/atom+xml"
private const val RSS_MIME_TYPE = "application/rss+xml"
private const val CONTENT_TYPE = "Content-Type"

class XmlFeedRetrieverTest {
    private var mockServer: ClientAndServer? = null
    private val atomTestFile: String by lazy {
        File("src/test/resources/atom-feed.xml").readText(Charsets.UTF_8)
    }
    private val rssTestFile: String by lazy {
        File("src/test/resources/sample-rss-2.xml").readText(Charsets.UTF_8)
    }

    @BeforeEach
    fun startMockServer() {
        mockServer = ClientAndServer.startClientAndServer(1080)
    }

    @AfterEach
    fun stopMockServer() {
        mockServer?.stop()
    }

    @Test
    fun `atom happy path`() {
        goodMockFeedServer()

        val httpTransport = HttpTransport(
            Source("test", "http://localhost:1080/atom")
        )
        val atomFeedRetriever = XmlFeedRetriever(httpTransport)

        val result = runBlocking {
            atomFeedRetriever.retrieve(
                ZonedDateTime.parse("2000-01-01T00:00:00Z")
            )
        }

        assertEquals(
            "urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6",
            result.id
        )
        assertEquals("Example Feed", result.title)

        val createdDateTime = ZonedDateTime.parse("2003-12-13T18:30:02Z")
        assertEquals(createdDateTime, result.lastUpdated)

        assertEquals(1, result.feedItems.size)

        val expectedLastUpdated = FeedItem(
            "urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a",
            "Atom-Powered Robots Run Amok",
            createdDateTime
        )
        assertTrue(result.feedItems.hasItem(expectedLastUpdated))
    }

    @Test
    fun `rss happy path`() {
        goodMockFeedServer()

        val httpTransport = HttpTransport(
            Source("test", "http://localhost:1080/rss")
        )
        val atomFeedRetriever = XmlFeedRetriever(httpTransport)

        val result = runBlocking {
            atomFeedRetriever.retrieve(
                ZonedDateTime.parse("2000-01-01T00:00:00Z")
            )
        }

        assertEquals(
            "http://liftoff.msfc.nasa.gov/",
            result.id
        )
        assertEquals("Liftoff News", result.title)

        val createdDateTime = ZonedDateTime.parse("2003-06-03T09:39:21Z")
        assertEquals(createdDateTime, result.lastUpdated)

        assertEquals(4, result.feedItems.size)
    }

    @Test
    fun `feed not found`() {
        feedNotFoundMockFeedServer()

        val httpTransport = HttpTransport(
            Source("test", "http://localhost:1080/atom")
        )

        val atomFeedRetriever = XmlFeedRetriever(httpTransport)
        assertThrows<HttpError> { runBlocking { atomFeedRetriever.retrieve(ZonedDateTime.parse("2001-01-01T00:00:00Z")) } }

    }

    @Test
    fun `connection error`() {
        val httpTransport = HttpTransport(
            Source("test", "http://localhost:65500/atom")
        )

        val atomFeedRetriever = XmlFeedRetriever(httpTransport)
        assertThrows<HttpError> { runBlocking { atomFeedRetriever.retrieve(ZonedDateTime.parse("2001-01-01T00:00:00Z")) } }
    }

    @Test
    fun `invalid content`() {
        invalidContentMockFeedServer()

        val httpTransport = HttpTransport(
            Source("test", "http://localhost:1080/atom")
        )

        val atomFeedRetriever = XmlFeedRetriever(httpTransport)
        assertThrows<CannotDeserializeXML> { runBlocking { atomFeedRetriever.retrieve(ZonedDateTime.parse("2001-01-01T00:00:00Z")) } }
    }

    @Test
    fun `invalid content type`() {
        invalidContentTypeMockFeedServer()

        val httpTransport = HttpTransport(
            Source("test", "http://localhost:1080/atom")
        )

        val atomFeedRetriever = XmlFeedRetriever(httpTransport)
        assertThrows<UnknownFeedType> { runBlocking { atomFeedRetriever.retrieve(ZonedDateTime.parse("2001-01-01T00:00:00Z")) } }
    }

    @Test
    fun `no content type`() {
        noContentTypeMockFeedServer()

        val httpTransport = HttpTransport(
            Source("test", "http://localhost:1080/atom")
        )

        val atomFeedRetriever = XmlFeedRetriever(httpTransport)
        assertThrows<UnknownFeedType> { runBlocking { atomFeedRetriever.retrieve(ZonedDateTime.parse("2001-01-01T00:00:00Z")) } }
    }

    private fun goodMockFeedServer() {
        mockServer
            ?.`when`(
                request()
                    .withMethod("GET")
                    .withPath("/atom")

            )
            ?.respond(
                response()
                    .withStatusCode(200)
                    .withHeader(CONTENT_TYPE, ATOM_MIME_TYPE)
                    .withBody(atomTestFile)
            )

        mockServer
            ?.`when`(
                request()
                    .withMethod("GET")
                    .withPath("/rss")
            )
            ?.respond(
                response()
                    .withStatusCode(200)
                    .withHeader(CONTENT_TYPE, RSS_MIME_TYPE)
                    .withBody(rssTestFile)
            )
    }

    private fun feedNotFoundMockFeedServer() {
        mockServer
            ?.`when`(
                request()
                    .withMethod("GET")
                    .withPath("/atom")
            )
            ?.respond(
                response()
                    .withStatusCode(404)
            )
        mockServer
            ?.`when`(
                request()
                    .withMethod("GET")
                    .withPath("/rss")
            )
            ?.respond(
                response()
                    .withStatusCode(404)
            )
    }

    private fun invalidContentMockFeedServer() {
        mockServer
            ?.`when`(
                request()
                    .withMethod("GET")
                    .withPath("/atom")
            )
            ?.respond(
                response()
                    .withStatusCode(200)
                    .withHeader(CONTENT_TYPE, ATOM_MIME_TYPE)
                    .withBody("Simply text")
            )
        mockServer
            ?.`when`(
                request()
                    .withMethod("GET")
                    .withPath("/rss")
            )
            ?.respond(
                response()
                    .withStatusCode(200)
                    .withHeader(CONTENT_TYPE, RSS_MIME_TYPE)
                    .withBody("Simply text")
            )
    }

    private fun noContentTypeMockFeedServer() {
        mockServer
            ?.`when`(
                request()
                    .withMethod("GET")
                    .withPath("/atom")
            )
            ?.respond(
                response()
                    .withStatusCode(200)
                    .withBody(atomTestFile)
            )

        mockServer
            ?.`when`(
                request()
                    .withMethod("GET")
                    .withPath("/rss")
            )
            ?.respond(
                response()
                    .withStatusCode(200)
                    .withBody(rssTestFile)
            )
    }

    private fun invalidContentTypeMockFeedServer() {
        mockServer
            ?.`when`(
                request()
                    .withMethod("GET")
                    .withPath("/atom")
            )
            ?.respond(
                response()
                    .withStatusCode(200)
                    .withHeader(CONTENT_TYPE, "text/plain")
                    .withBody(atomTestFile)
            )

        mockServer
            ?.`when`(
                request()
                    .withMethod("GET")
                    .withPath("/rss")
            )
            ?.respond(
                response()
                    .withStatusCode(200)
                    .withHeader(CONTENT_TYPE, "text/plain")
                    .withBody(rssTestFile)
            )
    }

}