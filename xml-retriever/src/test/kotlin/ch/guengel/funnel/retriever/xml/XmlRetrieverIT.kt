package ch.guengel.funnel.retriever.xml

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import ch.guengel.funnel.feed.bridges.FeedRetrieverException
import ch.guengel.funnel.feed.data.Source
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.mockserver.integration.ClientAndServer
import org.mockserver.integration.ClientAndServer.startClientAndServer
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response

internal class XmlRetrieverIT {
    private val xmlRetriever = XmlRetriever()

    @Test
    fun `happy path`() = runBlocking<Unit> {
        val source = Source("test", "http://localhost:$port/feed")
        val feed = xmlRetriever.fetch(source)
        assertThat(feed.feedItems.size).isEqualTo(1)
    }

    @Test
    fun `no connection`() = runBlocking<Unit> {
        val source = Source("test", "http://localhost:12345/feed")
        assertThat { xmlRetriever.fetch(source) }.isFailure().isInstanceOf(FeedRetrieverException::class)
    }

    @Test
    fun `http 400 error`() = runBlocking<Unit> {
        val source = Source("test", "http://localhost:$port/400")
        assertThat { xmlRetriever.fetch(source) }.isFailure().isInstanceOf(FeedRetrieverException::class)
    }

    @Test
    fun `http 500 error`() = runBlocking<Unit> {
        val source = Source("test", "http://localhost:$port/500")
        assertThat { xmlRetriever.fetch(source) }.isFailure().isInstanceOf(FeedRetrieverException::class)
    }


    companion object {
        private var mockServer: ClientAndServer? = null
        const val port = 1080

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            mockServer = startClientAndServer(port)
            mockServer
                ?.`when`(
                    request()
                        .withMethod("GET")
                        .withPath("/feed")
                )
                ?.respond(
                    response()
                        .withStatusCode(200)
                        .withBody(this::class.java.getResource("/atom-feed.xml").readText())
                )
            mockServer
                ?.`when`(
                    request()
                        .withMethod("GET")
                        .withPath("/400")
                )
                ?.respond(
                    response()
                        .withStatusCode(400)
                )
            mockServer
                ?.`when`(
                    request()
                        .withMethod("GET")
                        .withPath("/500")
                )
                ?.respond(
                    response()
                        .withStatusCode(500)
                )

        }

        @AfterAll
        @JvmStatic
        fun afterAll() {
            mockServer?.stop()
        }
    }
}