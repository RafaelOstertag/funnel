package ch.guengel.funnel.xmlretriever.network

import ch.guengel.funnel.domain.Source
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.response.HttpResponse
import io.ktor.client.response.readText
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import org.slf4j.LoggerFactory

class HttpTransport(private val source: Source) {
    private var contentFetched: Boolean = false
    private var contentType: String = ""
    private var content: String = ""

    private suspend fun fetchResource() {
        if (contentFetched) {
            logger.debug("Resource ${source.address} already fetched")
            return
        }

        val response = httpGet()
        if (response.status != HttpStatusCode.OK) {
            logger.debug("Fetch failed with HTTP status ${response.status}")
            throw HttpError("Error fetching '${source.name}' from '${source.address}': ${response.status.description}")
        }

        contentType = getContentTypeFromResponse(response)
        logger.debug("Fetched content for ${source.address}")

        content = response.readText()
        logger.debug("Read content for ${source.address}")

        contentFetched = true
    }

    private suspend fun httpGet(): HttpResponse {
        try {
            return httpClient.get(source.address)
        } catch (e: Throwable) {
            throw HttpError("HTTP Error", e)
        }
    }

    private fun getContentTypeFromResponse(response: HttpResponse): String {
        val contentType = response.contentType() ?: return "text/plain"

        return contentType.contentType + "/" + contentType.contentSubtype
    }

    suspend fun retrieve(responseHandler: (contentType: String, content: String) -> Unit) {
        fetchResource()
        responseHandler(contentType, content)
    }

    private companion object {
        val httpClient = HttpClient(CIO)
        val logger = LoggerFactory.getLogger(HttpTransport::class.java)
    }
}

class HttpError(message: String, cause: Throwable? = null) : Exception(message, cause)