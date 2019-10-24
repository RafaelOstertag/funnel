package ch.guengel.funnel.retriever.xml

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.Url
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal class XmlFetcher : AutoCloseable {
    private val client = HttpClient()
    var closed = false
        private set

    suspend fun fetch(url: Url): String {
        check(!closed) { "client closed" }
        logger.info("Fetch feed from '{}'", url)
        return client.get(url)
    }

    override fun close() {
        client.close()
        closed = true
    }

    private companion object {
        val logger: Logger = LoggerFactory.getLogger(XmlFetcher::class.java)
    }
}