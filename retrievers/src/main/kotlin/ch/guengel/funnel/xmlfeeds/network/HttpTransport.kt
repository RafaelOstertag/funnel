package ch.guengel.funnel.xmlfeeds.network

import ch.guengel.funnel.domain.Source
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.response.HttpResponse
import io.ktor.client.response.readText
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class HttpTransport(private val source: Source) {
    private var contentFetched: Boolean = false
    private var contentType: String = ""
    private var content: String = ""
  
    private fun fetchResource() {
        if (contentFetched) {
            return
        }

        val response = httpGet()

        if (response.status != HttpStatusCode.OK) {
            throw HttpError("Error fetching '${source.name}' from '${source.address}': ${response.status.description}")
        }

        content = runBlocking(Dispatchers.IO) {
            response.readText()
        }

        contentFetched = true
    }

    private fun httpGet(): HttpResponse {
        try {
            return runBlocking(Dispatchers.IO) {
                val response = httpClient.get<HttpResponse>(source.address)
                contentType = getContentTypeFromResponse(response)

                response
            }
        } catch (e: Throwable) {
            throw HttpError("HTTP Error", e)
        }
    }

    private fun getContentTypeFromResponse(response: HttpResponse): String {
        val contentType = response.contentType() ?: return "text/plain"

        return contentType.contentType + "/" + contentType.contentSubtype
    }

    fun contentType(): String {
        fetchResource()
        return contentType
    }

    fun response(): String {
        fetchResource()
        return content
    }

    private companion object {
        val httpClient = HttpClient(CIO)
    }
}

class HttpError(message: String, cause: Throwable? = null) : Exception(message, cause)