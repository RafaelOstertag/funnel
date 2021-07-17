package ch.guengel.funnel.connector.xmlretriever.http

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import ch.guengel.funnel.feed.bridges.FeedRetrieverException
import ch.guengel.funnel.feed.data.Source
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import kotlinx.coroutines.runBlocking
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.junit.jupiter.api.Test
import javax.inject.Inject

@QuarkusTest
@QuarkusTestResource(WiremockTestResource::class)
internal class XmlRetrieverIT {
    @Inject
    private lateinit var xmlRetriever: XmlRetriever

    @ConfigProperty(name = "wiremock.port")
    private lateinit var port: String

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
}
