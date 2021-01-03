package ch.guengel.funnel.retriever.xml

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal class XmlFetcher : AutoCloseable {
    private val client = HttpClient {
        install(HttpPlainText) {
            register(Charsets.UTF_8)
            register(Charsets.ISO_8859_1, quality = 0.1f)
            sendCharset = Charsets.UTF_8
            responseCharsetFallback = Charsets.ISO_8859_1
        }

    }
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